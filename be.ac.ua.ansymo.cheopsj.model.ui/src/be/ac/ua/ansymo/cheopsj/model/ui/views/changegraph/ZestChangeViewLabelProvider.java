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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixObject;


public class ZestChangeViewLabelProvider extends LabelProvider implements IConnectionStyleProvider {

	public Color ConnectionColor = new Color(Display.getDefault(), 0, 0, 0);
	public Color FamixConnectionColor = new Color(Display.getDefault(), 0, 0, 204);
	public Color ChangeConnectionColor = new Color(Display.getDefault(), 204, 0, 0);
	public Color ChangeFamixConnectionColor = new Color(Display.getDefault(), 60, 60, 60);

	public Image getImage(Object element) {
		if (element instanceof AtomicChange) {
			return ((AtomicChange) element).getIcon();
		}
		if (element instanceof FamixEntity) {
			return ((FamixEntity) element).getIcon();
		}
		return null;
	}

	public String getText(Object element) {
		if (element instanceof AtomicChange) {
			//return ((AtomicChange) element).getName();
		}
		if (element instanceof FamixEntity) {
			return ((FamixEntity) element).getUniqueName();
		}
		if (element instanceof FamixInvocation) {
			return ((FamixInvocation) element).getStringRepresentation();
		}

		/*if (element instanceof EntityConnectionData) {
			if (((EntityConnectionData) element).source instanceof FamixEntity) {
				return "belongsTo";
			} else if (((EntityConnectionData) element).source instanceof
					AtomicChange) {
				if (((EntityConnectionData) element).dest instanceof AtomicChange) {
				}
				return "dep";
			} else if (((EntityConnectionData) element).dest instanceof
					FamixEntity) {
				return "subj";
			}
		}*/
		return "";
	}

	@Override
	public int getConnectionStyle(Object rel) {

		if (rel instanceof EntityConnectionData) { 
			if(((EntityConnectionData) rel).dest instanceof FamixEntity) { 
				return ZestStyles.CONNECTIONS_DIRECTED + ZestStyles.CONNECTIONS_DASH; 
			} else if (((EntityConnectionData) rel).dest instanceof AtomicChange) { 
				if (((EntityConnectionData) rel).source instanceof AtomicChange) {
					return ZestStyles.CONNECTIONS_DIRECTED + ZestStyles.CONNECTIONS_DOT;
				} else if (((EntityConnectionData) rel).source instanceof FamixEntity) { 
					return ZestStyles.CONNECTIONS_DIRECTED +
							ZestStyles.CONNECTIONS_DASH_DOT; 
				} 
			} 
		} 
		return ZestStyles.NONE;

		//return ZestStyles.CONNECTIONS_DIRECTED;
	}

	@Override
	public Color getColor(Object rel) {
		if (rel instanceof EntityConnectionData) {
			if (((EntityConnectionData) rel).source instanceof FamixObject) {
				return FamixConnectionColor;
			} else if (((EntityConnectionData) rel).source instanceof AtomicChange) {
				if (((EntityConnectionData) rel).dest instanceof AtomicChange) {
					return ChangeConnectionColor;
				} else if (((EntityConnectionData) rel).dest instanceof FamixObject) {
					return ChangeFamixConnectionColor;
				}
			}
		}
		return ConnectionColor;
	}

	@Override
	public Color getHighlightColor(Object rel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLineWidth(Object rel) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IFigure getTooltip(Object entity) {
		if (entity instanceof AtomicChange) {
			Label toolTip = new Label();
			toolTip.setText(((AtomicChange) entity).getName());
			return toolTip;
		}
		return null;
	}
}
