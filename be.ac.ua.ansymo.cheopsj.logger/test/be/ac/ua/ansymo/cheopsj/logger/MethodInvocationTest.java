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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;



/**
 * @author quinten
 * 
 */
public class MethodInvocationTest extends AbstractCheopsJTest {

	/**
	 * The object that is being tested.
	 * 
	 * @see com.qualityeclipse.favorites.views.FavoritesView
	 */
	private IPackageFragment fPack1;
	private ICompilationUnit fcu1;
	private ICompilationUnit fcu2;
	private IType ftype1;
	private IType ftype2;
	/*private IField field1;
	private IMethod fmethod1;
	private IMethod fmethod2;
	private IMethod fmethod3;*/

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		// create pacakge
		fPack1 = fRoot1.createPackageFragment("pack1", true, null);

		// create one class
		fcu1 = fPack1.getCompilationUnit("BooClass.java");
		ftype1 = fcu1.createType("public class BooClass {\n}\n", null, true, null);
		ftype1.createMethod("public void boo() {}\n", null, true, null);

		// create another class
		fcu2 = fPack1.getCompilationUnit("FooClass.java");
		ftype2 = fcu2.createType("public class FooClass {\n}\n", null, true, null);
		ftype2.createField("private BooClass b", null, true, null);
		ftype2.createMethod("public void foo() {}\n", null, true, null);
		ftype2.createMethod("public void foo2() {}\n", null, true, null);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void addInvocationToLocalMethodTest() {

		try {
			ftype2.createMethod("public void foo() {\n foo2(); \n }\n", null, true, null);
			// Apperently this doesn't work this way :s

			assertTrue(manager.famixInvocationExists("pack1.FooClass.foo{pack1.FooClass.foo2}"));
			FamixInvocation invocation = manager.getFamixInvocation("pack1.FooClass.foo{pack1.FooClass.foo2}");
			List<Change> changes = (List<Change>) invocation.getAffectingChanges();
			assertEquals(1, changes.size());
			assertTrue(changes.get(0) instanceof Add);
			AtomicChange addition = (AtomicChange) changes.get(0);
			assertTrue(addition.getChangeSubject().equals(invocation));

			FamixMethod invoker = manager.getFamixMethod("pack1.FooClass.foo");
			FamixMethod invokee = manager.getFamixMethod("pack1.FooClass.foo2");

			assertTrue(invocation.getInvokedBy().equals(invoker));
			//assertTrue(invocation.getCandidate().equals(invokee));

			assertTrue(addition.getStructuralDependencies().contains(((List<Change>) invokee.getAffectingChanges()).get(0)));
			assertTrue(addition.getStructuralDependencies().contains(((List<Change>) invoker.getAffectingChanges()).get(0)));
		} catch (Exception e) {
			fail("Failed to create method with method invocation");
		}

	}

	@Test
	public void addIvocationToMethodInOtherClass() {

	}

	// TODO invocation of Static methods
	// TODO invocation with parameters

}
