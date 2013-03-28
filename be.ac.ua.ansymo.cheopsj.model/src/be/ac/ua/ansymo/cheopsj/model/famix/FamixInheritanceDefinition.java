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

public class FamixInheritanceDefinition extends FamixAssociation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7336410834130155668L;
	// private Qualifier accessControlQualifier;
	// private index Index;
	private FamixClass superClass;
	private FamixClass subClass;

	public FamixClass getSuperClass() {
		return superClass;
	}

	public void setSuperClass(FamixClass superClass) {
		this.superClass = superClass;
	}

	public FamixClass getSubClass() {
		return subClass;
	}

	public void setSubClass(FamixClass subClass) {
		this.subClass = subClass;
	}

	@Override
	public String getFamixType() {
		return "Inheritance Definition";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object fe) {
		boolean result = false;
		if (fe instanceof FamixInheritanceDefinition) {
			result = superClass.equals(((FamixInheritanceDefinition) fe)
					.getSuperClass());
			result = result
					&& subClass.equals(((FamixInheritanceDefinition) fe)
							.getSubClass());
		}

		return result;
	}
}
