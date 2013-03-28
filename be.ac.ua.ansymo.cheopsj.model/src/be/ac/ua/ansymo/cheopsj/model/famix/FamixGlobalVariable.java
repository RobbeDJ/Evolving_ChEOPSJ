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

import org.eclipse.swt.graphics.Image;

public class FamixGlobalVariable extends FamixStructuralEntity {
	//Not needed for Java Code?


	/**
	 * 
	 */
	private static final long serialVersionUID = 1088814027028260884L;
	/*
	 * (non-javadoc)
	 */
	private FamixPackage famixPackage = null;



	/**
	 * Getter of the property <tt>famixPackage</tt>
	 *
	 * @return Returns the famixPackage.
	 * 
	 */

	public FamixPackage getFamixPackage()
	{
		return famixPackage;
	}



	/**
	 * Setter of the property <tt>famixPackage</tt>
	 *
	 * @param famixPackage The famixPackage to set.
	 *
	 */
	public void setFamixPackage(FamixPackage famixPackage){
		this.famixPackage = famixPackage;
	}

	
	@Override
	public String getFamixType() {
		return "Global Variable";
	}
	
	/* (non-Javadoc)
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixEntity#getIcon()
	 */
	@Override
	public Image getIcon() {
		return null;
	}
}
