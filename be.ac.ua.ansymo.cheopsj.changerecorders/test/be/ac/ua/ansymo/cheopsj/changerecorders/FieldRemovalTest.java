package be.ac.ua.ansymo.cheopsj.changerecorders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;

public class FieldRemovalTest {

	private FieldRecorder recorder1;
	private FieldRecorder recorder2;
	private FieldRecorder recorder3;
	private ModelManager manager;
	private ClassRecorder crec;
	
	private String packname = "be.ac.ua.test.pack";
	private String classname = "Boo";
	private String declaredTypeName = "be.ac.ua.test.otherpack.Foo";
	
	private String intfieldname = "intfield";
	private String fieldname = "field";
	private String field3Name = "field3";
	
	private AtomicChange declaredclassadd;
	private AtomicChange classadd;
	private AtomicChange field1add;
	private AtomicChange field2add;
	private AtomicChange field3add;
	
	
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
		crec = new ClassRecorder(type);
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
		
		field1add = new Add();
		recorder1.storeChange(field1add);
		field2add = new Add();
		recorder2.storeChange(field2add);
		field3add = new Add();
		recorder3.storeChange(field3add);
	}
	
	@After
	public void tearDown(){
		manager.clearModel();
	}
	
	@Test
	public void test1() {
		Remove field1rem = new Remove();
		Remove field2rem = new Remove();
		Remove field3rem = new Remove();
		
		recorder1.storeChange(field1rem);
		recorder2.storeChange(field2rem);
		recorder3.storeChange(field3rem);
		
		assertEquals(1, field1rem.getStructuralDependencies().size());
		assertTrue(field1rem.getStructuralDependencies().contains(field1add));
		
		assertEquals(1, field2rem.getStructuralDependencies().size());
		assertTrue(field2rem.getStructuralDependencies().contains(field2add));
		
		assertEquals(1, field3rem.getStructuralDependencies().size());
		assertTrue(field3rem.getStructuralDependencies().contains(field3add));
	}
	
	
	
	@Test
	public void test2(){
		Remove crem = new Remove();
		crec.storeChange(crem);
		
		assertEquals(15,manager.getChanges().size());
		assertTrue(crem.getStructuralDependencies().contains(classadd));
		assertEquals(4,crem.getStructuralDependencies().size());
		
		Object[] changes = crem.getStructuralDependencies().toArray();
		
		assertTrue(changes[1] instanceof Remove);
		assertTrue(((AtomicChange)changes[1]).getChangeSubject() instanceof FamixAttribute);
		assertEquals(packname+"."+classname+"."+intfieldname,((FamixAttribute)((AtomicChange)changes[1]).getChangeSubject()).getUniqueName());
		
		assertTrue(changes[2] instanceof Remove);
		assertTrue(((AtomicChange)changes[2]).getChangeSubject() instanceof FamixAttribute);
		assertEquals(packname+"."+classname+"."+fieldname,((FamixAttribute)((AtomicChange)changes[2]).getChangeSubject()).getUniqueName());
		
		assertTrue(changes[3] instanceof Remove);
		assertTrue(((AtomicChange)changes[3]).getChangeSubject() instanceof FamixAttribute);
		assertEquals(packname+"."+classname+"."+field3Name,((FamixAttribute)((AtomicChange)changes[3]).getChangeSubject()).getUniqueName());
		
		assertTrue(changes[0] instanceof Add);
		assertTrue(((AtomicChange)changes[0]).getChangeSubject() instanceof FamixClass);
		assertEquals(packname+"."+classname,((FamixClass)((AtomicChange)changes[0]).getChangeSubject()).getUniqueName());
	}
	
	@Test
	public void test2b(){
		Remove field1rem = new Remove();
		recorder1.storeChange(field1rem);
				
		Remove crem = new Remove();
		crec.storeChange(crem);
		
		assertTrue(crem.getStructuralDependencies().contains(field1rem));
		assertTrue(crem.getStructuralDependencies().contains(classadd));
		assertEquals(4,crem.getStructuralDependencies().size());
		
		Object[] changes = crem.getStructuralDependencies().toArray();
		
		assertTrue(changes[1] instanceof Remove);
		assertTrue(((AtomicChange)changes[1]).getChangeSubject() instanceof FamixAttribute);
		assertEquals(packname+"."+classname+"."+intfieldname,((FamixAttribute)((AtomicChange)changes[1]).getChangeSubject()).getUniqueName());
		
		assertTrue(changes[2] instanceof Remove);
		assertTrue(((AtomicChange)changes[2]).getChangeSubject() instanceof FamixAttribute);
		assertEquals(packname+"."+classname+"."+fieldname,((FamixAttribute)((AtomicChange)changes[2]).getChangeSubject()).getUniqueName());
		
		assertTrue(changes[3] instanceof Remove);
		assertTrue(((AtomicChange)changes[3]).getChangeSubject() instanceof FamixAttribute);
		assertEquals(packname+"."+classname+"."+field3Name,((FamixAttribute)((AtomicChange)changes[3]).getChangeSubject()).getUniqueName());
		
		assertTrue(changes[0] instanceof Add);
		assertTrue(((AtomicChange)changes[0]).getChangeSubject() instanceof FamixClass);
		assertEquals(packname+"."+classname,((FamixClass)((AtomicChange)changes[0]).getChangeSubject()).getUniqueName());
		
		assertEquals(15,manager.getChanges().size());
	}
	
	
}
