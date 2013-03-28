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
package be.ac.ua.ansymo.cheopsj.model.changes;

public class CompositeChange extends Change {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6352341144138745117L;
/*
 * (non-javadoc)
 */
private Change parent;

	public void undo(){
	
	
	
	}

	public void apply(){
	
	
	
	}

	
	 
	/**
	 * Getter of the property <tt>parent</tt>
	 *
	 * @return Returns the parent.
	 * 
	 */
	
	public Change getParent()
	{
		return parent;
	}

	
	
	/**
	 * Setter of the property <tt>parent</tt>
	 *
	 * @param parent The parent to set.
	 *
	 */
	public void setParent(Change parent){
		this.parent = parent;
	}

}
