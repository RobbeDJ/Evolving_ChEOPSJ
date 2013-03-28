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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class Change implements IChange, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1775934576119950076L;
	private Timestamp timeStamp;
	private boolean isApplied;
	private boolean isDummy;
	private String intent = "";
	private String user = "";
	private Collection<CompositeChange> composites;
	private Collection<Change> semanticalDependencies;
	private Collection<Change> structuralDependencies;
	private Collection<Change> semanticalDependees;
	private Collection<Change> structuralDependees;
	
	private String UniqueID;
	private static int IDCounter = 0;

	public Change() {
		setTimeStamp(now());

		composites = new ArrayList<CompositeChange>();
		semanticalDependees = new ArrayList<Change>();
		semanticalDependencies = new ArrayList<Change>();
		structuralDependees = new ArrayList<Change>();
		structuralDependencies = new ArrayList<Change>();
		
		UniqueID = "n" + Integer.toString(IDCounter);
		IDCounter++;
	}
	
	public String getID(){
		return UniqueID;
	}

	static public Timestamp now() {
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		return currentTimestamp;
	}

	public void undo() {
	}

	public void apply() {
	}

	/**
	 * Getter of the property <tt>timeStamp</tt>
	 * 
	 * @return Returns the timeStamp.
	 */
	@Override
	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Setter of the property <tt>timeStamp</tt>
	 * 
	 * @param timeStamp
	 *            The timeStamp to set.
	 */
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * Getter of the property <tt>isApplied</tt>
	 * 
	 * @return Returns the isApplied.
	 */
	public boolean getIsApplied() {
		return isApplied;
	}

	/**
	 * Setter of the property <tt>isApplied</tt>
	 * 
	 * @param isApplied
	 *            The isApplied to set.
	 */
	public void setIsApplied(boolean isApplied) {
		this.isApplied = isApplied;
	}

	/**
	 * Getter of the property <tt>intent</tt>
	 * 
	 * @return Returns the intent.
	 */
	public String getIntent() {
		return intent;
	}

	/**
	 * Setter of the property <tt>intent</tt>
	 * 
	 * @param intent
	 *            The intent to set.
	 */
	public void setIntent(String intent) {
		this.intent = intent;
	}

	/**
	 * Getter of the property <tt>user</tt>
	 * 
	 * @return Returns the user.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Setter of the property <tt>user</tt>
	 * 
	 * @param user
	 *            The user to set.
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Getter of the property <tt>composites</tt>
	 * 
	 * @return Returns the composites.
	 */
	public Collection<CompositeChange> getComposites() {
		return composites;
	}

	/**
	 * Setter of the property <tt>composites</tt>
	 * 
	 * @param composites
	 *            The composites to set.
	 * 
	 */
	public void setComposites(Collection<CompositeChange> composites) {
		this.composites = composites;
	}

	/**
	 * Getter of the property <tt>semanticalDependencies</tt>
	 * 
	 * @return Returns the semanticalDependencies.
	 * 
	 */
	public Collection<Change> getSemanticalDependencies() {
		return this.semanticalDependencies;
	}

	public void addSemanticalDependency(Change change) {
		this.semanticalDependees.add(change);
		if (!change.getSemanticalDependencies().contains(this))
			change.addSemanticalDependee(this);
	}

	public Collection<Change> getSemanticalDependees() {
		return this.semanticalDependees;
	}

	public void addSemanticalDependee(Change change) {
		this.semanticalDependees.add(change);
		if (!change.getSemanticalDependencies().contains(this))
			change.addSemanticalDependency(this);
	}

	/**
	 * Getter of the property <tt>structuralDependencies</tt>
	 * 
	 * @return Returns the structuralDependencies.
	 * 
	 */
	public Collection<Change> getStructuralDependencies() {
		return this.structuralDependencies;
	}

	public void addStructuralDependency(Change change) {
		this.structuralDependencies.add(change);
		if (!change.getStructuralDependees().contains(this))
			change.addStructuralDependee(this);
	}

	public Collection<Change> getStructuralDependees() {
		return this.structuralDependees;
	}

	public void addStructuralDependee(Change change) {
		this.structuralDependees.add(change);
		if (!change.getStructuralDependencies().contains(this))
			change.addStructuralDependency(this);
	}

	@Override
	public String getChangeType() {
		return "";
	}

	@Override
	public String getName() {
		return "";
	}

	// For now, this is how we suppress a warning that we cannot fix
	// See Bugzilla #163093 and Bugzilla #149805 comment #14
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public String getFamixType() {
		return "";
	}

	@Override
	public boolean equals(Object change) {
		boolean returnval = false;
		if (change instanceof Change) {
			returnval = getName().equals(((Change) change).getName());
			returnval = returnval && getChangeType().equals(((Change) change).getChangeType());
		}
		return returnval;
	}

	public String toString() {
		String returnval = "";
		returnval += getChangeType() + " of " + getFamixType() + " " + getName() + '\n';
		returnval += "Structural dependencies" + '\n';
		for (Change ch : this.structuralDependencies) {
			returnval += ch.getChangeType() + " of " + ch.getFamixType() + " " + ch.getName() + '\n';
		}
		returnval += "Structural dependees" + '\n';
		for (Change ch : this.structuralDependees) {
			returnval += ch.getChangeType() + " of " + ch.getFamixType() + " " + ch.getName() + '\n';
		}
		returnval += "Semantical dependencies" + '\n';
		for (Change ch : this.semanticalDependencies) {
			returnval += ch.getChangeType() + " of " + ch.getFamixType() + " " + ch.getName() + '\n';
		}
		returnval += "Semantical dependees" + '\n';
		for (Change ch : this.semanticalDependees) {
			returnval += ch.getChangeType() + " of " + ch.getFamixType() + " " + ch.getName() + '\n';
		}
		return returnval;
	}

	public Image getIcon() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

	public boolean isDummy() {
		return isDummy;
	}

	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
	}
}
