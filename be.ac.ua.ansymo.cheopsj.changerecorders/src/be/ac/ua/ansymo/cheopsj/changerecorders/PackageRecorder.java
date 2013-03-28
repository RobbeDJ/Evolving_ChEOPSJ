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

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;


/**
 * The PackageRecorder, is a changerecorder that records changes made to a package. 
 * A package is represented by it's unique name : <superpackage unique name>'.' <package name>
 * @author quinten
 */
public class PackageRecorder extends AbstractEntityRecorder {
	private FamixPackage famixPackage; //the package to which we link a change
	private FamixPackage parent; //the super package
	private ModelManager manager; //the model manager
	private String uniqueName; //the unique name of our package
	private String name = "";

	private PackageRecorder(){
		manager = ModelManager.getInstance();
	}

	/**
	 * Constructor to create a PackageRecorder based on an IPackageFragment, which is a representation of a package in the JDT JavaModel.
	 * @see be.ac.ua.ansymo.cheopsj.logger.listeners.ChangeRecorder
	 * @see org.eclipse.jdt.core.IPackageFragment
	 * @param element The IPackageFragment.
	 */
	public PackageRecorder(IPackageFragment element) {
		this();
		uniqueName = element.getElementName();
		name = element.getElementName();
	}

	/**
	 * Constructor to create a PackageRecorder based on a PackageDeclaration, which is a representation of a package in the AST of a Compilation Unit. 
	 * @see be.ac.ua.ansymo.cheopsj.logger.listeners.ChangeRecorder
	 * @see org.eclipse.jdt.core.dom.PackageDeclaration
	 * @param declaration The PackageDeclaration 
	 */
	public PackageRecorder(PackageDeclaration declaration) {
		this();
		uniqueName = declaration.getName().getFullyQualifiedName();
		name = declaration.getName().getFullyQualifiedName();
	}

	/**
	 * @param uniquename
	 */
	public PackageRecorder(String uniquename) {
		this();
		uniqueName = uniquename;
		name = uniquename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		if (!manager.famixPackageExists(uniqueName)) {
			famixPackage = new FamixPackage();


			famixPackage.setUniqueName(uniqueName);
			//famixPackage.setName(packageName);

			linkToParent(famixPackage);
			parent = famixPackage.getBelongsToPackage();

			famixPackage.setName(name);
			
			manager.addFamixElement(famixPackage);
		} else {
			famixPackage = manager.getFamixPackage(uniqueName);
			parent = famixPackage.getBelongsToPackage();
		}
	}

	private void linkToParent(FamixPackage pack){
		String packagename = pack.getUniqueName();
		if(packagename.lastIndexOf('.') > 0){ //if there is a '.' in the name, then there is a parent package
			String superPackageName = packagename.substring(0, packagename.lastIndexOf('.'));
			FamixPackage parentPack = manager.getFamixPackage(superPackageName);
			if (parentPack != null) {
				pack.setBelongsToPackage(parentPack);
				parentPack.addPackage(pack);
			} else {
				//parent package did not yet exist, so we have to make it now! + link that to HIS parent
				parentPack = new FamixPackage();
				parentPack.setUniqueName(superPackageName);
				pack.setBelongsToPackage(parentPack);
				parentPack.addPackage(pack);

				linkToParent(parentPack);

				manager.addFamixElement(parentPack);

			}
		}//else there is NO parent package, then do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange()
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		change.setChangeSubject(famixPackage);
		famixPackage.addChange(change);

		setStructuralDependencies(change, famixPackage);
		manager.addChange(change);
	}

	protected void setStructuralDependencies(AtomicChange change, Subject subject) {
		if (change instanceof Add) {
			if (parent != null) {
				Change parentChange = parent.getLatestAddition();
				if (parentChange != null) {
					change.addStructuralDependency(parentChange);
				}else{
					linkToParentAdditions((FamixPackage)subject);		
				}
			}
			Remove removalChange = subject.getLatestRemoval();
			if (removalChange != null) {
				change.addStructuralDependency(removalChange);
			}
		} else if (change instanceof Remove) {
			//Dependencies to removes of child entities.
			//Subpackages:
			setDependenciesToSubPackages(change, subject);

			//Classes
			setDependenciesToContainingClasses(change, subject);

			// set dependency to addition of this entity
			AtomicChange additionChange = subject.getLatestAddition();
			if (additionChange != null) {
				change.addStructuralDependency(additionChange);
			}
		}
	}

	private void setDependenciesToSubPackages(AtomicChange change,
			Subject subject) {
		Collection<FamixPackage> subpacks = ((FamixPackage)subject).getPackages();
		if (!subpacks.isEmpty()) {
			for(FamixPackage child: subpacks){
				Change childChange = child.getLatestRemoval();
				if (childChange != null) {
					change.addStructuralDependency(childChange);
				}else{
					linkToChildRemoves((FamixPackage)subject);
				}
			}
		}
	}

	private void setDependenciesToContainingClasses(AtomicChange change,
			Subject subject) {
		Collection<FamixClass> classes = ((FamixPackage)subject).getClasses();
		if(!classes.isEmpty()){
			for(FamixClass child: classes){
				Change childChange = child.getLatestRemoval();
				if (childChange != null) {
					change.addStructuralDependency(childChange);
				}else{
					Remove classrem = new Remove();
					child.addChange(classrem);
					classrem.setChangeSubject(child);
					change.addStructuralDependency(classrem);

					classrem.addStructuralDependency(child.getLatestAddition());

					manager.addChange(classrem);
					
					//TODO remove all within the class?
				}
			}
		}
	}

	private void linkToChildRemoves(FamixPackage pack) {
		Remove packrem = pack.getLatestRemoval();
		Collection<FamixPackage> subPacks = pack.getPackages();

		for(FamixPackage subpack: subPacks){
			if(subpack.getLatestRemoval() == null){
				Remove subpackrem = new Remove();
				subpack.addChange(subpackrem);
				subpackrem.setChangeSubject(subpack);
				packrem.addStructuralDependency(subpackrem);

				subpackrem.addStructuralDependency(subpack.getLatestAddition());

				manager.addChange(subpackrem);
				linkToChildRemoves(subpack);
			}
		}		
	}

	private void linkToParentAdditions(FamixPackage pack) {
		AtomicChange packadd = pack.getLatestAddition();
		FamixPackage superPack = pack.getBelongsToPackage();

		if(superPack != null && superPack.getLatestAddition() == null){
			AtomicChange superpackadd = new Add();
			superpackadd.setChangeSubject(superPack);
			superPack.addChange(superpackadd);

			packadd.addStructuralDependency(superpackadd);

			manager.addChange(superpackadd);
			linkToParentAdditions(superPack);
		}
	}
}
