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
package be.ac.ua.ansymo.cheopsj.model.ui.view.changeinspector;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;

public class ChangeViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	
	
	public String getColumnText(Object obj, int index) {
		switch (index) {
		case 0: // Type column
			if (obj instanceof IChange)
				return ((IChange) obj).getChangeType();
		case 1: // Type column
			if (obj instanceof IChange)
				return ((IChange) obj).getFamixType();
		case 2: // Name column
			if (obj instanceof IChange)
				return ((IChange) obj).getName();
			if (obj != null)
				return obj.toString();
			return "";
		case 3: // Timestamp column
			if (obj instanceof IChange)
				return ((IChange) obj).getTimeStamp().toString();
		case 4: // User Column
			if (obj instanceof IChange)
				return ((IChange) obj).getUser();
		case 5: //Intent Column
			if (obj instanceof IChange)
				return ((IChange) obj).getIntent();
		default:
			return "";
		}
	}

	public Image getColumnImage(Object obj, int index) {
		if ((index == 0) && (obj instanceof AtomicChange)) {
			return ((AtomicChange) obj).getIcon();
		} else if ((index == 1) && (obj instanceof AtomicChange)) {
			if (((AtomicChange) obj).getChangeSubject() instanceof FamixEntity) {
				return ((FamixEntity) ((AtomicChange) obj).getChangeSubject()).getIcon();
			}
		}
		return null;
	}
}