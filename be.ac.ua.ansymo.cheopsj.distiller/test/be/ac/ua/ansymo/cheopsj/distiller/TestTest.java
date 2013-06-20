package be.ac.ua.ansymo.cheopsj.distiller;

import static org.junit.Assert.*;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.distiller.popup.actions.DistillChanges;
import be.ac.ua.ansymo.cheopsj.logger.JavaProjectHelper;
import be.ac.ua.ansymo.cheopsj.model.ModelManager;

public class TestTest {

	protected IJavaProject fJProject1;
	protected IPackageFragmentRoot fRoot1;
	protected ModelManager manager;

	/**
	 * Perform pre-test initialization.
	 */
	@Before
	public void setUp() throws Exception {
		/*manager = ModelManager.getInstance();

		// Add additional setup code here.
		// Create new project ...
		// fWorkspace= ResourcesPlugin.getWorkspace();
		fJProject1 = JavaProjectHelper.createJavaProject("TestProject1", "bin");//$NON-NLS-1$//$NON-NLS-2$
		fJProject1.setRawClasspath(new IClasspathEntry[0], null);
		fRoot1 = JavaProjectHelper.addSourceContainer(fJProject1, "src");//$NON-NLS-1$
		*/
	}

	/**
	 * Perform post-test cleanup.
	 */
	@After
	public void tearDown() throws Exception {
		/*
		// Add additional teardown code here.
		manager.clearModel();
		JavaProjectHelper.delete(fJProject1);

		// getJavaPage().hideView(testView);
		// getJavaPage().hideView(zestView);
		 * */
	}

	@Test
	public void test() {
		/*DistillChanges dc = new DistillChanges();
		dc.setSelectedProject(fJProject1.getProject());
		//fJProject1.
		String url = "https://subversion.assembla.com/svn/cheopsj-test/";
		//make repository
		dc.setRepositoryUrl(url);
		dc.run(null);*/
		fail("Not yet implemented");
	}

}
