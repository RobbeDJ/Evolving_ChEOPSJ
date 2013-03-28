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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MethodTest extends AbstractCheopsJTest {

	/**
	 * The object that is being tested.
	 * 
	 * @see com.qualityeclipse.favorites.views.FavoritesView
	 */
	private IPackageFragment fPack1;
	private ICompilationUnit fcu;
	private IType ftype;
	private IMethod fmethod;

	/**
	 * Perform pre-test initialization.
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		fPack1 = fRoot1.createPackageFragment("pack1", true, null);
		fcu = fPack1.getCompilationUnit("TestClass.java");
		ftype = fcu.createType("public class TestClass {\n}\n", null, true, null);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void addSomeMethods() {
		try {
			fmethod = ftype.createMethod("public void a() {}\n", null, true, null);
			ftype.createMethod("public void b(java.util.Vector v) {}\n", null, true, null);
		} catch (Exception e) {
			fail("failed to create package or compilation unit");
		}
	}

	/**
	 * Run the view test.
	 */
	@Test
	public void testAddMethod() {
		addSomeMethods();
		assertTrue(manager.famixMethodExists("pack1.TestClass.a"));
		assertTrue(manager.famixMethodExists("pack1.TestClass.b"));
		assertEquals(1, manager.getFamixMethod("pack1.TestClass.a").getAffectingChanges().size());
		assertEquals(1, manager.getFamixMethod("pack1.TestClass.b").getAffectingChanges().size());
		// TODO check that this change is actually an addition!!!
	}

	@Test
	public void testRemoveMethod() {
		addSomeMethods();
		try {
			fmethod.delete(true, new NullProgressMonitor());
		} catch (JavaModelException e) {
			fail("failed to delete method");
		}

		assertTrue(manager.famixMethodExists("pack1.TestClass.a"));
		assertTrue(manager.famixMethodExists("pack1.TestClass.b"));
		assertEquals(2, manager.getFamixMethod("pack1.TestClass.a").getAffectingChanges().size());
		assertEquals(1, manager.getFamixMethod("pack1.TestClass.b").getAffectingChanges().size());
	}

}
