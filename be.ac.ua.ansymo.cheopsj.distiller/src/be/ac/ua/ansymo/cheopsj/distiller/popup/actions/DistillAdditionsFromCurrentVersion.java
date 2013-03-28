package be.ac.ua.ansymo.cheopsj.distiller.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
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

import be.ac.ua.ansymo.cheopsj.changerecorders.ClassRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.FieldRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodInvocationRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.PackageRecorder;
import be.ac.ua.ansymo.cheopsj.distiller.svnconnection.SVNConnector;
import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;

public class DistillAdditionsFromCurrentVersion implements IObjectActionDelegate {

	private IResource selectedResource;
	private IJavaProject javaProject;
	private SVNConnector svnConnector;


	private IResource getProjectForSelection(ISelection selection){
		if(selection == null){ return null; }
		Object selectedElement = ((IStructuredSelection)selection).getFirstElement();

		if (selectedElement instanceof IJavaProject){
			javaProject = (IJavaProject) selectedElement;
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
	}

	@Override
	public void run(IAction action) {
		getSelectedProject();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					makeAdditions(monitor);
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

	private void makeAdditions(IProgressMonitor monitor){

		if(javaProject instanceof IJavaProject){
			try{
				IPackageFragment[] packages = javaProject.getPackageFragments();
				makePackageAdditions(packages);
				makeClassAdditions(packages);
				makeClassMemberAdditions(packages);
				makeLocalVarAdditions(packages);
				makeInvocationAdditions(packages);
			}catch(JavaModelException e){

			}
		}	

	}

	private void makeInvocationAdditions(IPackageFragment[] packages) throws JavaModelException {
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				//store additions for each class inside the package.
				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					
					CompilationUnit parse = parse(unit);
					MIVisitor visitor = new MIVisitor();
					parse.accept(visitor);
					for(MethodInvocation invocation : visitor.getMethodInvocations()){
						MethodInvocationRecorder recorder = new MethodInvocationRecorder(invocation);
						recorder.storeChange(new Add());
					}
				}
			}
		}

	}

	private  CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}


	private void makeLocalVarAdditions(IPackageFragment[] packages) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	private void makeClassMemberAdditions(IPackageFragment[] packages) throws JavaModelException  {
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				//store additions for each class inside the package.
				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					IType[] allTypes = unit.getAllTypes();
					//store additions of type
					for(IType type: allTypes){
						IMethod[] methods = type.getMethods();
						IField[] fields = type.getFields();

						for(IMethod method: methods){
							String classname = type.getFullyQualifiedName();
							String methodname = method.getElementName();
							if(!ModelManager.getInstance().famixMethodExists(classname + '.' + methodname)){
								MethodRecorder recorder = new MethodRecorder(method);
								recorder.storeChange(new Add());
							}		
						}

						for(IField field: fields){
							String classname = type.getFullyQualifiedName();
							String fieldname = field.getElementName();
							if(!ModelManager.getInstance().famixFieldExists(classname + '.' + fieldname)){
								FieldRecorder recorder = new FieldRecorder(field);
								recorder.storeChange(new Add());
							}
						}
						//and store any nested classes
						makeNestedClassMemberAdditions(type);
					}			
				}
			}
		}

	}

	private void makeNestedClassMemberAdditions(IType type) throws JavaModelException {
		IType[] membertypes = type.getTypes();
		for(IType member: membertypes){
			IMethod[] methods = member.getMethods();
			IField[] fields = member.getFields();

			for(IMethod method: methods){
				String classname = member.getFullyQualifiedName();
				String methodname = method.getElementName();
				if(!ModelManager.getInstance().famixMethodExists(classname + '.' + methodname)){
					MethodRecorder recorder = new MethodRecorder(method);
					recorder.storeChange(new Add());
				}
			}

			for(IField field: fields){
				String classname = member.getFullyQualifiedName();
				String fieldname = field.getElementName();
				if(!ModelManager.getInstance().famixFieldExists(classname + '.' + fieldname)){
					FieldRecorder recorder = new FieldRecorder(field);
					recorder.storeChange(new Add());
				}
			}
			//and store any nested classes
			makeNestedClassAdditions(member);
		}
	}

	private void makeClassAdditions(IPackageFragment[] packages) throws JavaModelException {
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				//store additions for each class inside the package.
				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					IType[] allTypes = unit.getAllTypes();
					//store additions of type
					for(IType type: allTypes){
						String uniqueName = type.getFullyQualifiedName();
						uniqueName = uniqueName.replace('$', '.');
						if(!ModelManager.getInstance().famixClassExists(uniqueName)){
							ClassRecorder recorder = new ClassRecorder(type);
							recorder.storeChange(new Add());
						}
						//and store any nested classes
						makeNestedClassAdditions(type);
					}			
				}
			}
		}
	}

	private void makeNestedClassAdditions(IType type) throws JavaModelException {
		IType[] membertypes = type.getTypes();
		for(IType member: membertypes){
			String uniqueName = type.getFullyQualifiedName();
			uniqueName = uniqueName.replace('$', '.');
			if(!ModelManager.getInstance().famixClassExists(uniqueName)){
				ClassRecorder recorder = new ClassRecorder(member);
				recorder.storeChange(new Add());
			}
			//and store any nested classes
			makeNestedClassAdditions(member);
		}
	}

	private void makePackageAdditions(IPackageFragment[] packages) throws JavaModelException {
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				//store additions for each pacakge.
				if(!ModelManager.getInstance().famixPackageExists(mypackage.getElementName())){

					PackageRecorder recorder = new PackageRecorder(mypackage);
					recorder.storeChange(new Add());
				}
			}
		}
	}

	/*
	private void makeAdditions(IProgressMonitor monitor){
		File file = new File(selectedResource.getLocationURI());
		//iterate all java files in the selected project!
		recursiveTraversal(file,monitor);
	}

	public void recursiveTraversal(File fileObject,IProgressMonitor monitor) {
		if(fileObject.getAbsolutePath().contains(".svn"))
			return;

		if(monitor.isCanceled()){
			return; //if cancel was pressed leave loop
		}

		if (fileObject.isDirectory()) {
			File allFiles[] = fileObject.listFiles();
			for (File aFile : allFiles) {
				if(monitor.isCanceled()){
					return; //if cancel was pressed leave loop
				}
				recursiveTraversal(aFile,monitor);
			}
		} else if (fileObject.isFile()) {
			//System.out.println(fileObject.getAbsolutePath());

			if(fileObject.getAbsolutePath().endsWith(".java")){

				Calendar calendar = Calendar.getInstance();
				java.util.Date now = calendar.getTime();
				ChangeExtractor extractor = new ChangeExtractor("none", now, "noob");

				String addedFileContents = FileUtils.getContent(fileObject);
				extractor.storeClassAddition(addedFileContents);
				extractor.storeFieldAdditions(addedFileContents);
				extractor.storeMethodAdditions(addedFileContents);
				extractor.storeMethodInvocationAdditions(addedFileContents);
			}
		}
	}*/

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}


	private class MIVisitor extends ASTVisitor {
		private List<MethodInvocation> invocations = new ArrayList<MethodInvocation>();

		@Override
		public boolean visit(EnumDeclaration node){
			//Ignore Enum's
			return false;
		}
		
		@Override
		public boolean visit(MethodInvocation node) {
			invocations.add(node);
			return true;
		}

		public List<MethodInvocation> getMethodInvocations() {
			return invocations;			
		}
	}

}
