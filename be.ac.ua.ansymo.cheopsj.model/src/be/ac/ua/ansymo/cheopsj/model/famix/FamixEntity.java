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

import org.eclipse.swt.graphics.Image;

public abstract class FamixEntity extends FamixObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9045753433613555690L;
	//private String name;
	private String uniqueName;
	private String name;
	
	
	/*public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}*/
	public String getUniqueName() {
		return uniqueName;
	}
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	
	@Override
	public boolean equals(Object fe){
		if(fe instanceof FamixEntity)
			return this.uniqueName.equals(((FamixEntity)fe).uniqueName);
		else
			return false;
			
	}
	
	abstract public Image getIcon();
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
