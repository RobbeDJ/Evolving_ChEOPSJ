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

package be.ac.ua.ansymo.cheopsj.changerecorders;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;

/**
 * @author quinten
 * 
 */
public class CompilationUnitRecorder extends AbstractEntityRecorder {

	ICompilationUnit element;

	public CompilationUnitRecorder(ICompilationUnit element) {
		this.element = element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * storeChange(be.ac.ua.cheopsj.Model.Changes.IChange)
	 */
	@Override
	public void storeChange(IChange change) {

		if (change instanceof Remove) {
			// super.storeChange(change);
			String filename = element.getElementName();

			String classname = filename.substring(0, filename.indexOf('.'));
			String uniquename = classname;
			if (element.getParent() instanceof IPackageFragment) {
				String packagename = ((IPackageFragment) element.getParent()).getElementName();

				uniquename = packagename + '.' + classname;
			}

			// System.out.println(classname);
			ClassRecorder recorder = new ClassRecorder(uniquename);
			recorder.createAndLinkChange((AtomicChange) change);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange(be.ac.ua.cheopsj.Model.Changes.AtomicChange)
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		// TODO Auto-generated method stub

	}

}
