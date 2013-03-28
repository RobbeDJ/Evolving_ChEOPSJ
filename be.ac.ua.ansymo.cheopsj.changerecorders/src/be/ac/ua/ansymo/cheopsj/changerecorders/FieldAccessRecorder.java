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

package be.ac.ua.ansymo.cheopsj.changerecorders;

import org.eclipse.jdt.core.dom.FieldAccess;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;

/**
 * @author quinten
 * 
 */
public class FieldAccessRecorder extends AbstractEntityRecorder {

	/**
	 * @param node
	 */
	public FieldAccessRecorder(FieldAccess node) {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange(be.ac.ua.cheopsj.Model.Changes.AtomicChange)
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		// TODO Auto-generated method stub

	}

}
