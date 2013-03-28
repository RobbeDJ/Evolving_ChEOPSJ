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
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class ClassAndPackageRemovalTest {

	private ClassRecorder classrecorder;
	private PackageRecorder packrecorder;
	private ModelManager manager;
	private String packname = "be.ac.ua.test.pack";
	private String classname = "Boo";
	
	private AtomicChange packadd;
	private AtomicChange classadd;
	private Remove packrem;
	private Remove classrem;
	
	@Before
	public void setUp() throws Exception {
		manager = ModelManager.getInstance();
		createRecorderFromDeclaration();
		
		packadd = new Add();
		packrecorder.storeChange(packadd);
		classadd = new Add();
		classrecorder.storeChange(classadd);
		
		packrem = new Remove();
		classrem = new Remove();
	}

	private void createRecorderFromDeclaration() {
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

		classrecorder = new ClassRecorder(type);
		packrecorder = new PackageRecorder(pack);
	}

	@After
	public void tearDown() throws Exception {
		manager.clearModel();
	}
	
	@Test 
	public void test1(){
		//Package removal test with first removing class
		classrecorder.storeChange(classrem);
		packrecorder.storeChange(packrem);
		
		assertEquals(2,packrem.getStructuralDependencies().size());
		assertTrue(packrem.getStructuralDependencies().contains(classrem));
		assertTrue(packrem.getStructuralDependencies().contains(packadd));
		
	}
	
	@Test 
	public void test2(){
		//Package removal test without first removing class
		packrecorder.storeChange(packrem);
		FamixPackage pack = (FamixPackage) packrem.getChangeSubject();		
		FamixClass clazz = (FamixClass) pack.getClasses().toArray()[0];
		
		classrem = clazz.getLatestRemoval();
		
		assertEquals(2,packrem.getStructuralDependencies().size());
		assertTrue(packrem.getStructuralDependencies().contains(classrem));
		assertTrue(packrem.getStructuralDependencies().contains(packadd));
	}
}
