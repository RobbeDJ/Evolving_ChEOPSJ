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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public abstract class Subject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1779222541504273975L;
	/*
	 * (non-javadoc)
	 */
	private Collection<Change> affectingChanges;
	private static int IDCounter = 0;
	private String uniqueID;

	public Subject() {
		
		affectingChanges = new ArrayList<Change>();
		
		uniqueID = "f" + Integer.toString(IDCounter);
		IDCounter++;
	}
	
	public String getID(){
		return uniqueID;
	}

	/**
	 * Getter of the property <tt>affectingChanges</tt>
	 * 
	 * @return Returns the affectingChanges.
	 * 
	 */

	public Collection<Change> getAffectingChanges() {
		return affectingChanges;
	}

	/**
	 * Setter of the property <tt>affectingChanges</tt>
	 * 
	 * @param affectingChanges
	 *            The affectingChanges to set.
	 * 
	 */
	public void setAffectingChanges(Collection<Change> affectingChanges) {
		this.affectingChanges = affectingChanges;
	}

	public void addChange(Change change) {
		this.affectingChanges.add(change);
	}

	public abstract String getFamixType();

	/**
	 * Finds the latest Addition Change related to this Subject
	 * 
	 * @return
	 */
	public Add getLatestAddition() {
		Add latestAddition = null;
		for (Change change : affectingChanges) {
			if (change instanceof Add) {
				if (latestAddition == null) {
					latestAddition = (Add) change;
				} else {
					if (latestAddition.getTimeStamp().compareTo(change.getTimeStamp()) < 0) {
						latestAddition = (Add) change;
					}
				}
			}
		}
		return latestAddition;
	}

	/**
	 * @return
	 */
	public Remove getLatestRemoval() {
		Remove latestRemoval = null;
		for (Change change : affectingChanges) {
			if (change instanceof Remove) {
				if (latestRemoval == null) {
					latestRemoval = (Remove) change;
				} else {
					if (latestRemoval.getTimeStamp().compareTo(change.getTimeStamp()) < 0) {
						latestRemoval = (Remove) change;
					}
				}
			}
		}
		return latestRemoval;
	}

	/**
	 * @return
	 */
	public Change getLatestChange() {
		Change latestChange = null;
		for (Change change : affectingChanges) {
			if (latestChange == null) {
				latestChange = change;
			} else {
				if (latestChange.getTimeStamp().compareTo(change.getTimeStamp()) < 0) {
					latestChange = change;
				}
			}
		}
		return latestChange;
	}

}
