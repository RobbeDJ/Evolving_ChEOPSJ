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
package be.ac.ua.ansymo.cheopsj.logger;

import be.ac.ua.ansymo.cheopsj.logger.listeners.JavaElementChange;

public class Cheops {

	private static Cheops instance;
	private JavaElementChange elementChange;

	private Cheops() {
	}

	/**
	 * 
	 * @return The unique instance of Cheops
	 */
	public static Cheops getInstance() {
		if (instance == null) {
			instance = new Cheops();
		}
		return instance;
	}

	/**
	 * Method called when plug-in starts.
	 */
	public void startPlugin() {
		// add a new ResourceChangeListener
		Cheops.getInstance().addResourceChangeListener();
	}

	/**
	 * 
	 */
	public void stopCheops() {
		// Remove the ResourceChangeListener
		removeResourceChangeListener();
	}

	/**
	 * Adds the ResourceChangeListener to capture changes for each new build
	 */
	public void addResourceChangeListener() {
		if (elementChange == null) {
			elementChange = new JavaElementChange();
		}
	}

	/**
	 * Removes the ResourceChangeListener
	 */
	public void removeResourceChangeListener() {
		if (elementChange != null) {
			elementChange.dispose();
			elementChange = null;
		}
	}
}
