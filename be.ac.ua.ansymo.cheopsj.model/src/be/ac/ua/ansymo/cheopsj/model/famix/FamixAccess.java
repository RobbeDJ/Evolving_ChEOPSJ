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

public class FamixAccess extends FamixAssociation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2899405554817417927L;

	private FamixStructuralEntity accesses = null;

	private FamixBehaviouralEntity accessedIn;

	public FamixStructuralEntity getAccesses() {
		return accesses;
	}

	public void setAccesses(FamixStructuralEntity accesses) {
		this.accesses = accesses;
	}

	public FamixBehaviouralEntity getAccessedIn() {
		return accessedIn;
	}

	public void setAccessedIn(FamixBehaviouralEntity accessedIn) {
		this.accessedIn = accessedIn;
	}

	@Override
	public String getFamixType() {
		return "Access";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object fe) {
		boolean result = false;
		if (fe instanceof FamixAccess) {
			result = accesses.equals(((FamixAccess) fe).getAccesses());
			result = result
					&& accessedIn.equals(((FamixAccess) fe).getAccessedIn());
		}

		return result;
	}
}
