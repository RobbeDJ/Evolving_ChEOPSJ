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

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

/**
 * @author quinten
 * 
 */
public abstract class AbstractEntityRecorder {

	/**
	 * @param change
	 */
	public void storeChange(IChange change) {
		createAndLinkFamixElement();
		createAndLinkChange((AtomicChange) change);
	}

	abstract protected void createAndLinkFamixElement();

	abstract protected void createAndLinkChange(AtomicChange change);
}
