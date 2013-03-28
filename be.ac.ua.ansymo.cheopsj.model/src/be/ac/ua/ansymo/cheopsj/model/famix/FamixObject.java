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

import java.util.Collection;
import java.util.Iterator;

import be.ac.ua.ansymo.cheopsj.model.changes.Subject;

public abstract class FamixObject extends Subject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8612301559130783370L;

	// TODO add sourceAnchor where is the actual source code that this
	// FamixObject is representing? Typically: filename + begin and end lines
	private String CommentsAt;

	private int flags;

	private boolean dummy;

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getFlags() {
		return flags;
	}

	/**
     *
     */
	private Collection<FamixProperty> property = null;

	public String getCommentsAt() {
		return CommentsAt;
	}

	public void setCommentsAt(String commentsAt) {
		CommentsAt = commentsAt;
	}

	/**
	 * Getter of the property <tt>property</tt>
	 * 
	 * @return Returns the property.
	 * 
	 */

	public Collection<FamixProperty> getProperty() {
		return property;
	}

	/**
	 * Returns an iterator over the elements in this collection.
	 * 
	 * @return an <tt>Iterator</tt> over the elements in this collection
	 * @see java.util.Collection#iterator()
	 * 
	 */
	public Iterator<FamixProperty> propertyIterator() {
		return property.iterator();
	}

	/**
	 * Returns <tt>true</tt> if this collection contains no elements.
	 * 
	 * @return <tt>true</tt> if this collection contains no elements
	 * @see java.util.Collection#isEmpty()
	 * 
	 */
	public boolean isPropertyEmpty() {
		return property.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this collection contains the specified element.
	 * 
	 * @param element
	 *            whose presence in this collection is to be tested.
	 * @see java.util.Collection#contains(Object)
	 * 
	 */
	public boolean containsProperty(FamixProperty property) {
		return this.property.contains(property);
	}

	/**
	 * Returns <tt>true</tt> if this collection contains all of the elements in
	 * the specified collection.
	 * 
	 * @param elements
	 *            collection to be checked for containment in this collection.
	 * @see java.util.Collection#containsAll(Collection)
	 * 
	 */
	public boolean containsAllProperty(Collection<FamixProperty> property) {
		return this.property.containsAll(property);
	}

	/**
	 * Returns the number of elements in this collection.
	 * 
	 * @return the number of elements in this collection
	 * @see java.util.Collection#size()
	 * 
	 */
	public int propertySize() {
		return property.size();
	}

	/**
	 * Returns all elements of this collection in an array.
	 * 
	 * @return an array containing all of the elements in this collection
	 * @see java.util.Collection#toArray()
	 * 
	 */
	public FamixProperty[] propertyToArray() {
		return property.toArray(new FamixProperty[property.size()]);
	}

	/**
	 * Setter of the property <tt>property</tt>
	 * 
	 * @param property
	 *            the property to set.
	 * 
	 */
	public void setProperty(Collection<FamixProperty> property) {
		this.property = property;
	}

	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation).
	 * 
	 * @param element
	 *            whose presence in this collection is to be ensured.
	 * @see java.util.Collection#add(Object)
	 * 
	 */
	public boolean addProperty(FamixProperty property) {
		return this.property.add(property);
	}

	/**
	 * Removes a single instance of the specified element from this collection,
	 * if it is present (optional operation).
	 * 
	 * @param element
	 *            to be removed from this collection, if present.
	 * @see java.util.Collection#add(Object)
	 * 
	 */
	public boolean removeProperty(FamixProperty property) {
		return this.property.remove(property);
	}

	/**
	 * Removes all of the elements from this collection (optional operation).
	 * 
	 * @see java.util.Collection#clear()
	 * 
	 */
	public void clearProperty() {
		this.property.clear();
	}

	public abstract boolean equals(Object fe);

	/**
	 * 
	 */
	public void setIsDummy(boolean d) {
		dummy = d;
	}

	public boolean isDummy() {
		return dummy;
	}

}
