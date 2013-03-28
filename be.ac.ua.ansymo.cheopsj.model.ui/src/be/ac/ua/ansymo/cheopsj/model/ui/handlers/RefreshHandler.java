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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;

import be.ac.ua.ansymo.cheopsj.model.ui.views.changegraph.ZestChangeView;



/**
 * @author quinten
 * 
 */
public class RefreshHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IViewPart findView = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView("be.ac.ua.ansymo.cheopsj.model.ui.view.changegraph.ZestChangeView");
		ZestChangeView view = (ZestChangeView) findView;
		view.resetViewLayout();
		return null;
	}
}
