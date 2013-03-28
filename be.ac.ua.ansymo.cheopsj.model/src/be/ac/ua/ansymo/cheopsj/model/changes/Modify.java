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
package be.ac.ua.ansymo.cheopsj.model.changes;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class Modify extends AtomicChange {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3742423656428378556L;

	public void undo(){ }

	public void apply(){ }

	
	@Override
	public String getChangeType() {
		return "Modification";
	}
	
	@Override
	public Image getIcon(){
		return PlatformUI.getWorkbench().getSharedImages()
		.getImage(ISharedImages.IMG_DEC_FIELD_WARNING);
	}
}
