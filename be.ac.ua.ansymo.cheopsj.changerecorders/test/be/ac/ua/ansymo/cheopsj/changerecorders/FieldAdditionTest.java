package be.ac.ua.ansymo.cheopsj.changerecorders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

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
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.evolizer.changedistiller.ChangeDistiller;
import org.evolizer.changedistiller.distilling.FileDistiller;
import org.evolizer.changedistiller.model.entities.SourceCodeChange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;

public class FieldAdditionTest {

	private FieldRecorder recorder1;
	private FieldRecorder recorder2;
	private FieldRecorder recorder3;
	private ModelManager manager;

	private String packname = "be.ac.ua.test.pack";
	private String classname = "Boo";
	private String declaredTypeName = "be.ac.ua.test.otherpack.Foo";

	private String intfieldname = "intfield";
	private String fieldname = "field";
	private String field3Name = "field3";

	private AtomicChange declaredclassadd;
	private AtomicChange classadd;


	@Before
	public void setUp(){
		manager = ModelManager.getInstance();

		AST ast2 = AST.newAST(AST.JLS3);
		CompilationUnit cu2 = ast2.newCompilationUnit();
		PackageDeclaration pack2 = ast2.newPackageDeclaration();
		pack2.setName(ast2.newName("be.ac.ua.test.otherpack"));
		cu2.setPackage(pack2);
		TypeDeclaration type2 = ast2.newTypeDeclaration();
		type2.setName(ast2.newSimpleName("Foo"));

		cu2.types().add(type2);//created mock compilationunit containing package and class

		PackageRecorder prec2 = new PackageRecorder(pack2);
		prec2.storeChange(new Add());//store the package addition
		ClassRecorder crec2 = new ClassRecorder(type2);
		declaredclassadd = new Add();
		crec2.storeChange(declaredclassadd);



		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		PackageDeclaration pack = ast.newPackageDeclaration();
		pack.setName(ast.newName(packname));
		cu.setPackage(pack);
		TypeDeclaration type = ast.newTypeDeclaration();
		type.setName(ast.newSimpleName(classname));

		cu.types().add(type);

		PackageRecorder prec = new PackageRecorder(pack);
		prec.storeChange(new Add());//store the package addition
		ClassRecorder crec = new ClassRecorder(type);
		classadd = new Add();
		crec.storeChange(classadd);

		//Class and package created and changes logged, now create the Field.

		VariableDeclarationFragment frag1 = ast.newVariableDeclarationFragment();
		frag1.setName(ast.newSimpleName(intfieldname));
		FieldDeclaration field =ast.newFieldDeclaration(frag1);
		field.setType(ast.newPrimitiveType(PrimitiveType.INT));//field has type int
		type.bodyDeclarations().add(field);

		VariableDeclarationFragment frag2 = ast.newVariableDeclarationFragment();
		frag2.setName(ast.newSimpleName(fieldname));
		FieldDeclaration field2 =ast.newFieldDeclaration(frag2);
		field2.setType(ast.newSimpleType(ast.newName(declaredTypeName)));//field has type Foo
		type.bodyDeclarations().add(field2);

		VariableDeclarationFragment frag3 = ast.newVariableDeclarationFragment();
		frag3.setName(ast.newSimpleName(field3Name));
		FieldDeclaration field3 =ast.newFieldDeclaration(frag3);
		field3.setType(ast.newSimpleType(ast.newName("Foo")));//field has type Foo
		type.bodyDeclarations().add(field3);

		ImportDeclaration imp = ast.newImportDeclaration();
		imp.setName(ast.newName(declaredTypeName));
		cu.imports().add(imp);

		//created mock compilationunit containing package and class

		recorder1 = new FieldRecorder(field);
		recorder2 = new FieldRecorder(field2);
		recorder3 = new FieldRecorder(field3);
	}


	@After
	public void tearDown(){
		manager.clearModel();
	}

