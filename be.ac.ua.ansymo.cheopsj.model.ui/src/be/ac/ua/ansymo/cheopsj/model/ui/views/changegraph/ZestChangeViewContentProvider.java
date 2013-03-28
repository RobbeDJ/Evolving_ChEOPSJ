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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerEvent;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerListener;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixBehaviouralEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixObject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;



public class ZestChangeViewContentProvider implements IGraphEntityContentProvider, ModelManagerListener {
	private ModelManager manager;
	private GraphViewer viewer;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		this.viewer = (GraphViewer) viewer;
		if (manager != null)
			manager.removeModelManagerListener(this);

		manager = (ModelManager) newInput;

		if (manager != null)
			manager.addModelManagerListener(this);

	}

	@Override
	public Object[] getElements(Object input) {

		Collection<Object> result = new ArrayList<Object>();
		result.addAll(manager.getChanges());
		result.addAll(manager.getFamixElements());
		
		for(FamixObject elem : manager.getFamixElements()){
			if(elem instanceof FamixBehaviouralEntity){
				result.addAll(((FamixBehaviouralEntity) elem).getLocalVariables());
			}
		}

		return result.toArray();
	}

	@Override
	public Object[] getConnectedTo(Object entity) {

		Collection<Object> result = new ArrayList<Object>();

		if (entity instanceof Change) {
			Change node = (Change) entity;
			result.addAll(node.getStructuralDependencies());

			if (entity instanceof AtomicChange) {
				result.add(((AtomicChange) entity).getChangeSubject());
			}

		} else if (entity instanceof FamixPackage) {
			result.add(((FamixPackage) entity).getBelongsToPackage());
		} else if (entity instanceof FamixClass) {
			result.add(((FamixClass) entity).getBelongsToPackage());
			result.add(((FamixClass) entity).getBelongsToClass());
		} else if (entity instanceof FamixMethod) {
			result.add(((FamixMethod) entity).getBelongsToClass());
			result.add(((FamixMethod) entity).getDeclaredReturnClass());
		} else if (entity instanceof FamixAttribute) {
			result.add(((FamixAttribute) entity).getBelongsToClass());
			result.add(((FamixAttribute) entity).getDeclaredClass());
		} else if (entity instanceof FamixInvocation) {
			result.add(((FamixInvocation) entity).getCandidate());
			result.add(((FamixInvocation) entity).getInvokedBy());
		} else if (entity instanceof FamixLocalVariable) {
			result.add(((FamixLocalVariable) entity).getDeclaredClass());
			result.add(((FamixLocalVariable) entity).getBelongsToBehaviour());
		}
		return result.toArray();
	}

	@Override
	public void changesAdded(final ModelManagerEvent event) {
		// If this is the UI thread, then make the change.
		if (Display.getCurrent() != null) {
			updateViewer(event);
			return;
		}

		// otherwise, redirect to execute on the UI thread.
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updateViewer(event);
			}
		});
	}

	@SuppressWarnings("restriction")
	private void updateViewer(ModelManagerEvent event) {
		// Use the setRedraw method to reduce flicker
		// when adding or removing multiple items in a table.
		viewer.getGraphControl().setRedraw(false);
		try {
			for (IChange change : event.getNewChanges()) {
				viewer.addNode((Change) change);
			}
		} finally {
			viewer.getGraphControl().setRedraw(true);
			viewer.refresh();
			viewer.applyLayout();
		}
	}

}
