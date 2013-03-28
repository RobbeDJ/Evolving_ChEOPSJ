package be.ac.ua.ansymo.cheopsj.distiller.popup.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class LocalVariableFinder extends ASTVisitor  {
	
	private List<VariableDeclaration> vars;
	
	public LocalVariableFinder() {
		super();
		vars = new ArrayList<VariableDeclaration>();
	}

	public List<VariableDeclaration> getFoundVariables() {
		return vars;
	}
	
	public boolean visit(TypeDeclaration node){
		//Don't visit the childnodes when a nested class is declared inside a method.
		return false;
	}
	
	/*public boolean visit(MethodDeclaration node){
		return false;
	}*/
	
	public boolean visit(VariableDeclarationFragment node){
		//VariableDeclarationFragments are used in local variabel declaration statements and For statement initializers
		vars.add(node);
		return true;
	}
	
	public boolean visit(SingleVariableDeclaration node){
		//SingleVariableDeclarations are used in catch clauses and formal parameter lists.
		vars.add(node);
		return true;
	}
}