	@Test
	public void test0(){
		try {
			Field uniqueNameField = FieldRecorder.class.getDeclaredField("uniquename");
			uniqueNameField.setAccessible(true);
			String fieldValue = (String) uniqueNameField.get(recorder1);
			assertEquals(packname+"."+classname+"."+intfieldname, fieldValue);

			Field parentField = FieldRecorder.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			FamixEntity fieldValue2 = (FamixEntity)parentField.get(recorder1);
			assertEquals(packname+"."+classname,fieldValue2.getUniqueName());

			Field declaredClassField = FieldRecorder.class.getDeclaredField("declaredClass");
			declaredClassField.setAccessible(true);
			FamixEntity fieldValue3 = (FamixEntity)declaredClassField.get(recorder1);
			assertEquals(null,fieldValue3);

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test0b(){
		try {
			Field uniqueNameField = FieldRecorder.class.getDeclaredField("uniquename");
			uniqueNameField.setAccessible(true);
			String fieldValue = (String) uniqueNameField.get(recorder2);
			assertEquals(packname+"."+classname+"."+fieldname, fieldValue);

			Field parentField = FieldRecorder.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			FamixEntity fieldValue2 = (FamixEntity)parentField.get(recorder2);
			assertEquals(packname+"."+classname,fieldValue2.getUniqueName());

			Field declaredClassField = FieldRecorder.class.getDeclaredField("declaredClass");
			declaredClassField.setAccessible(true);
			FamixEntity fieldValue3 = (FamixEntity)declaredClassField.get(recorder2);
			assertEquals(declaredTypeName,fieldValue3.getUniqueName());

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test0c(){
		try {
			Field uniqueNameField = FieldRecorder.class.getDeclaredField("uniquename");
			uniqueNameField.setAccessible(true);
			String fieldValue = (String) uniqueNameField.get(recorder3);
			assertEquals(packname+"."+classname+"."+field3Name, fieldValue);

			Field parentField = FieldRecorder.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			FamixEntity fieldValue2 = (FamixEntity)parentField.get(recorder3);
			assertEquals(packname+"."+classname,fieldValue2.getUniqueName());

			Field declaredClassField = FieldRecorder.class.getDeclaredField("declaredClass");
			declaredClassField.setAccessible(true);
			FamixEntity fieldValue3 = (FamixEntity)declaredClassField.get(recorder3);
			assertEquals(declaredTypeName,fieldValue3.getUniqueName());

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test0d(){

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject project  = root.getProject("TestProject");
			IJavaProject javaProject = JavaCore.create(project);

			IPackageFragment mypackage = javaProject.findPackageFragment(new Path("/TestProject/src/be/ac/ua/test/pack"));
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				ICompilationUnit cu = mypackage.getCompilationUnits()[0]; 
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
	public void test0e(){
		/*try {
			FieldRecorder rec = new FieldRecorder(MockSourceCodeEntity.getMockFieldEntity(fieldname), MockSourceCodeEntity.getMockTypeEntity(packname+"."+classname));

			Field uniqueNameField = FieldRecorder.class.getDeclaredField("uniquename");
			uniqueNameField.setAccessible(true);
			String fieldValue = (String) uniqueNameField.get(rec);
			assertEquals(fieldValue, fieldname);

			Field parentField = FieldRecorder.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			FamixEntity fieldValue2 = (FamixEntity)parentField.get(rec);
			assertEquals(packname+"."+classname, fieldValue2.getUniqueName());
		} catch (SecurityException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		}*/

		FileDistiller distiller = ChangeDistiller.createFileDistiller(ChangeDistiller.Language.JAVA);

		File left = new File("/Users/quinten/Desktop/CHEOPSWorkspace/be.ac.ua.ansymo.cheopsj.changerecorders/testfiles/ClassA.java");
		File right = new File("/Users/quinten/Desktop/CHEOPSWorkspace/be.ac.ua.ansymo.cheopsj.changerecorders/testfiles/ClassAb.java"); 

		distiller.extractClassifiedSourceCodeChanges(left, right);
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();

		assertEquals(1, changes.size());
		FieldRecorder rec = new FieldRecorder(changes.get(0).getChangedEntity(),changes.get(0).getParentEntity());
		try{
			Field uniqueNameField = FieldRecorder.class.getDeclaredField("uniquename");
			uniqueNameField.setAccessible(true);
			String fieldValue = (String) uniqueNameField.get(rec);
			assertEquals("field",fieldValue);

			Field parentField = FieldRecorder.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			FamixEntity fieldValue2 = (FamixEntity)parentField.get(rec);
			assertEquals("be.ac.ua.test.pack.ClassA", fieldValue2.getUniqueName());
		} catch (SecurityException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test(){
		//test what happens when field has a primitive type and a class as type.
		AtomicChange field1Add = new Add();
		AtomicChange field2Add = new Add();
		AtomicChange field3Add = new Add();
		recorder1.storeChange(field1Add);
		recorder2.storeChange(field2Add);
		recorder3.storeChange(field3Add);

		//field 1 is a field with a primitive type "int"
		assertEquals(1,field1Add.getStructuralDependencies().size());
		assertTrue(field1Add.getStructuralDependencies().contains(classadd));
		//field 2 has a fully qualified class as type "be.ac.ua.test.otherpack.Foo"
		assertEquals(2,field2Add.getStructuralDependencies().size());
		assertTrue(field2Add.getStructuralDependencies().contains(classadd));
		assertTrue(field2Add.getStructuralDependencies().contains(declaredclassadd));
		//field 3 only has the classname as type "Foo", FieldRecorder finds the fully qualified name in the import declaration.
		assertEquals(2, field3Add.getStructuralDependencies().size());
		assertTrue(field3Add.getStructuralDependencies().contains(classadd));
		assertTrue(field3Add.getStructuralDependencies().contains(declaredclassadd));

	}

}
