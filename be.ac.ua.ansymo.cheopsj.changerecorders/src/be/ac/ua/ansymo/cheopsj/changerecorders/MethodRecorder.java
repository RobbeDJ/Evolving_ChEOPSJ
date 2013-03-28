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

import java.util.Collection;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;



public class MethodRecorder extends AbstractEntityRecorder {
	private FamixMethod famixMethod;
	private FamixClass parent; // TODO is there something like a nested method inside another method?
	private ModelManager manager;
	private String uniquename = ""; //TODO need to add parameters to unique naming
	//TODO need to link method to return type
	private int flags = 0;
	private String name = "";
	
	private MethodRecorder(){
		manager = ModelManager.getInstance();
	}

	public MethodRecorder(IMethod method) {
		this();
		String classname = ((IType) method.getParent()).getFullyQualifiedName();
		name = method.getElementName();
		uniquename = classname + '.' + name;
		
		IJavaElement parentJavaElement = method.getParent();
		if (parentJavaElement != null && parentJavaElement instanceof IType) {
			parent = manager.getFamixClass(((IType) parentJavaElement).getFullyQualifiedName());
		}
		try {
			flags = method.getFlags();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MethodRecorder(MethodDeclaration method) {
		this();
		
		parent = findParentFamixEntity(method);
		name = method.getName().getIdentifier();
		if(parent != null){
			uniquename = parent.getUniqueName() + "."  + method.getName().getFullyQualifiedName();
		}else{
			uniquename = method.getName().getFullyQualifiedName();
		}
		
		flags = method.getFlags();
	}
	
	private FamixClass findParentFamixEntity(MethodDeclaration method) {
		//find parent famix entity
		String parentName = findParentName(method);
	
		return manager.getFamixClass(parentName);	
	}
	
	private String findParentName(ASTNode node){
		ASTNode parent = node.getParent();
		if(parent instanceof TypeDeclaration){
			return findParentName(parent) + "." + ((TypeDeclaration) parent).getName().getIdentifier();
		}
		if(parent instanceof CompilationUnit){
			
			if(((CompilationUnit) parent).getPackage() != null)
				return ((CompilationUnit) parent).getPackage().getName().getFullyQualifiedName();
			else //default package
				return "";
		}
		
		return "";
	}
	
	public MethodRecorder(SourceCodeEntity entity, SourceCodeEntity parentEntity){
		this();
		if(entity.getType().isMethod()){
			uniquename = entity.getUniqueName();
			int i = uniquename.indexOf('(');
			uniquename = uniquename.substring(0, i);
			
			int j = uniquename.lastIndexOf('.');
			name = uniquename.substring(j,i);
			
			if(parentEntity.getType().isType()){
				String parentUniqueName = parentEntity.getUniqueName();
				parent = manager.getFamixClass(parentUniqueName);
			}
		}
		flags = entity.getModifiers(); //TOFIX
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		// TODO use signature in unique name (includes parameter types)

		if (!manager.famixMethodExists(uniquename)) {
			famixMethod = new FamixMethod();
			famixMethod.setUniqueName(uniquename);
			famixMethod.setName(name);
			setMethodFlagsAndParent();
			manager.addFamixElement(famixMethod);
		} else {
			famixMethod = manager.getFamixMethod(uniquename);
			if (famixMethod.isDummy()) {
				setMethodFlagsAndParent();
				
				famixMethod.setIsDummy(false);
			} else {
				parent = famixMethod.getBelongsToClass();
			}
		}
	}

	private void setMethodFlagsAndParent() {
		if (uniquename.contains("test")) {
			famixMethod.setIsTest(true);
		}
		
		famixMethod.setFlags(flags);
		
		if (parent != null) {
			famixMethod.setBelongsToClass(parent);
			parent.addMethod(famixMethod);
		}

		/*TODO Fix this too!
		 * try {
		 
			FamixClass returnType = manager.getFamixClass(element.getReturnType());
			famixMethod.setDeclaredReturnClass(returnType);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange()
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		change.setChangeSubject(famixMethod);
		famixMethod.addChange(change);

		setStructuralDependencies(change, famixMethod);
		manager.addChange(change);
	}

	protected void setStructuralDependencies(AtomicChange change, Subject subject) {
		if (change instanceof Add) {
			if (parent != null) {
				Change parentChange = parent.getLatestAddition();
				if (parentChange != null) {
					change.addStructuralDependency(parentChange);
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

				removeAllContainedWithin(change, additionChange);
			}
		}
	}

	private void removeAllContainedWithin(AtomicChange change, AtomicChange additionChange) {
		Collection<Change> dependees = additionChange.getStructuralDependees();
		for (Change dependee : dependees) {
			if (dependee instanceof Add) {
				Subject changesubject = ((AtomicChange) dependee).getChangeSubject();
				Change latestChange = changesubject.getLatestChange();
				if (latestChange instanceof Add) {
					// only remove if it wasn't removed yet

					Remove removal = new Remove();
					removal.setChangeSubject(changesubject);
					setStructuralDependencies(removal, removal.getChangeSubject());

					change.addStructuralDependency(removal);

					manager.addChange(removal);
				} else if (latestChange instanceof Remove) {
					change.addStructuralDependency(latestChange);
				}
			}
		}
	}
}
