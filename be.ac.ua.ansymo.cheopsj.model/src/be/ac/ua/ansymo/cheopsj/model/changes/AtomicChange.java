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

import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;

public class AtomicChange extends Change {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1879288093953723902L;
	/*
	 * (non-javadoc)
	 */
	private Subject changeSubject;

	public void undo() {
	}

	public void apply() {
	}

	/**
	 * Getter of the property <tt>changeSubject</tt>
	 * 
	 * @return Returns the changeSubject.
	 */
	public Subject getChangeSubject() {
		return changeSubject;
	}

	/**
	 * Setter of the property <tt>changeSubject</tt>
	 * 
	 * @param changeSubject
	 *            The changeSubject to set.
	 */
	public void setChangeSubject(Subject changeSubject) {
		this.changeSubject = changeSubject;
	}

	@Override
	public String getFamixType() {
		if (changeSubject != null)
			return changeSubject.getFamixType();
		else
			return "";
	}

	@Override
	public String getName() {
		if (changeSubject != null && changeSubject instanceof FamixEntity)
			return ((FamixEntity) changeSubject).getUniqueName();
		else if (changeSubject instanceof FamixInvocation)
			return ((FamixInvocation) changeSubject).getStringRepresentation();
		else
			return "";
	}
}
