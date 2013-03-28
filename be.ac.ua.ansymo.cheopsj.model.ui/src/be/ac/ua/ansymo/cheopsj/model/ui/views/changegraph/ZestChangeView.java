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
package be.ac.ua.ansymo.cheopsj.model.ui.views.changegraph;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;

public class ZestChangeView extends ViewPart {

	public static final String ID = "be.ac.ua.ansymo.cheopsj.model.ui.view.changegraph.ZestChangeView";

	private static GraphViewer viewer = null;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new GraphViewer(parent, SWT.BORDER);
		viewer.setContentProvider(new ZestChangeViewContentProvider());
		viewer.setLabelProvider(new ZestChangeViewLabelProvider());

		viewer.setInput(ModelManager.getInstance());
		LayoutAlgorithm layout = setLayout();
		viewer.setLayoutAlgorithm(layout, true);
		viewer.applyLayout();

		createToolbarButtons();
	}

	private LayoutAlgorithm setLayout() {
		LayoutAlgorithm layout;
	    //layout = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		layout = new ZestChangeViewLayout(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
	    //layout = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		/*layout = new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new 
				LayoutAlgorithm[] { new 
				HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), new 
				HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });*/
	    //layout = new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);

		/*layout = new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new 
				LayoutAlgorithm[] {new 
				SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), new 
				HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });*/
		
		return layout;
	}

	@Override
	public void setFocus() {
	}

	private void createToolbarButtons() {
		IToolBarManager toolBarMgr = getViewSite().getActionBars().getToolBarManager();
		toolBarMgr.add(new GroupMarker("additions"));
	}

	public void resetViewLayout() {
		viewer.applyLayout();
	}

}
