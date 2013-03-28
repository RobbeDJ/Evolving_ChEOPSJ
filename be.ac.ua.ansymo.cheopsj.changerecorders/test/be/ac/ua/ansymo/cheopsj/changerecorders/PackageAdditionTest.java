package be.ac.ua.ansymo.cheopsj.changerecorders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class PackageAdditionTest {
	private String p1 = "be";
	private String p2 = p1 + ".ac";
	private String p3 = p2 + ".ua";
	private String p4 = p3 + ".test";
	private String p5 = p4 + ".pack";
	private PackageRecorder recorder1;
	private ModelManager manager;

	@Before
	public void setUp() throws Exception {
		manager = ModelManager.getInstance();
		createRecorderFromDeclaration();
	}

	private void createRecorderFromDeclaration() {
		AST ast = AST.newAST(AST.JLS3);
		PackageDeclaration declaration = ast.newPackageDeclaration();
		Name name = ast.newName(p5);
		declaration.setName(name);
		recorder1 = new PackageRecorder(declaration);
	}

	@After
	public void tearDown() throws Exception {
		manager.clearModel();
	}

	@Test
	public void test0(){
		//Test if PackageRecorder is correctly instantiated.
		try {
			Field uniqueNameField = PackageRecorder.class.getDeclaredField("uniqueName");
			uniqueNameField.setAccessible(true);
			String fieldValue = (String) uniqueNameField.get(recorder1);
			assertEquals(fieldValue, p5);
		}  catch (SecurityException e) {
			e.printStackTrace();
			fail();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test0b(){

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project  = root.getProject("TestProject");
		IJavaProject javaProject = JavaCore.create(project);

		try {
			IPackageFragment mypackage = javaProject.findPackageFragment(new Path("/TestProject/src/be/ac/ua/test/pack"));
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				PackageRecorder recorder = new PackageRecorder(mypackage);
				
				Field uniqueNameField = PackageRecorder.class.getDeclaredField("uniqueName");
				uniqueNameField.setAccessible(true);
				String fieldValue = (String) uniqueNameField.get(recorder);
				assertEquals(fieldValue, p5);
				
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			fail();
		} catch (SecurityException e) {
			e.printStackTrace();
			fail();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test1() {
		//Test if all the packages exist!
		recorder1.createAndLinkFamixElement();
		assertTrue(manager.famixPackageExists(p1));
		assertTrue(manager.famixPackageExists(p2));
		assertTrue(manager.famixPackageExists(p3));
		assertTrue(manager.famixPackageExists(p4));
		assertTrue(manager.famixPackageExists(p5));
	}

	@Test
	public void test2(){
		//Test if the existing packages are correctly linked
		recorder1.createAndLinkFamixElement();
		FamixPackage pack = manager.getFamixPackage(p5);
		assertEquals(pack.getBelongsToPackage().getUniqueName(),p4);
		pack = manager.getFamixPackage(p4);
		assertEquals(pack.getBelongsToPackage().getUniqueName(),p3);
		pack = manager.getFamixPackage(p3);
		assertEquals(pack.getBelongsToPackage().getUniqueName(),p2);
		pack = manager.getFamixPackage(p2);
		assertEquals(pack.getBelongsToPackage().getUniqueName(),p1);
		pack = manager.getFamixPackage(p1);
		assertNull(pack.getBelongsToPackage());
	}

	@Test
	public void test3(){
		//Test if the changes exist (i.e. an addition for each of the packages)
		recorder1.createAndLinkFamixElement();
		recorder1.createAndLinkChange(new Add());

		assertEquals(5, manager.getChanges().size());

		packageHasAddition(p5);
		packageHasAddition(p4);
		packageHasAddition(p3);
		packageHasAddition(p2);
		packageHasAddition(p1);
	}

	private void packageHasAddition(String name) {
		FamixPackage pack;
		pack = manager.getFamixPackage(name);
		assertTrue(pack.getAffectingChanges().size() == 1);
		assertNotNull(pack.getLatestAddition());
		assertNotNull(pack.getLatestAddition().getChangeSubject());
		assertTrue(pack.getLatestAddition().getChangeSubject().equals(pack));
	}

	@Test
	public void test4(){
		//Test if the changes are correctly linked
		recorder1.createAndLinkFamixElement();
		recorder1.createAndLinkChange(new Add());

		additionDependsOnParentAddition(p5,p4);
		additionDependsOnParentAddition(p4,p3);
		additionDependsOnParentAddition(p3,p2);
		additionDependsOnParentAddition(p2,p1);

		FamixPackage pack = manager.getFamixPackage(p1);
		AtomicChange addition = pack.getLatestAddition();
		assertEquals(5, manager.getChanges().size());
		assertTrue(addition.getStructuralDependencies().isEmpty());
	}

	private void additionDependsOnParentAddition(String name, String parentname) {
		FamixPackage pack = manager.getFamixPackage(name);
		AtomicChange addition = pack.getLatestAddition();
		assertFalse(addition.getStructuralDependencies().isEmpty());
		FamixPackage pack2 = manager.getFamixPackage(parentname);
		AtomicChange addition2 = pack2.getLatestAddition();
		assertTrue(addition.getStructuralDependencies().contains(addition2));
	}

	@Test
	public void test5(){
		//Test addition of p5, when the superpackage p4 already exists.
		PackageRecorder prec = new PackageRecorder(p4);
		prec.storeChange(new Add());

		int oldsize = manager.getChanges().size();
		assertFalse(manager.famixPackageExists(p5));
		recorder1.storeChange(new Add());
		assertTrue(manager.famixPackageExists(p5));
		assertEquals(oldsize + 1, manager.getChanges().size());

		additionDependsOnParentAddition(p5,p4);
	}

	@Test
	public void test6(){
		//Test addition of p5, after it was previously removed.
		PackageRecorder prec = new PackageRecorder(p5);
		AtomicChange add1 = new Add();
		Remove rem = new Remove();
		AtomicChange add2 = new Add();

		prec.storeChange(add1);
		prec.storeChange(rem);
		prec.storeChange(add2);

		assertEquals(7, manager.getChanges().size());

		FamixPackage pack = manager.getFamixPackage(p5);
		AtomicChange addp = pack.getLatestAddition();
		Remove remp = pack.getLatestRemoval();

		assertEquals(add2, addp);
		assertEquals(rem, remp);

		assertTrue(add2.getStructuralDependencies().contains(rem));
		assertTrue(rem.getStructuralDependees().contains(add2));
		assertTrue(rem.getStructuralDependencies().contains(add1));
		assertTrue(add1.getStructuralDependees().contains(rem));
	}
}
