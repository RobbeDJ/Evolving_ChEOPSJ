package be.ac.ua.ansymo.cheopsj.distiller.popup.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.svn.core.SVNTeamProvider;

import be.ac.ua.ansymo.cheopsj.changerecorders.LocalVariableRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodInvocationRecorder;
import be.ac.ua.ansymo.cheopsj.distiller.cd.ChangeDistillerProxy;
import be.ac.ua.ansymo.cheopsj.distiller.scmconnection.SCMConnector;
import be.ac.ua.ansymo.cheopsj.distiller.scmconnection.SCMLogEntryHandler;
import be.ac.ua.ansymo.cheopsj.distiller.svnconnection.SVNConnector;
import be.ac.ua.ansymo.cheopsj.distiller.svnconnection.SVNLogEntryHandler;
import be.ac.ua.ansymo.cheopsj.logger.astdiffer.ASTComparator;
import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;


public class DistillChanges implements IObjectActionDelegate {

	private IProject selectedProject;
	private IProject testSelectedProject;
	private SCMConnector scmConnector;
	private String repositoryUrl = "";
	private String repositoryUser = "";
	private String repositoryPass = "";
	private SupportedSCMEnum scms;
	
	public DistillChanges() {
	}
	
	private IProject getProjectForSelection(ISelection selection){
		if(selection == null){ return null; }
		Object selectedElement = ((IStructuredSelection)selection).getFirstElement();
				
		if (selectedElement instanceof IProject) {
			return (IProject) selectedElement;
		} else if (selectedElement instanceof IJavaProject){
			return ((IJavaProject) selectedElement).getProject();
		}
		return null;
	}
	
	private void getSelectedProject(){
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		ISelectionService selectionService = window.getSelectionService();
		ISelection selection = selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		selectedProject = getProjectForSelection(selection);
		if (selectedProject == null) {
			selectedProject = testSelectedProject;
		}
	}
	
