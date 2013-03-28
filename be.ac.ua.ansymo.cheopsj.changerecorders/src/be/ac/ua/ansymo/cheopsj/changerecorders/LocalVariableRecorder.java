/*******************************************************************************
 * Copyright (c) 2011 Quinten David Soetens
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Quinten David Soetens - initial API and implementation
 ******************************************************************************/

package be.ac.ua.ansymo.cheopsj.changerecorders;

import java.util.List;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;

/**
 * @author quinten
 *
 */
public class LocalVariableRecorder extends StatementRecorder {

	private String uniquename = "";
	//private String typeuniquename = "";
	
    private ModelManager manager;
    private FamixMethod containingMethod;
    private FamixLocalVariable variable;
    private FamixClass declaredClass;
    private String name = "";
	
    private LocalVariableRecorder(){
    	manager = ModelManager.getInstance();
    }
    
	//TODO variables can hide one another if they have the same name, need to somehow encode the nesting depth or scope in the unique name!
	public LocalVariableRecorder(VariableDeclaration node){
		this();
		uniquename = node.getName().getFullyQualifiedName();
		name = node.getName().getIdentifier();
		containingMethodName = getContainingMethod(node);
		uniquename = containingMethodName + '{' + uniquename + '}';
		
		declaredClass = findDeclaredClass(node);
		if(manager.famixMethodExists(containingMethodName)){
			containingMethod = manager.getFamixMethod(containingMethodName);
		}
	}
	
	private FamixClass findDeclaredClass(VariableDeclaration node) {
		if(node instanceof SingleVariableDeclaration){
			SingleVariableDeclaration sd =  (SingleVariableDeclaration)node;
			return findDeclaredClass(sd.getType());
		}else if(node instanceof VariableDeclarationFragment){
			VariableDeclarationFragment df = (VariableDeclarationFragment)node;
			if (node.getParent() instanceof VariableDeclarationExpression){
				return findDeclaredClass(((VariableDeclarationExpression)node.getParent()).getType());
			}else if (node.getParent() instanceof VariableDeclarationStatement){
				return findDeclaredClass(((VariableDeclarationStatement)node.getParent()).getType());
			}
		}
		return null;
	}
	
	private FamixClass findDeclaredClass(Type type){
		String declaredClassName = "";
		if (type.isSimpleType()) {
			declaredClassName = ((SimpleType) type).getName().getFullyQualifiedName();
			
			//TODO first need to go up all classlevels to see if there is a typemember in the class with this name
			
			//THEN Search for fully qualified classname in import statments
			CompilationUnit cu = (CompilationUnit)type.getRoot();
			List<ImportDeclaration> imports = cu.imports();
			for(ImportDeclaration imp : imports){
				Name impname = imp.getName();
				if(impname.getFullyQualifiedName().endsWith(declaredClassName)){
					declaredClassName = impname.getFullyQualifiedName();
					break;
				}
			}
			
			//if still not found there, need to assume that the class is in same package as this class.
			if(!declaredClassName.contains(".")){
				declaredClassName = cu.getPackage().getName().getFullyQualifiedName() + "." + declaredClassName;
			}
		}
		
		if(!manager.famixClassExists(declaredClassName)){
			//If this class doesn't exist, make a dummy.
			FamixClass clazz = new FamixClass();
			clazz.setUniqueName(declaredClassName);
			clazz.setIsDummy(true);
			manager.addFamixElement(clazz);
		}
		
		return manager.getFamixClass(declaredClassName);	
	}
	
	

	public LocalVariableRecorder(ILocalVariable element) {
		this();
		//TODO 
	}
	
	public LocalVariableRecorder(SourceCodeEntity entity){
		this();
		if(entity.getType().equals(JavaEntityType.VARIABLE_DECLARATION_STATEMENT)){
			//TODO 
		}
	}

	/* (non-Javadoc)
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		if(containingMethod == null)
			return;
		
		if(!containingMethod.containsVariable(uniquename)){
			variable = new FamixLocalVariable();
			variable.setUniqueName(uniquename);
			
			variable.setBelongsToBehaviour(containingMethod);
			containingMethod.addLocalVariable(variable);
			
			variable.setDeclaredClass(declaredClass);
		}else{
			variable = containingMethod.findLocalVariable(uniquename);
		}
	}

	/* (non-Javadoc)
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#createAndLinkChange(be.ac.ua.cheopsj.Model.Changes.AtomicChange)
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		if(containingMethod == null)
			return;
		
		change.setChangeSubject(variable);
		variable.addChange(change);

		setStructuralDependencies(change, variable);
		manager.addChange(change);
	}

	private void setStructuralDependencies(AtomicChange change, Subject subject) {
		if (change instanceof Add) {
			
			//link to addition of containing method
			if (containingMethod != null) {
				Change parentChange = containingMethod.getLatestAddition();
				if (parentChange != null) {
					change.addStructuralDependency(parentChange);
				}
			}
			
			//link to addition of declared type
			if(declaredClass != null){
				Change declaredClassChange = declaredClass.getLatestAddition();
				if(declaredClassChange != null){
					change.addStructuralDependency(declaredClassChange);
				}else{
					AtomicChange a = new Add();
					a.setDummy(true);
					change.addStructuralDependency(a);
					a.setChangeSubject(declaredClass);
					declaredClass.addChange(a);
				}
			}
			
			
			Remove removalChange = subject.getLatestRemoval();
			if (removalChange != null) {
				change.addStructuralDependency(removalChange);
			}
		} else if (change instanceof Remove) {
			// set dependency to addition of this entity
			// Subject removedSubject = change.getChangeSubject();
			AtomicChange additionChange = subject.getLatestAddition();
			if (additionChange != null) {
				change.addStructuralDependency(additionChange);
			}
		}
	}

}
