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

import be.ac.ua.ansymo.cheopsj.changerecorders.LocalVariableRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodInvocationRecorder;
import be.ac.ua.ansymo.cheopsj.distiller.cd.ChangeDistillerProxy;
import be.ac.ua.ansymo.cheopsj.distiller.svnconnection.SVNConnector;
import be.ac.ua.ansymo.cheopsj.distiller.svnconnection.SVNLogEntryHandler;
import be.ac.ua.ansymo.cheopsj.logger.astdiffer.ASTComparator;
import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;


public class DistillChanges implements IObjectActionDelegate {

	private IProject selectedProject;
	private SVNConnector svnConnector;
	
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
			svnConnector = new SVNConnector("", "");
			svnConnector.initialize();

			//long rev = svnConnector.getCurrentRevision(file);//Get current revision number.
			//long targetRev = svnConnector.getHeadRevisionNumber(file);//total nr of revisions
			//long targetRev = 1000;
			
			long rev = 0;
			//Get current revision number.
			long targetRev = svnConnector.getCurrentRevision(file);
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
					SVNLogEntryHandler entryHandler = new SVNLogEntryHandler();
					svnConnector.getCommitMessage(file, rev + 1, entryHandler); //Lookahead at changes in next revision!

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

	private void extractChangesFromJavaFiles(long rev,
			SVNLogEntryHandler entryHandler, Iterator<?> it) throws Exception {
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
				String addedFileContents = svnConnector.getFileContents(path, rev + 1);
				extractor.storeClassAddition(addedFileContents);
				extractor.storeFieldAdditions(addedFileContents);
				extractor.storeMethodAdditions(addedFileContents);
				extractor.storeMethodInvocationAdditions(addedFileContents);
				break;
			case SVNLogEntryPath.TYPE_DELETED:
				//System.out.println("DELETED: " + path);
				//This file was removed --> create remove changes for everything in this file!
				String removedFileContents = svnConnector.getFileContents(path, rev);
				
				extractor.storeMethodInvocationRemovals(removedFileContents);
				extractor.storeMethodRemoval(removedFileContents);
				extractor.storeFieldRemovals(removedFileContents);
				extractor.storeClassRemoval(removedFileContents);
				break;
			case SVNLogEntryPath.TYPE_MODIFIED:
				//System.out.println("CHANGED: " + path);									
				//This file was modified --> run evolizer.ChangeDistiller on old and new files to find out differences.
				String targetFileContents = svnConnector.getFileContents(path, rev + 1); 
				String sourceFileContents = svnConnector.getFileContents(path, rev);  
				
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
		svnConnector.updateToRevision(file, rev + 1, monitor);
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
