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
package be.ac.ua.ansymo.cheopsj.model.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;



public class LoadStateHandler extends AbstractHandler {

	/**
	 * Load the state of the model
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ModelManager.getInstance().loadModel();
		// should now also refresh the change view?

		return null;
	}
}
