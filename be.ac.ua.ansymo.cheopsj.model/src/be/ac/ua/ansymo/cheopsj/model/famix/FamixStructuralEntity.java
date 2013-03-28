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

public abstract class FamixStructuralEntity extends FamixEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8371156868944449276L;
	/*
	 * (non-javadoc)
	 */
	private FamixClass declaredClass;
	private PrimitiveTypes declaredReturnType;


	/**
	 * Getter of the property <tt>declaredClass</tt>
	 *
	 * @return Returns the declaredClass.
	 * 
	 */

	public FamixClass getDeclaredClass()
	{
		return declaredClass;
	}



	/**
	 * Setter of the property <tt>declaredClass</tt>
	 *
	 * @param declaredClass The declaredClass to set.
	 *
	 */
	public void setDeclaredClass(FamixClass declaredClass){
		this.declaredClass = declaredClass;
	}



	public PrimitiveTypes getDeclaredReturnType() {
		return declaredReturnType;
	}



	public void setDeclaredReturnType(PrimitiveTypes declaredReturnType) {
		this.declaredReturnType = declaredReturnType;
	}

}
