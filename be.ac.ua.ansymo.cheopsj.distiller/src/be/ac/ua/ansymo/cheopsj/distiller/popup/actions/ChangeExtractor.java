package be.ac.ua.ansymo.cheopsj.distiller.popup.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeChange;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;

import be.ac.ua.ansymo.cheopsj.changerecorders.ClassRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.FieldRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.LocalVariableRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodInvocationRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.PackageRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.StatementRecorder;
import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;

public class ChangeExtractor {

	private String changeIntent;
	private Date changeDate;
	private String changeUser;

	public ChangeExtractor(String message, Date date, String user) {
		changeIntent = message;
		changeDate = date;
		changeUser = user;
	}

	//#############################
	//###### CHANGEDISTILLER ######
	//#############################

	public void convertChanges(List<SourceCodeChange> sourceCodeChanges) {
		for(SourceCodeChange scc: sourceCodeChanges) {
			SourceCodeEntity entity = scc.getChangedEntity();

			switch(scc.getChangeType()){
			case ADDITIONAL_CLASS:
				if(entity.getType().isClass()){
					ClassRecorder recorder = new ClassRecorder(entity, scc.getParentEntity());
					recorder.storeChange(createAddition());
				}
				break;
			case REMOVED_CLASS:
				if(entity.getType().isClass()){
					ClassRecorder recorder = new ClassRecorder(entity, scc.getParentEntity());
					recorder.storeChange(createRemoval());
				}
				break;
			case ADDITIONAL_FUNCTIONALITY:
				//ADDED METHOD
				if(entity.getType().isMethod()){
					MethodRecorder recorder = new MethodRecorder(entity, scc.getParentEntity());
					recorder.storeChange(createAddition());
				}
				break;
			case REMOVED_FUNCTIONALITY:
				//REMOVED METHOD
				if(entity.getType().isMethod()){
					MethodRecorder recorder = new MethodRecorder(entity, scc.getParentEntity());
					recorder.storeChange(createRemoval());
				}
				break;
			case ADDITIONAL_OBJECT_STATE:
				//ADDED ATTRIBUTE
				if(entity.getType().isField()){
					FieldRecorder recorder = new FieldRecorder(entity, scc.getParentEntity());
					recorder.storeChange(createAddition());
				}
				break;
			case REMOVED_OBJECT_STATE:
				//REMOVED ATTRIBUTE
				if(entity.getType().isField()){
					FieldRecorder recorder = new FieldRecorder(entity, scc.getParentEntity());
					recorder.storeChange(createRemoval());
				}
				break;
			/*case STATEMENT_INSERT:
				//ADDED STATEMENT -> figure out what kind of statement (i.e., method invoke or variable access)
				if(entity.getType() == JavaEntityType.METHOD_INVOCATION){
					//ADDED METHOD INVOCATION
				}
				break;
			case STATEMENT_DELETE:
				//REMOVED STATEMENT -> figure out what kind of statement
				if(entity.getType() == JavaEntityType.METHOD_INVOCATION){
					//REMOVED METHOD INVOCATION
				}
				break;	*/
			}
		}
	}

	//#############################
	//######## REMOVALS ###########
	//#############################

	public void storeMethodInvocationRemovals(String contents) {
		TypeDeclaration bigType = getDeclaredType(contents);
		if(bigType != null){
			MethodDeclaration[] methods = bigType.getMethods();
			for(MethodDeclaration method : methods){
				MIVisitor visitor = new MIVisitor();
				method.accept(visitor);
				for(MethodInvocation invocation: visitor.getMethodInvocations()){
					StatementRecorder recorder = new MethodInvocationRecorder(invocation);
					recorder.storeChange(createRemoval());
				}
			}
		}
	}

	public void storeMethodRemoval(String contents) {
		TypeDeclaration bigType = getDeclaredType(contents);
		if(bigType != null){
			MethodDeclaration[] methods = bigType.getMethods();
			for(MethodDeclaration method : methods){
				MethodRecorder recorder = new MethodRecorder(method);
				recorder.storeChange(createRemoval());
			}	
		}
	}

	public void storeFieldRemovals(String contents) {
		TypeDeclaration bigType = getDeclaredType(contents);
		if(bigType != null){
			FieldDeclaration[] fields = bigType.getFields();
			for(FieldDeclaration field : fields){
				FieldRecorder recorder = new FieldRecorder(field);
				recorder.storeChange(createRemoval());
			}
		}
	}

	public void storeClassRemoval(String contents) {
		CompilationUnit cu = getASTFromString(contents);
		List<?> types = cu.types();
		TypeDeclaration bigType = (TypeDeclaration) types.get(0);

		//TODO deal with package removal;
		PackageDeclaration pack = cu.getPackage();
		storePackageRemoval(pack);

		ClassRecorder recorder = new ClassRecorder(bigType);
		recorder.storeChange(createRemoval());
	}

	private void storePackageRemoval(PackageDeclaration pack) {
		// TODO Auto-generated method stub
		//Somehow find out how many types are declared in this Package.

	}

	//#############################
	//######## ADDITIONS ##########
	//#############################

