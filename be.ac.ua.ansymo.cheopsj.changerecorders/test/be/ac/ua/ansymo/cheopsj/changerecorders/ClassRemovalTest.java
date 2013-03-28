package be.ac.ua.ansymo.cheopsj.changerecorders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;

public class ClassRemovalTest {

	private ClassRecorder recorder1;
	private ModelManager manager;
	private String packname = "be.ac.ua.test.pack";
	private String classname = "Boo";
	//private String nestedclassname = "Foo";
	//private String nestednestedclassname = "Fun";
	private AtomicChange addition;

	@Before
	public void setUp() throws Exception {
		manager = ModelManager.getInstance();
		recorder1 = createRecorderFromDeclaration();
		addition = new Add();
		recorder1.storeChange(addition);
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
	public void test1(){
		Remove rem = new Remove();
		recorder1.storeChange(rem);
		
		assertEquals(7,manager.getChanges().size());
		
		assertEquals(1,rem.getStructuralDependencies().size());
		assertTrue(rem.getStructuralDependencies().contains(addition));
	}
}
