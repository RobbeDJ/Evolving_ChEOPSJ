package be.ac.ua.ansymo.cheopsj.changerecorders;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Initializer;

public abstract class StatementRecorder extends AbstractEntityRecorder {

	protected String containingMethodName;

	public StatementRecorder() {
		super();
	}

	/**
	 * @param node2
	 * @return
	 */
	protected String getContainingMethod(ASTNode node) {
		//Go up till we find a MethodDeclaration, that's the method our invocation is declared in!
		if (node instanceof MethodDeclaration) {
			return getContainingClass(node) + '.' + ((MethodDeclaration) node).getName().getIdentifier();
		} else {
			if(!(node instanceof CompilationUnit))
				return getContainingMethod(node.getParent());
			else
				return "";
		}
	}

	private String getContainingClass(ASTNode node) {
		//Go up till we find a class, that's the class our invoking method is declared in!
		//TODO what if the parent is not a package but another class???
		if (node instanceof TypeDeclaration) {
			if(node.getParent() instanceof TypeDeclaration){
				return getContainingClass(node.getParent()) + "." + ((TypeDeclaration) node).getName().getFullyQualifiedName();
			}else{
				return getPackageDeclaration(node) + '.' + ((TypeDeclaration) node).getName().getFullyQualifiedName();
			}
		} else {
			return getContainingClass(node.getParent());
		}
		
	}

	private String getPackageDeclaration(ASTNode node) {
		if (node instanceof CompilationUnit) {
			if (((CompilationUnit) node).getPackage() != null) {
				return ((CompilationUnit) node).getPackage().getName().getFullyQualifiedName();
			} else {
				return "defaultpackage";
			}
		} else {
			return getPackageDeclaration(node.getParent());
		}
	}

}