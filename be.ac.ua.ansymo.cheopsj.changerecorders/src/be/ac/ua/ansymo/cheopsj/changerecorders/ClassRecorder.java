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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
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
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

//TODO need to fix inheritance relationships and links to interfaces
public class ClassRecorder extends AbstractEntityRecorder {
	private FamixClass famixClass;
	private FamixEntity parent;
	private ModelManager manager;
	private String uniqueName;
	private int flags;
	private String name = "";

	private ClassRecorder(){
		//get manager instance
		manager = ModelManager.getInstance();
	}

	public ClassRecorder(IType element) {
		this();

		//can be null!!!!
		parent = findParentEntity(element);

		//set the unique (fully qualified) name of the class entity.
		uniqueName = element.getFullyQualifiedName();
		uniqueName = uniqueName.replace('$', '.');
		name = element.getElementName();

		//set the flags
		try {
			flags = element.getFlags();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public ClassRecorder(TypeDeclaration declaration) {
		this();

		name = declaration.getName().getIdentifier();
		
		//find the parent famix entity (if any)
		parent = findParentEntity(declaration);

		//set the unique (fully qualified) name of the class entity
		if(parent != null && parent.getUniqueName() != ""){
			uniqueName = parent.getUniqueName() + "." + declaration.getName().getFullyQualifiedName();
		}else{
			//there was no package declaration == default package
			uniqueName = declaration.getName().getFullyQualifiedName();
		}

		//set the flags
		flags = declaration.getFlags();
		
		String superclassname = declaration.getSuperclass().getFullyQualifiedName();
		System.out.println(superclassname);
		
	}

	public ClassRecorder(SourceCodeEntity entity, SourceCodeEntity parentEntity) {
		this();
		if(manager.famixClassExists(parentEntity.getUniqueName()))
			parent = manager.getFamixClass(parentEntity.getUniqueName());
		
		uniqueName = entity.getUniqueName();
		int j = uniqueName.lastIndexOf('.');
		name = uniqueName.substring(j,uniqueName.length());
		
		flags = entity.getModifiers();
		//TODO check supertype
	}

	public ClassRecorder(String uniquename, String classname, String packagename) {
		this();
		if(manager.famixClassExists(packagename))
			parent = manager.getFamixClass(packagename);
		uniqueName = uniquename;
		name = classname;
	}

	private FamixEntity findParentEntity(IType element) {
		IJavaElement parentJavaElement = element.getParent();
		if (parentJavaElement != null) {
			if (parentJavaElement instanceof ICompilationUnit) {
				parentJavaElement = parentJavaElement.getParent();
			}
			if (parentJavaElement instanceof IPackageFragment) {
				return manager.getFamixPackage(parentJavaElement.getElementName());
			}
			if (parentJavaElement instanceof IType) {
				return manager.getFamixClass(((IType) parentJavaElement).getFullyQualifiedName());
			}
		}
		return null;
	}

	private FamixEntity findParentEntity(TypeDeclaration declaration) {
		ASTNode parentASTNode = declaration.getParent();
		String parentName = "";
		if(parentASTNode != null){
			if (parentASTNode instanceof CompilationUnit) {
				PackageDeclaration pack = ((CompilationUnit) parentASTNode).getPackage();
				if(pack != null){
					parentName = pack.getName().getFullyQualifiedName();
					return manager.getFamixPackage(parentName);
				}
			}
			if (parentASTNode instanceof TypeDeclaration) {
				return manager.getFamixClass(findParentName(parentASTNode));
			}
		}
		return null;
	}
	
	private String findParentName(ASTNode node){
		if(node instanceof TypeDeclaration){
			String name = ((TypeDeclaration)node).getName().getFullyQualifiedName();
			ASTNode parentASTNode = node.getParent();
			if(parentASTNode != null){
				if (parentASTNode instanceof CompilationUnit) {
					PackageDeclaration pack = ((CompilationUnit) parentASTNode).getPackage();
					if(pack != null){
						return pack.getName().getFullyQualifiedName() + "." + name;
					}
				}
				if (parentASTNode instanceof TypeDeclaration) {
					return findParentName(parentASTNode) + "." + name;
				}
			}
		}
		return "";
	}
	
	

	/*public ClassRecorder(String name) {
		manager = ModelManager.getInstance();
		famixClass = manager.getFamixClass(name);
		parent = famixClass.getBelongsToPackage();
		// causes nullpointerexceptions!!!
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		if (!manager.famixClassExists(uniqueName)) {
			famixClass = new FamixClass();

			famixClass.setUniqueName(uniqueName);
			//famixClass.setName(declaration.getName().getIdentifier());

			setClassFlagsAndParent(famixClass);
			famixClass.setName(name);
			manager.addFamixElement(famixClass);
		} else {
			famixClass = manager.getFamixClass(uniqueName);
			if(famixClass.isDummy()){
				//If it was a dummy, undummy it!
				setClassFlagsAndParent(famixClass);
				
				famixClass.setIsDummy(false);
			}else{
				parent = famixClass.getBelongsToPackage();
			}
		}
	}

	private void setClassFlagsAndParent(FamixClass famixClass) {
		famixClass.setFlags(flags);

		if (parent != null && parent instanceof FamixPackage) {
			famixClass.setBelongsToPackage((FamixPackage) parent);
			((FamixPackage) parent).addClass(famixClass);
		} else if (parent != null && parent instanceof FamixClass) {
			famixClass.setBelongsToClass((FamixClass) parent);
			((FamixClass) parent).addNestedClass(famixClass);
		}		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange()
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		if(change instanceof Add){
			Add a = famixClass.getLatestAddition();
			if(a != null && a.isDummy()){
				change = a;
				change.setDummy(false);
			}
		}
		
		change.setChangeSubject(famixClass);
		famixClass.addChange(change);

		setStructuralDependencies(change, famixClass);
		manager.addChange(change);
	}

	protected void setStructuralDependencies(AtomicChange change, Subject subject) {
		if (change instanceof Add) {
			if (parent != null) {
				Change parentChange = parent.getLatestAddition();
				if (parentChange != null) {
					change.addStructuralDependency(parentChange);
				}//The parent of the class, be it a class or a package should already exist.
			}
			Remove removalChange = subject.getLatestRemoval();
			if (removalChange != null) {
				change.addStructuralDependency(removalChange);
			}
		} else if (change instanceof Remove) {
			// set dependency to addition of this entity
			AtomicChange additionChange = subject.getLatestAddition();
			if (additionChange != null) {
				change.addStructuralDependency(additionChange);
				
				//Dependencies to removes of child entities:
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
					changesubject.addChange(removal);
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
