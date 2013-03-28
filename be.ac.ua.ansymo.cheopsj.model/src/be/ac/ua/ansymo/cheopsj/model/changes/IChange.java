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

import java.sql.Timestamp;

import org.eclipse.core.runtime.IAdaptable;

public interface IChange extends IAdaptable {

	public String getChangeType();

	public String getFamixType();

	public String getName();

	public Timestamp getTimeStamp();

	public String toString();

	public String getUser();
	
	public String getIntent();
	
	public String getID();
}