	public void storeFieldAdditions(String contents) {
		TypeDeclaration bigType = getDeclaredType(contents);
		if(bigType != null){
			FieldDeclaration[] fields = bigType.getFields();
			for(FieldDeclaration field : fields){
				FieldRecorder recorder = new FieldRecorder(field);
				recorder.storeChange(createAddition());
			}
			
			TypeDeclaration[] localtypes = bigType.getTypes();
			for(TypeDeclaration type: localtypes){
				FieldDeclaration[] subfields = type.getFields();
				for(FieldDeclaration field : subfields){
					FieldRecorder recorder = new FieldRecorder(field);
					recorder.storeChange(createAddition());
				}
				
			}
		}
	}

	public void storeMethodAdditions(String contents) {
		TypeDeclaration bigType = getDeclaredType(contents);
		if(bigType != null){
			MethodDeclaration[] methods = bigType.getMethods();
			for(MethodDeclaration method : methods){
				MethodRecorder recorder = new MethodRecorder(method);
				recorder.storeChange(createAddition());
				//storeLocalVariableAdditions(method);
			}			
			
			TypeDeclaration[] localtypes = bigType.getTypes();
			for(TypeDeclaration type: localtypes){
				MethodDeclaration[] submethods = type.getMethods();
				for(MethodDeclaration method : submethods){
					MethodRecorder recorder = new MethodRecorder(method);
					recorder.storeChange(createAddition());
					//storeLocalVariableAdditions(method);
				}
				
			}
		}
	}

	public void storeLocalVariableAdditions(MethodDeclaration method){
		//List<SingleVariableDeclaration> params = method.parameters();
		LocalVariableFinder finder = new LocalVariableFinder();
		//method.getBody().accept(finder);
		method.accept(finder);

		List<VariableDeclaration> vars = finder.getFoundVariables();		
		//vars.addAll(params);

		for(VariableDeclaration var: vars){
			LocalVariableRecorder recorder = new LocalVariableRecorder(var);
			recorder.storeChange(createAddition());
		}
	}

	public void storeMethodInvocationAdditions(String contents) {
		TypeDeclaration bigType = getDeclaredType(contents);
		if(bigType != null){
			MethodDeclaration[] methods = bigType.getMethods();
			for(MethodDeclaration method : methods){
				MIVisitor visitor = new MIVisitor();
				method.accept(visitor);
				for(MethodInvocation invocation: visitor.getMethodInvocations()){
					StatementRecorder recorder = new MethodInvocationRecorder(invocation);
					recorder.storeChange(createAddition());
				}
			}
		}
	}

	public void storeClassAddition(String contents) {
		CompilationUnit cu = getASTFromString(contents);
		List<?> types = cu.types();
		if(types.get(0) instanceof TypeDeclaration){
			//TODO are there any more types in this list?
			TypeDeclaration bigType = (TypeDeclaration) types.get(0);

			PackageDeclaration pack = cu.getPackage();
			storePackageAddition(pack);

			ClassRecorder recorder = new ClassRecorder(bigType);
			recorder.storeChange(createAddition());
			
			TypeDeclaration[] localtypes = bigType.getTypes();
			for(TypeDeclaration type: localtypes){
				ClassRecorder subrec = new ClassRecorder(type);
				subrec.storeChange(createAddition());
			}
			
		}
	}

	private TypeDeclaration getDeclaredType(String contents){
		CompilationUnit cu = getASTFromString(contents);
		List<?> types = cu.types();
		if(types.get(0) instanceof TypeDeclaration){
			return (TypeDeclaration) types.get(0);
		}else
			return null;
	}

	private void storePackageAddition(PackageDeclaration pack) {
		//only create packageaddition, if this package was not previously created
		if(pack != null){
			if(!ModelManager.getInstance().famixPackageExists(pack.getName().getFullyQualifiedName())){
				PackageRecorder recorder = new PackageRecorder(pack);
				recorder.storeChange(createAddition());
			}
		}

	}

	//#############################
	//########## UTILS ############
	//#############################

	private static final java.sql.Timestamp utilDateToSqlTimestamp(java.util.Date utilDate) {
		return new java.sql.Timestamp(utilDate.getTime());
	}

	private CompilationUnit getASTFromString(String str) {
		CompilationUnit cu = null;

		ASTParser parser = ASTParser.newParser(AST.JLS3);

		parser.setSource(str.toString().toCharArray());
		cu = (CompilationUnit) parser.createAST(null);

		return cu;
	}

	private AtomicChange createAddition() {
		AtomicChange add = new Add();
		add.setIntent(changeIntent);
		add.setTimeStamp(utilDateToSqlTimestamp(changeDate));
		add.setUser(changeUser);
		return add;
	}

	private Remove createRemoval() {
		Remove rem = new Remove();
		rem.setIntent(changeIntent);
		rem.setTimeStamp(utilDateToSqlTimestamp(changeDate));
		rem.setUser(changeUser);
		return rem;
	}

	private class MIVisitor extends ASTVisitor {

		private List<MethodInvocation> invocations = new ArrayList<MethodInvocation>();

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