	private Shell getShell() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getShell();
	}

	/*private IResource getProjectForSelection(ISelection selection){
		if(selection == null){ return null; }
		Object selectedElement = ((IStructuredSelection)selection).getFirstElement();

		if (selectedElement instanceof IJavaProject){
			return ((IJavaProject) selectedElement).getProject();
		} else if (selectedElement instanceof IFolder){
			return (IFolder) selectedElement;
		}

		return null;
	}

	private void getSelectedProject(){
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		ISelectionService selectionService = window.getSelectionService();
		ISelection selection = selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		selectedResource = getProjectForSelection(selection);
	}

	private Shell getShell() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getShell();
	}*/

	@Override
	public void run(IAction action) {
		getSelectedProject();
		//TODO: figger out correct repository link and scm system
		/**
		 * maybe make a menu with an string input for the repository and a combobox for the scm system
		 * also add user and pass if read is not public
		 */
		//these checks are necessary to allow tests to set the repository before running
		if (repositoryUrl == null || repositoryUrl == "") {
			repositoryUrl = "https://subversion.assembla.com/svn/cheopsj-test/";
		}
		if (scms == null) {
			scms = SupportedSCMEnum.SVN;
		}
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					iterateRevisions(monitor);
				}
			});
		} catch (InterruptedException e) {

		} catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			ErrorDialog.openError(getShell(), "TargetError",
					"Error Occured While Running Experiment", new Status(0,
							"MetricsExperiment", 0, "no message", target));
		}
	}
		
	private void iterateRevisions(IProgressMonitor monitor){
		try {			
			//TODO record additions for initial project!
			File file = new File(selectedProject.getLocationURI());

            scmConnector = getConnector();

			
			//svnConnector = new SVNConnector("", "");
			scmConnector.initialize();

			//long rev = svnConnector.getCurrentRevision(file);//Get current revision number.
			//long targetRev = svnConnector.getHeadRevisionNumber(file);//total nr of revisions
			//long targetRev = 1000;
			
			long rev = 0;
			//Get current revision number.
			long targetRev = scmConnector.getCurrentRevision(file);
			//long targetRev = 4;
			int diff = (int) (targetRev - rev); //nr of revisions that will be processed
			monitor.beginTask("Extracting changes", diff);
			while(rev < targetRev){
				try{
					//update to revision rev
					//svnConnector.updateToRevision(file, rev, new SubProgressMonitor(monitor, 1));
					//updateOneRev(file, rev, new SubProgressMonitor(monitor, 1));//update working copy of svn rep one revision
					//refresh project!
					//selectedProject.refreshLocal(IProject.DEPTH_INFINITE, new SubProgressMonitor(monitor, 1));
					
					double percent = ((double)rev/targetRev)*100;
					monitor.subTask("from revision: " + rev + "/" + targetRev + " (" +(int)percent+ "%)");
					SCMLogEntryHandler entryHandler = getHandler();
					scmConnector.getCommitMessage(file, rev + 1, entryHandler); //Lookahead at changes in next revision!

					Map<?, ?> changedPaths = entryHandler.getChangedPaths();
					
					if(changedPaths != null){
						Iterator<?> it = changedPaths.entrySet().iterator();
						while (it.hasNext()) {
							//iterate changed files
							try{
								extractChangesFromJavaFiles(rev, entryHandler, it);
							}catch(Exception e){
								//ignore and try next file!!!
							}
						}
					}
					monitor.worked(1);
					rev++;
				}catch(Exception e){
					e.printStackTrace();
					//break;//if exception occurred: leave loop
					rev++; //if exception occurred, go to next revision
					//make a note of revision where exception occurred, data may be faulty
				}
				if(monitor.isCanceled()){
					break; //if cancel was pressed leave loop
				}
			}
			//svnConnector.updateToRevision(file, rev, monitor);
			monitor.done();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private SCMLogEntryHandler getHandler() {
		SCMLogEntryHandler handler;
		switch (scms) {
			case SVN:
				handler = new SVNLogEntryHandler();
				return handler;
			default:
				return null;
		}
	}
	private SCMConnector getConnector() {
		SCMConnector connector;
		switch (scms) {
			case SVN:
				connector = new SVNConnector(repositoryUrl, repositoryUser, repositoryPass);
				return connector;
			default:
				return null;
		}
	}

	private void extractChangesFromJavaFiles(long rev,
			SCMLogEntryHandler entryHandler, Iterator<?> it) throws Exception {
		@SuppressWarnings("rawtypes")
		Map.Entry pairs = (Map.Entry)it.next();
		String path = (String)pairs.getKey();
		if(path.endsWith(".java")){
			//only look at java source files

			SVNLogEntryPath logEntry = (SVNLogEntryPath)pairs.getValue();
			ChangeExtractor extractor = new ChangeExtractor(entryHandler.getMessage(), entryHandler.getDate(), entryHandler.getUser());
			switch(logEntry.getType()){
			case SVNLogEntryPath.TYPE_ADDED: 
				//System.out.println("ADDED: " + path);
				//This file was added --> create addition changes for everything in this file!									
				String addedFileContents = scmConnector.getFileContents(path, rev + 1);
				extractor.storeClassAddition(addedFileContents);
				extractor.storeFieldAdditions(addedFileContents);
				extractor.storeMethodAdditions(addedFileContents);
				extractor.storeMethodInvocationAdditions(addedFileContents);
				break;
			case SVNLogEntryPath.TYPE_DELETED:
				//System.out.println("DELETED: " + path);
				//This file was removed --> create remove changes for everything in this file!
				String removedFileContents = scmConnector.getFileContents(path, rev);
				
				extractor.storeMethodInvocationRemovals(removedFileContents);
				extractor.storeMethodRemoval(removedFileContents);
				extractor.storeFieldRemovals(removedFileContents);
				extractor.storeClassRemoval(removedFileContents);
				break;
			case SVNLogEntryPath.TYPE_MODIFIED:
				//System.out.println("CHANGED: " + path);									
				//This file was modified --> run evolizer.ChangeDistiller on old and new files to find out differences.
				String targetFileContents = scmConnector.getFileContents(path, rev + 1); 
				String sourceFileContents = scmConnector.getFileContents(path, rev);  
				
				ChangeDistillerProxy cd = new ChangeDistillerProxy();
				cd.copyOldFileFrom(sourceFileContents);
				cd.copyNewFileFrom(targetFileContents);
				cd.performDistilling();
				//Convert cd changes into cheops changes
				if(cd.getSourceCodeChanges() != null && !cd.getSourceCodeChanges().isEmpty())
					extractor.convertChanges(cd.getSourceCodeChanges());
				
				
				ASTParser parser = ASTParser.newParser(AST.JLS3);
				parser.setSource(sourceFileContents.toString().toCharArray());
				CompilationUnit oldAST = (CompilationUnit) parser.createAST(null);
				
				parser.setSource(targetFileContents.toString().toCharArray());
				CompilationUnit newAST = (CompilationUnit) parser.createAST(null);
				
				ASTComparator differ = new ASTComparator();
				differ.setSource(oldAST);
				differ.setTarget(newAST);
				differ.diff();

				Collection<ASTNode> added = differ.getAddedElements();
				Collection<ASTNode> removed = differ.getRemovedElements();

				for (ASTNode node : added) {
					storeChange(node, new Add());
				}

				for (ASTNode node : removed) {
					storeChange(node, new Remove());
				}
				
				
				break;
			default: break;
			}
		}
	}
	
	private void storeChange(ASTNode node, IChange change) {
		if (node instanceof MethodInvocation) {
			new MethodInvocationRecorder((MethodInvocation) node).storeChange(change);
		}else if (node instanceof VariableDeclaration){
			//This is to get changes to other local vars.
			//new LocalVariableRecorder((VariableDeclaration) node).storeChange(change);
		}
	}
	
	private void updateOneRev(File file, long rev, IProgressMonitor monitor) throws CoreException{
		scmConnector.updateToRevision(file, rev + 1, monitor);
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
	public void setSelectedProject(IProject project) {
		testSelectedProject = project;
	}
	public void setRepositoryUrl(String url) {
		repositoryUrl = url;
	}	
	public void setRepositoryInfo(String url, String user, String pass) {
		repositoryUrl = url;
		repositoryUser = user;
		repositoryPass = pass;
	}
	public void setSCMSystem(SupportedSCMEnum scm) {
		scms = scm;
	}
}
