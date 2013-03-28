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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import be.ac.ua.ansymo.cheopsj.model.ui.view.changeinspector.ChangeView;
import be.ac.ua.ansymo.cheopsj.model.ui.views.changegraph.ZestChangeView;


public class OpenChangeViewHandler extends AbstractHandler {

	/**
	 * Open the favorites view.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get the active window

		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		if (window == null)
			return null;

		// Get the active page

		IWorkbenchPage page = window.getActivePage();
		if (page == null)
			return null;

		// Open and activate the view

		try {
			page.showView(ChangeView.ID);
			// page.showView(GEFChangeView.ID);
			page.showView(ZestChangeView.ID);
		} catch (PartInitException e) {

		}
		return null;
	}
}
