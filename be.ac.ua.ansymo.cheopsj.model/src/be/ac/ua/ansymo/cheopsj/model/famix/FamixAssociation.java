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

public abstract class FamixAssociation extends FamixObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8981682243360218998L;
	private String stringrep;

	/**
	 * @return the stringrep
	 */
	public String getStringRepresentation() {
		return stringrep;
	}

	/**
	 * @param stringrep
	 *            the stringrep to set
	 */
	public void setStringRepresentation(String stringrep) {
		this.stringrep = stringrep;
	}

	//TODO find a way to uniquely represent an association?
	
	
}
