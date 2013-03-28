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
package be.ac.ua.ansymo.cheopsj.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;


public class PackageTest extends AbstractCheopsJTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	private IPackageFragment createPackage(String pack) {
		try {
			return fRoot1.createPackageFragment(pack, true, null);
		} catch (Exception e) {
			fail("failed to create package");
		}
		return null;
	}

	private void removePackage(IPackageFragment pack) {
		try {
			pack.delete(true, new NullProgressMonitor());
		} catch (JavaModelException e) {
			fail("failed to remove package");
		}
	}

	/**
	 * Run the view test.
	 */
	@Test
	public void testAddPackage() {
		createPackage("pack1");

		assertTrue(manager.famixPackageExists("pack1"));
		FamixPackage packInSystem = manager.getFamixPackage("pack1");
		List<Change> changes = (List<Change>) packInSystem.getAffectingChanges();
		assertEquals(1, changes.size());
		assertTrue(changes.get(0) instanceof Add);
		AtomicChange addition = (AtomicChange) changes.get(0);
		assertTrue(addition.getChangeSubject().equals(packInSystem));
	}

	/**
	 * Run the view test.
	 */
	@Test
	public void testAddSubPackage() {
		createPackage("pack1");
		createPackage("pack1.subpack");

		assertTrue(manager.famixPackageExists("pack1"));
		FamixPackage superpack = manager.getFamixPackage("pack1");
		List<Change> changes = (List<Change>) superpack.getAffectingChanges();
		assertEquals(1, changes.size());
		assertTrue(changes.get(0) instanceof Add);
		AtomicChange addition = (AtomicChange) changes.get(0);
		assertTrue(addition.getChangeSubject().equals(superpack));

		assertTrue(manager.famixPackageExists("pack1.subpack"));
		FamixPackage subpack = manager.getFamixPackage("pack1.subpack");
		List<Change> subchanges = (List<Change>) subpack.getAffectingChanges();
		assertEquals(1, subchanges.size());
		assertTrue(subchanges.get(0) instanceof Add);
		AtomicChange addition2 = (AtomicChange) subchanges.get(0);
		assertTrue(addition2.getChangeSubject().equals(subpack));

		assertTrue(subpack.getBelongsToPackage().equals(superpack));
		assertTrue(superpack.getPackages().contains(subpack));
		assertTrue(addition2.getStructuralDependencies().contains(addition));
		assertTrue(addition.getStructuralDependees().contains(addition2));

	}

	@Test
	public void testRemovePackage() {
		IPackageFragment superpack = createPackage("pack1");
		removePackage(superpack);

		assertTrue(manager.famixPackageExists("pack1"));
		FamixPackage packInSystem = manager.getFamixPackage("pack1");
		List<Change> changes = (List<Change>) packInSystem.getAffectingChanges();
		assertEquals(2, changes.size());
		assertTrue(changes.get(0) instanceof Add);
		AtomicChange addition = (AtomicChange) changes.get(0);
		assertTrue(addition.getChangeSubject().equals(packInSystem));

		assertTrue(changes.get(1) instanceof Remove);
		Remove removal = (Remove) changes.get(1);
		assertTrue(removal.getChangeSubject().equals(packInSystem));

		assertTrue(removal.getStructuralDependencies().contains(addition));
		assertTrue(addition.getStructuralDependees().contains(removal));
	}

	@Test
	public void testAddRemoveAddPackage() {
		IPackageFragment pack = createPackage("pack1");
		removePackage(pack);
		createPackage("pack1");

		assertTrue(manager.famixPackageExists("pack1"));
		FamixPackage packInSystem = manager.getFamixPackage("pack1");
		List<Change> changes = (List<Change>) packInSystem.getAffectingChanges();
		assertEquals(3, changes.size());
		assertTrue(changes.get(0) instanceof Add);
		AtomicChange addition = (AtomicChange) changes.get(0);
		assertTrue(addition.getChangeSubject().equals(packInSystem));

		assertTrue(changes.get(1) instanceof Remove);
		Remove removal = (Remove) changes.get(1);
		assertTrue(removal.getChangeSubject().equals(packInSystem));

		assertTrue(changes.get(2) instanceof Add);
		AtomicChange secondaddition = (AtomicChange) changes.get(2);
		assertTrue(secondaddition.getChangeSubject().equals(packInSystem));

		assertTrue(removal.getStructuralDependencies().contains(addition));
		assertTrue(addition.getStructuralDependees().contains(removal));

		assertTrue(secondaddition.getStructuralDependencies().contains(removal));
		assertTrue(removal.getStructuralDependees().contains(secondaddition));
	}
}
