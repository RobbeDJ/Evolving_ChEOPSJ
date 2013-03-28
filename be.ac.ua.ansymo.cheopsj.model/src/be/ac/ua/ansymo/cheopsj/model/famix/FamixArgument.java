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

public abstract class FamixArgument extends FamixObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6675862587491925575L;

	private boolean isReceiver;
	
	private int position;
	
	public boolean isReceiver() {
		return isReceiver;
	}
	
	public void setReceiver(boolean isReceiver) {
		this.isReceiver = isReceiver;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
}
