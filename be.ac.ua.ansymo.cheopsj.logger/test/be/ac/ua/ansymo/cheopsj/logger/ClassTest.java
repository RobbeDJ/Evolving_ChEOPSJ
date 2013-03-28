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
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;


public class ClassTest extends AbstractCheopsJTest {

	/**
	 * The object that is being tested.
	 * 
	 */

	private IPackageFragment fPack1;
	private ICompilationUnit fcu;

	/**
	 * Perform pre-test initialization.
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		fPack1 = fRoot1.createPackageFragment("pack1", true, null);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void addClass() {
		try {
			fcu = fPack1.getCompilationUnit("TestClass.java");
			fcu.createType("public class TestClass {\n}\n", null, true, null);
		} catch (Exception e) {
			fail("failed to create package or compilation unit");
		}
	}

	public void removeClass() {
		try {
			fcu.delete(true, new NullProgressMonitor());
		} catch (Exception e) {
			fail("failed to remove compilation unit");
		}
	}

	/**
	 * Run the view test.
	 */
	@Test
	public void testAddClass() {
		addClass();

		assertTrue(manager.famixPackageExists("pack1"));
		FamixPackage packInSystem = manager.getFamixPackage("pack1");
		AtomicChange packAddition = packInSystem.getLatestAddition();

		assertTrue(manager.famixClassExists("pack1.TestClass"));
		FamixClass classInSystem = manager.getFamixClass("pack1.TestClass");
		List<Change> changes = (List<Change>) classInSystem.getAffectingChanges();
		assertEquals(1, changes.size());
		assertTrue(changes.get(0) instanceof Add);
		AtomicChange addition = (AtomicChange) changes.get(0);
		assertTrue(addition.getChangeSubject().equals(classInSystem));

		assertTrue(addition.getStructuralDependencies().contains(packAddition));
		assertTrue(packAddition.getStructuralDependees().contains(addition));

	}

	/**
	 * Run the view test.
	 */
	@Test
	public void testRemoveClass() {

		addClass();
		removeClass();

		assertTrue(manager.famixClassExists("pack1.TestClass"));
		FamixClass classInSystem = manager.getFamixClass("pack1.TestClass");
		List<Change> changes = (List<Change>) classInSystem.getAffectingChanges();
		assertEquals(2, changes.size());
		assertTrue(changes.get(0) instanceof Add);
		AtomicChange addition = (AtomicChange) changes.get(0);
		assertTrue(changes.get(1) instanceof Remove);
		Remove removal = (Remove) changes.get(1);

		assertTrue(removal.getStructuralDependencies().contains(addition));
		assertTrue(addition.getStructuralDependees().contains(removal));
	}

	@Test
	public void testRemovePackageThatContainsClass() {
		addClass();
		removeClass();
		try {
			fPack1.delete(true, new NullProgressMonitor());
		} catch (JavaModelException e) {
			fail("failed to remove package");
		}

		assertTrue(manager.famixPackageExists("pack1"));
		assertEquals(2, manager.getFamixPackage("pack1").getAffectingChanges().size());
		// TODO check that this change is actually an addition!!!
		assertTrue(manager.famixClassExists("pack1.TestClass"));
		assertEquals(2, manager.getFamixClass("pack1.TestClass").getAffectingChanges().size());
		// TODO check that these changes are actually an addition and a
		// remove!!!
	}
}
