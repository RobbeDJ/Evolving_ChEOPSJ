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
package be.ac.ua.ansymo.cheopsj.model.famix;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;

public class FamixLocalVariable extends FamixStructuralEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 868273049562546947L;
	/*
	 * (non-javadoc)
	 */
	private FamixBehaviouralEntity belongsToBehaviour = null;

	/**
	 * Getter of the property <tt>famixBehaviouralEntity</tt>
	 * 
	 * @return Returns the famixBehaviouralEntity.
	 * 
	 */

	public FamixBehaviouralEntity getBelongsToBehaviour() {
		return belongsToBehaviour;
	}

	/**
	 * Setter of the property <tt>belongsToBehaviour</tt>
	 * 
	 * @param belongsToBehaviour
	 *            The belongsToBehaviour to set.
	 * 
	 */
	public void setBelongsToBehaviour(FamixBehaviouralEntity belongsToBehaviour) {
		this.belongsToBehaviour = belongsToBehaviour;
	}

	@Override
	public String getFamixType() {
		return "Local Variable";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixEntity#getIcon()
	 */
	@Override
	public Image getIcon() {
		return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_LOCAL_VARIABLE);

	}
}
