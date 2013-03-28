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
package be.ac.ua.ansymo.cheopsj.model.famix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;

public class FamixPackage extends FamixEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4653287231040376260L;

	/**
	 *
	 */
	private Collection<FamixClass> classes = null;

	/*
	 * (non-javadoc)
	 */
	private FamixPackage belongsToPackage = null;

	/**
	 *
	 */
	private Collection<FamixPackage> packages = null;

	/**
	 *
	 */
	private Collection<FamixGlobalVariable> globalVariable = null;

	public FamixPackage() {
		super();
		this.classes = new ArrayList<FamixClass>();
		this.packages = new ArrayList<FamixPackage>();
	}
	
	/**
	 * Getter of the property <tt>classes</tt>
	 *
	 * @return Returns the classes.
	 * 
	 */

	public Collection<FamixClass> getClasses()
	{
		return classes;
	}

	/**
	 * Returns an iterator over the elements in this collection. 
	 *
	 * @return an <tt>Iterator</tt> over the elements in this collection
	 * @see	java.util.Collection#iterator()
	 * 
	 */
	public Iterator<FamixClass> classesIterator(){
		return classes.iterator();
	}

	/**
	 * Returns <tt>true</tt> if this collection contains no elements.
	 *
	 * @return <tt>true</tt> if this collection contains no elements
	 * @see	java.util.Collection#isEmpty()
	 *
	 */
	public boolean isClassesEmpty(){
		return classes.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this collection contains the specified element. 
	 *
	 * @param element whose presence in this collection is to be tested.
	 * @see	java.util.Collection#contains(Object)
	 *
	 */
	public boolean containsClass(FamixClass clazz){
		return this.classes.contains(clazz);
	}

	/**
	 * Returns <tt>true</tt> if this collection contains all of the elements
	 * in the specified collection.
	 *
	 * @param elements collection to be checked for containment in this collection.
	 * @see	java.util.Collection#containsAll(Collection)
	 *
	 */
	public boolean containsAllClasses(Collection<FamixClass> classes){
		return this.classes.containsAll(classes);
	}

	/**
	 * Returns the number of elements in this collection.
	 *
	 * @return the number of elements in this collection
	 * @see	java.util.Collection#size()
	 *
	 */
	public int classesSize(){
		return classes.size();
	}

	/**
	 * Returns all elements of this collection in an array.
	 *
	 * @return an array containing all of the elements in this collection
	 * @see	java.util.Collection#toArray()
	 *
	 */
	public FamixClass[] classesToArray(){
		return classes.toArray(new FamixClass[classes.size()]);
	}

	/**
	 * Setter of the property <tt>class</tt>
	 *
	 * @param class the class to set.
	 *
	 */
	public void setClasses(Collection<FamixClass> classes){
		this.classes = classes;
	}


	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation). 
	 *
	 * @param element whose presence in this collection is to be ensured.
	 * @see	java.util.Collection#add(Object)
	 *
	 */
	public boolean addClass(FamixClass clazz){
		return this.classes.add(clazz);
	}

	/**
	 * Removes a single instance of the specified element from this
	 * collection, if it is present (optional operation).
	 *
	 * @param element to be removed from this collection, if present.
	 * @see	java.util.Collection#add(Object)
	 *
	 */
	public boolean removeClass(FamixClass clazz){
		return this.classes.remove(clazz);
	}

	/**
	 * Removes all of the elements from this collection (optional operation).
	 *
	 * @see	java.util.Collection#clear()
	 *
	 */
	public void clearClasses(){
		this.classes.clear();
	}

	/**
	 * Getter of the property <tt>famixPackage1</tt>
	 *
	 * @return Returns the famixPackage1.
	 * 
	 */

	public Collection<FamixPackage> getPackages()
	{
		return packages;
	}

	/**
	 * Returns an iterator over the elements in this collection. 
	 *
	 * @return an <tt>Iterator</tt> over the elements in this collection
	 * @see	java.util.Collection#iterator()
	 * 
	 */
	public Iterator<FamixPackage> packagesIterator(){
		return packages.iterator();
	}

	/**
	 * Returns <tt>true</tt> if this collection contains no elements.
	 *
	 * @return <tt>true</tt> if this collection contains no elements
	 * @see	java.util.Collection#isEmpty()
	 *
	 */
	public boolean isPackagesEmpty(){
		return packages.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this collection contains the specified element. 
	 *
	 * @param element whose presence in this collection is to be tested.
	 * @see	java.util.Collection#contains(Object)
	 *
	 */
	public boolean containsPackage(FamixPackage Package){
		return this.packages.contains(Package);
	}

	/**
	 * Returns <tt>true</tt> if this collection contains all of the elements
	 * in the specified collection.
	 *
	 * @param elements collection to be checked for containment in this collection.
	 * @see	java.util.Collection#containsAll(Collection)
	 *
	 */
	public boolean containsAllPackages(Collection<FamixPackage> Packages){
		return this.packages.containsAll(Packages);
	}

	/**
	 * Returns the number of elements in this collection.
	 *
	 * @return the number of elements in this collection
	 * @see	java.util.Collection#size()
	 *
	 */
	public int packagesSize(){
		return packages.size();
	}

	/**
	 * Returns all elements of this collection in an array.
	 *
	 * @return an array containing all of the elements in this collection
	 * @see	java.util.Collection#toArray()
	 *
	 */
	public FamixPackage[] packagesToArray(){
		return packages.toArray(new FamixPackage[packages.size()]);
	}

	/**
	 * Setter of the property <tt>famixPackage1</tt>
	 *
	 * @param famixPackage1 the famixPackage1 to set.
	 *
	 */
	public void setPackages(Collection<FamixPackage> packages){
		this.packages = packages;
	}


	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation). 
	 *
	 * @param element whose presence in this collection is to be ensured.
	 * @see	java.util.Collection#add(Object)
	 *
	 */
	public boolean addPackage(FamixPackage Package){
		return this.packages.add(Package);
	}

	/**
	 * Removes a single instance of the specified element from this
	 * collection, if it is present (optional operation).
	 *
	 * @param element to be removed from this collection, if present.
	 * @see	java.util.Collection#add(Object)
	 *
	 */
	public boolean removePackage(FamixPackage Package){
		return this.packages.remove(Package);
	}

	/**
	 * Removes all of the elements from this collection (optional operation).
	 *
	 * @see	java.util.Collection#clear()
	 *
	 */
	public void clearPackages(){
		this.packages.clear();
	}

	/**
	 * Getter of the property <tt>belongsToPackage</tt>
	 *
	 * @return Returns the belongsToPackage.
	 * 
	 */

	public FamixPackage getBelongsToPackage()
	{
		return belongsToPackage;
	}

	/**
	 * Setter of the property <tt>belongsToPackage</tt>
	 *
	 * @param belongsToPackage The belongsToPackage to set.
	 *
	 */
	public void setBelongsToPackage(FamixPackage belongsToPackage){
		this.belongsToPackage = belongsToPackage;
	}



	/**
	 * Getter of the property <tt>globalVariable</tt>
	 *
	 * @return Returns the globalVariable.
	 * 
	 */

	public Collection<FamixGlobalVariable> getGlobalVariable()
	{
		return globalVariable;
	}




	/**
	 * Returns an iterator over the elements in this collection. 
	 *
	 * @return an <tt>Iterator</tt> over the elements in this collection
	 * @see	java.util.Collection#iterator()
	 * 
	 */
	public Iterator<FamixGlobalVariable> globalVariableIterator(){
		return globalVariable.iterator();
	}

	/**
	 * Returns <tt>true</tt> if this collection contains no elements.
	 *
	 * @return <tt>true</tt> if this collection contains no elements
	 * @see	java.util.Collection#isEmpty()
	 *
	 */
	public boolean isGlobalVariableEmpty(){
		return globalVariable.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this collection contains the specified element. 
	 *
	 * @param element whose presence in this collection is to be tested.
	 * @see	java.util.Collection#contains(Object)
	 *
	 */
	public boolean containsGlobalVariable(FamixGlobalVariable globalVariable){
		return this.globalVariable.contains(globalVariable);
	}

	/**
	 * Returns <tt>true</tt> if this collection contains all of the elements
	 * in the specified collection.
	 *
	 * @param elements collection to be checked for containment in this collection.
	 * @see	java.util.Collection#containsAll(Collection)
	 *
	 */
	public boolean containsAllGlobalVariable(Collection<FamixGlobalVariable> globalVariable){
		return this.globalVariable.containsAll(globalVariable);
	}

	/**
	 * Returns the number of elements in this collection.
	 *
	 * @return the number of elements in this collection
	 * @see	java.util.Collection#size()
	 *
	 */
	public int globalVariableSize(){
		return globalVariable.size();
	}

	/**
	 * Returns all elements of this collection in an array.
	 *
	 * @return an array containing all of the elements in this collection
	 * @see	java.util.Collection#toArray()
	 *
	 */
	public FamixGlobalVariable[] globalVariableToArray(){
		return globalVariable.toArray(new FamixGlobalVariable[globalVariable.size()]);
	}

	/**
	 * Setter of the property <tt>globalVariable</tt>
	 *
	 * @param globalVariable the globalVariable to set.
	 *
	 */
	public void setGlobalVariable(Collection<FamixGlobalVariable> globalVariable){
		this.globalVariable = globalVariable;
	}


	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation). 
	 *
	 * @param element whose presence in this collection is to be ensured.
	 * @see	java.util.Collection#add(Object)
	 *
	 */
	public boolean addGlobalVariable(FamixGlobalVariable globalVariable){
		return this.globalVariable.add(globalVariable);
	}

	/**
	 * Removes a single instance of the specified element from this
	 * collection, if it is present (optional operation).
	 *
	 * @param element to be removed from this collection, if present.
	 * @see	java.util.Collection#add(Object)
	 *
	 */
	public boolean removeGlobalVariable(FamixGlobalVariable globalVariable){
		return this.globalVariable.remove(globalVariable);
	}

	/**
	 * Removes all of the elements from this collection (optional operation).
	 *
	 * @see	java.util.Collection#clear()
	 *
	 */
	public void clearGlobalVariable(){
		this.globalVariable.clear();
	}

	@Override
	public String getFamixType() {
		return "Package";
	}
	
	/* (non-Javadoc)
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixEntity#getIcon()
	 */
	@Override
	public Image getIcon() {
		return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PACKAGE);
	}
	
}
