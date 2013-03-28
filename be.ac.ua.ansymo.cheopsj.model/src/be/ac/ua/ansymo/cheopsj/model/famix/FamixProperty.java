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

public class FamixProperty {

/*
 * 
 */
private FamixObject belongsToObject = null;

/*
 * 
 */
private String name = "";

/*
 * 
 */
private String value = "";
	
	 
	/**
	 * Getter of the property <tt>belongsToObject</tt>
	 *
	 * @return Returns the belongsToObject.
	 * 
	 */
	
	public FamixObject getBelongsToObject()
	{
		return belongsToObject;
	}

	
	
	/**
	 * Setter of the property <tt>belongsToObject</tt>
	 *
	 * @param object The belongsToObject to set.
	 *
	 */
	public void setBelongsToObject(FamixObject object){
		this.belongsToObject = object;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}

}
