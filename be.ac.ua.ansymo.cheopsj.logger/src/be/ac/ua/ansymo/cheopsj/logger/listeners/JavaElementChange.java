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
package be.ac.ua.ansymo.cheopsj.logger.listeners;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;

public class JavaElementChange {

	private IElementChangedListener elementChangeListener;

	public JavaElementChange() {
		elementChangeListener = new IElementChangedListener() {

			public void elementChanged(ElementChangedEvent event) {
				ChangeRecorder.getInstance().recordElementChanges(event.getDelta());
			}
		};

		JavaCore.addElementChangedListener(elementChangeListener);
	}

	/**
	 * Remove the ResourceChangeListener from ResourcesPlugin.
	 */
	public void dispose() {
		if (elementChangeListener != null) {
			JavaCore.removeElementChangedListener(elementChangeListener);
			elementChangeListener = null;
		}
	}

}
