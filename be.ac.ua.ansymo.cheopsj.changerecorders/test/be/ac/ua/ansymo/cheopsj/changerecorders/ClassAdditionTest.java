package be.ac.ua.ansymo.cheopsj.changerecorders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class ClassAdditionTest {

	private ClassRecorder recorder1;
	private ModelManager manager;
	private String packname = "be.ac.ua.test.pack";
	private String classname = "TestClass";
	private String nestedclassname = "Foo";
	private String nestednestedclassname = "Fun";

	@Before
	public void setUp() throws Exception {
		manager = ModelManager.getInstance();
		recorder1 = createRecorderFromDeclaration();
	}

	private ClassRecorder createRecorderFromDeclaration() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		PackageDeclaration pack = ast.newPackageDeclaration();
		Name pname = ast.newName(packname);
		pack.setName(pname);
		cu.setPackage(pack);

		TypeDeclaration type = ast.newTypeDeclaration();
		SimpleName tname = ast.newSimpleName(classname);
		type.setName(tname);

		cu.types().add(type);//created mock compilationunit containing package and class

		PackageRecorder prec = new PackageRecorder(pack);
		prec.storeChange(new Add());//store the package addition

		return new ClassRecorder(type);
	}

	@After
	public void tearDown() throws Exception {
		manager.clearModel();
	}

	@Test
	public void test0(){
		try {
			Field uniqueNameField = ClassRecorder.class.getDeclaredField("uniqueName");
			uniqueNameField.setAccessible(true);
			String fieldValue = (String) uniqueNameField.get(recorder1);
			assertEquals(fieldValue, packname+"."+classname);

			Field parentField = ClassRecorder.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			FamixEntity fieldValue2 = (FamixEntity)parentField.get(recorder1);

			assertEquals(fieldValue2.getUniqueName(), packname);

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test0b(){
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		TypeDeclaration type = ast.newTypeDeclaration();
		SimpleName tname = ast.newSimpleName(classname);
		type.setName(tname);

		cu.types().add(type);//created mock compilationunit containing class

		ClassRecorder recorder = new ClassRecorder(type);


		try {
			Field uniqueNameField = ClassRecorder.class.getDeclaredField("uniqueName");
			uniqueNameField.setAccessible(true);
			String fieldValue = (String) uniqueNameField.get(recorder);
			assertEquals(classname, fieldValue);

			Field parentField = ClassRecorder.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			FamixEntity fieldValue2 = (FamixEntity)parentField.get(recorder);

			assertEquals(null, fieldValue2);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test0c(){

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject project  = root.getProject("TestProject");
			IJavaProject javaProject = JavaCore.create(project);

			IPackageFragment mypackage = javaProject.findPackageFragment(new Path("/TestProject/src/be/ac/ua/test/pack"));
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				ICompilationUnit cu = mypackage.getCompilationUnits()[1]; 
				IType[] types = cu.getAllTypes();
				ClassRecorder recorder = new ClassRecorder(types[0]);
				
				Field uniqueNameField = ClassRecorder.class.getDeclaredField("uniqueName");
				uniqueNameField.setAccessible(true);
				String fieldValue = (String) uniqueNameField.get(recorder);
				assertEquals(fieldValue, packname+"."+classname);
				
				Field parentField = ClassRecorder.class.getDeclaredField("parent");
				parentField.setAccessible(true);
				FamixEntity fieldValue2 = (FamixEntity)parentField.get(recorder);
				assertEquals(packname, fieldValue2.getUniqueName());
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}

	}

	@Test
	public void test0d(){
		recorder1.storeChange(new Add());
		
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject project  = root.getProject("TestProject");
			IJavaProject javaProject = JavaCore.create(project);

			IPackageFragment mypackage = javaProject.findPackageFragment(new Path("/TestProject/src/be/ac/ua/test/pack"));
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				ICompilationUnit cu = mypackage.getCompilationUnits()[1]; 
				IType[] types = cu.getAllTypes();
				ClassRecorder recorder = new ClassRecorder(types[0].getTypes()[0]);
				
				Field uniqueNameField = ClassRecorder.class.getDeclaredField("uniqueName");
				uniqueNameField.setAccessible(true);
				String fieldValue = (String) uniqueNameField.get(recorder);
				assertEquals(fieldValue, packname+"."+classname + "." + nestedclassname);
				
				Field parentField = ClassRecorder.class.getDeclaredField("parent");
				parentField.setAccessible(true);
				FamixEntity fieldValue2 = (FamixEntity)parentField.get(recorder);
				assertEquals(packname+"."+classname, fieldValue2.getUniqueName());
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}

	}

	@Test
	public void test1() {
		recorder1.createAndLinkFamixElement();

		assertTrue(manager.famixClassExists(packname+"."+classname));
		FamixClass clazz = manager.getFamixClass(packname+"."+classname);
		assertEquals(packname,clazz.getBelongsToPackage().getUniqueName());
		assertTrue(clazz.getBelongsToPackage().getClasses().contains(clazz));
	}

	@Test
	public void test2() {
		AtomicChange add = new Add();
		recorder1.createAndLinkFamixElement();
		recorder1.createAndLinkChange(add);
		assertEquals(manager.getChanges().size(), 6); //5 for the packages + 1 for the class
		AtomicChange packadd = ((FamixClass)add.getChangeSubject()).getBelongsToPackage().getLatestAddition();
		assertEquals(1,add.getStructuralDependencies().size());
		assertTrue(add.getStructuralDependencies().contains(packadd));
		assertTrue(packadd.getStructuralDependees().contains(add));
	}

	@Test
	public void test3(){
		AtomicChange parentAdd = new Add();
		recorder1.storeChange(parentAdd);

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		PackageDeclaration pack = ast.newPackageDeclaration();
		Name pname = ast.newName(packname);
		pack.setName(pname);
		cu.setPackage(pack);

		TypeDeclaration type = ast.newTypeDeclaration();
		SimpleName tname = ast.newSimpleName(classname);
		type.setName(tname);

		TypeDeclaration nestedtype = ast.newTypeDeclaration();
		SimpleName ntname = ast.newSimpleName(nestedclassname);
		nestedtype.setName(ntname);

		type.bodyDeclarations().add(nestedtype);

		cu.types().add(type);//created mock compilationunit containing package and class

		ClassRecorder recorder = new ClassRecorder(nestedtype);
		AtomicChange add = new Add();
		recorder.storeChange(add);

		//TEST a nested class inside a normal class
		assertEquals(7, manager.getChanges().size());
		assertTrue(add.getStructuralDependencies().contains(parentAdd));

		FamixClass containingClass = (FamixClass)parentAdd.getChangeSubject();
		FamixClass clazz = (FamixClass)add.getChangeSubject();
		assertEquals(clazz.getBelongsToClass(), containingClass);

		//TEST Further nested class inside a nested class
		TypeDeclaration nestednestedtype = ast.newTypeDeclaration();
		SimpleName nntname = ast.newSimpleName(nestednestedclassname);
		nestednestedtype.setName(nntname);

		nestedtype.bodyDeclarations().add(nestednestedtype);

		ClassRecorder recorder2 = new ClassRecorder(nestednestedtype);
		AtomicChange add2 = new Add();
		recorder2.storeChange(add2);

		assertEquals(8, manager.getChanges().size());
		assertTrue(add2.getStructuralDependencies().contains(add));

		FamixClass clazz2 = (FamixClass)add2.getChangeSubject();
		assertEquals(clazz2.getBelongsToClass(), clazz);
	}

	@Test
	public void test(){
		//Addition after Removal

		AtomicChange firstAdd = new Add();
		recorder1.storeChange(firstAdd);
		Remove rem = new Remove();
		recorder1.storeChange(rem);
		AtomicChange secondAdd = new Add();
		recorder1.storeChange(secondAdd);

		FamixClass clazz = (FamixClass)firstAdd.getChangeSubject();
		Change change = (Change) firstAdd.getStructuralDependencies().toArray()[0];
		FamixPackage pack = (FamixPackage)((AtomicChange)change).getChangeSubject();

		assertEquals(3,clazz.getAffectingChanges().size());
		assertEquals(2,secondAdd.getStructuralDependencies().size());
		assertTrue(secondAdd.getStructuralDependencies().contains(rem));
		assertTrue(secondAdd.getStructuralDependencies().contains(pack.getLatestAddition()));

		assertEquals(1,rem.getStructuralDependencies().size());
		assertTrue(rem.getStructuralDependencies().contains(firstAdd));
		assertEquals(1,firstAdd.getStructuralDependencies().size());
		assertTrue(firstAdd.getStructuralDependencies().contains(pack.getLatestAddition()));


	}

}
