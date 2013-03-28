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

import org.eclipse.ui.IStartup;

/**
 * This class is used to activate the plug-in during startup time.
 * 
 * @author Lile
 * 
 */
public class EarlyStartup implements IStartup {

	/**
	 * Starts main class of the Plugin
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		Cheops.getInstance().startPlugin();
	}

}
