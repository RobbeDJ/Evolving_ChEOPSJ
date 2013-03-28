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
import java.util.List;

public class FamixInvocation extends FamixAssociation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2761645180761077520L;

	/*
	 * (non-javadoc)
	 */
	private FamixBehaviouralEntity invokedBy = null;

	//private List<FamixBehaviouralEntity> candidates = new ArrayList<FamixBehaviouralEntity>();
	private FamixBehaviouralEntity candidate = null;

	/**
	 * Getter of the property <tt>invokedBy</tt>
	 * 
	 * @return Returns the invokedBy.
	 * 
	 */

	public FamixBehaviouralEntity getInvokedBy() {
		return invokedBy;
	}

	/**
	 * Setter of the property <tt>invokedBy</tt>
	 * 
	 * @param invokedBy
	 *            The invokedBy to set.
	 * 
	 */
	public void setInvokedBy(FamixBehaviouralEntity invokedBy) {
		this.invokedBy = invokedBy;
	}

	public FamixBehaviouralEntity getCandidate() {
		return candidate;
	}

	public void setCandidate(FamixBehaviouralEntity candidate) {
		this.candidate = candidate;
	}

	@Override
	public String getFamixType() {
		return "Invocation";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object fe) {
		boolean result = false;
		if (fe instanceof FamixInvocation) {
			result = invokedBy.equals(((FamixInvocation) fe).getInvokedBy());
			result = result && candidate.equals(((FamixInvocation) fe).getCandidate());
		}

		return result;
	}
}
