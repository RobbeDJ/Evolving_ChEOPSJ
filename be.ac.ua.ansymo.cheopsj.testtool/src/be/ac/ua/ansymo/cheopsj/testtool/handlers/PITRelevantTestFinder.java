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

package be.ac.ua.ansymo.cheopsj.testtool.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.viewers.StructuredSelection;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;



/**
 * @author quinten
 * 
 */
public class PITRelevantTestFinder {

	// private static Collection<String> relevantTests;
	private static List<?> selection;
	private static Collection<String> tests;
	private static Map<String, Collection<String>> relevantTests;
	private static List<String> methodsAnalysed;

	//private static Map<AtomicChange, Collection<String>> relevantTests;

	/**
	 * @param selectedChanges
	 */
	public static void findTests(List<?> selectedChanges) {

		selection = selectedChanges;

		relevantTests = new HashMap<String, Collection<String>>();
		for (Object obj : selectedChanges) {
			tests = new ArrayList<String>();
			if (obj instanceof AtomicChange) {
				AtomicChange change = ((AtomicChange) obj);

				// look for which method this change is in
				AtomicChange methodAddition = findContainingMethodAddition(change);
				if(methodAddition != null){
					String sourceclass = findTestCaseofTest(methodAddition);


					// look for all tests that call this method
					methodsAnalysed = new ArrayList<String>();
					findRelevantTests(methodAddition);
					for(String testcase: tests){
						if (!relevantTests.containsKey(sourceclass)) {
							relevantTests.put(sourceclass, new ArrayList<String>());
							relevantTests.get(sourceclass).add(testcase);
						} else {
							if (!relevantTests.get(sourceclass).contains(testcase)) {
								relevantTests.get(sourceclass).add(testcase);
							}
						}
					}
				}

			}
		}
	}

	/**
	 * @param testClass
	 * @param testMethodName
	 * @return
	 * @throws JavaModelException
	 */
	private static IMethod findMethod(String testCase, String testMethodName) throws JavaModelException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		for (IProject project : projects) {
			IJavaProject javaProject = JavaCore.create(project);
			IPackageFragment[] packages = javaProject.getPackageFragments();
			for (IPackageFragment mypackage : packages) {
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
						IType[] allTypes = unit.getAllTypes();
						for (IType type : allTypes) {
							IMethod[] methods = type.getMethods();
							for (IMethod method : methods) {
								if (method.getElementName().contains(testMethodName)) {
									return method;
								}
							}
						}
					}
				}

			}

		}
		return null;
	}

	private static void findRelevantTests(AtomicChange addition) {
		if(addition != null){
			List<Add> invocations = findInvocations(addition);
			for (AtomicChange invocation : invocations) {
				AtomicChange methodAddition = findContainingMethodAddition(invocation);

				if (methodAddition.getChangeSubject() instanceof FamixMethod) {
					String methodName = ((FamixMethod) methodAddition.getChangeSubject()).getUniqueName();

					// TODO account for Junit 4 tests (@Test)
					if (methodName.contains("test")) {

						if(!methodsAnalysed.contains(methodName)){
							methodsAnalysed.add(methodName);

							String testcase = findTestCaseofTest(methodAddition);
							if(!tests.contains(testcase))
								tests.add(testcase);
						}
					} else {
						if(!methodsAnalysed.contains(methodName)){
							methodsAnalysed.add(methodName);
							findRelevantTests(methodAddition);
						}
					}
				}

			}
		}
	}

	/**
	 * @param methodName
	 * @return
	 */
	private static String findTestCaseofTest(AtomicChange methodAddition) {

		List<Change> deps = (List<Change>) methodAddition.getStructuralDependencies();
		for (Change ch : deps) {
			if (ch instanceof AtomicChange) {
				AtomicChange ach = (AtomicChange) ch;
				if (ach.getChangeSubject() instanceof FamixClass) {
					return ((FamixClass) ach.getChangeSubject()).getUniqueName();
				}
			}
		}
		return null;
	}

	/**
	 * @param addition
	 *            (can only be when addition is a MethodADDITION)
	 * @return
	 */
	private static List<Add> findInvocations(AtomicChange addition) {
		FamixMethod calledMethod = (FamixMethod) addition.getChangeSubject();

		List<Change> deps = (List<Change>) addition.getStructuralDependees();
		List<Add> invocations = new ArrayList<Add>();
		for (Change ch : deps) {
			if (ch instanceof AtomicChange) {
				AtomicChange ach = (AtomicChange) ch;
				if (ach.getChangeSubject() instanceof FamixInvocation) {
					FamixInvocation inv = (FamixInvocation) ach.getChangeSubject();
					if (inv.getCandidate().getUniqueName().equals(calledMethod.getUniqueName()))
						invocations.add((Add) ach);
				}
			}
		}
		return invocations;
	}

	/**
	 * @param change
	 * @return
	 */
	private static AtomicChange findContainingMethodAddition(AtomicChange change) {
		// Need a better way to navigate through hierarchical dependencies.
		if (change.getChangeSubject() instanceof FamixMethod) {
			return change.getChangeSubject().getLatestAddition();
		}

		if (change.getChangeSubject() instanceof FamixInvocation) {
			FamixInvocation inv = (FamixInvocation) change.getChangeSubject();
			return inv.getInvokedBy().getLatestAddition();
		}

		List<Change> deps = (List<Change>) change.getStructuralDependencies();
		for (Change ch : deps) {
			if (ch instanceof AtomicChange) {
				AtomicChange ach = (AtomicChange) ch;
				if (ach.getChangeSubject() instanceof FamixMethod) {
					return (AtomicChange) ach;
				} else {
					return findContainingMethodAddition(ach);
				}
			}
		}
		return null;
	}


	//TODO Fix
	public static void printToAntPITBuildConfiguration() throws IOException{
		for (String sourceClass : relevantTests.keySet()) {
			if(!sourceClass.endsWith("Test")){
				FileWriter fstream = new FileWriter("/Users/quinten/Desktop/builds/build_"+sourceClass+".xml");
				BufferedWriter out = new BufferedWriter(fstream);

				printStuffInFile(out, "/Users/quinten/Desktop/CHEOPSWorkspace/be.ac.ua.ansymo.cheopsj.testtool/stuffBeforeAnt.txt");


				out.write("<pitest classPath=\"mutation.path\"" + '\n');

				out.write("targetClasses=\""+sourceClass+"\"" + '\n');

				out.write("targetTests=\"");

				String str = "";

				for (String testCase : relevantTests.get(sourceClass)) {
					str += testCase + ',';
				}
				str = str.substring(0, str.lastIndexOf(','));

				out.write(str);

				out.write("\"" + '\n');

				out.write("reportDir=\"${target}/pitReports/dynamic/"+ sourceClass +'\"'+'\n'); 
				out.write("sourceDir=\"src\""+ '\n'); 
				out.write("excludedClasses=\"net.sourceforge.cruisecontrol.util.StdoutBufferTest,net.sourceforge.cruisecontrol.bootstrappers.ExecBootstrapperTest,net.sourceforge.cruisecontrol.builders.ExecBuilderTest,net.sourceforge.cruisecontrol.MainTest\"/>"+ '\n');

				printStuffInFile(out, "/Users/quinten/Desktop/CHEOPSWorkspace/be.ac.ua.ansymo.cheopsj.testtool/stuffAfterAnt.txt");
				out.close();
			}
		}
	}

	public static void printToMavenPITBuildConfiguration() throws IOException{

		for (String sourceClass : relevantTests.keySet()) {

			FileWriter fstream = new FileWriter("/Users/quinten/Desktop/poms/"+sourceClass+"_pom.xml");
			BufferedWriter out = new BufferedWriter(fstream);

			printStuffInFile(out, "/Users/quinten/Desktop/CHEOPSWorkspace/be.ac.ua.ansymo.cheopsj.testtool/stuffBeforeMaven.txt");

			out.write("<plugin>" + '\n' +
					"<groupId>org.pitest</groupId>"+ '\n' +
					"<artifactId>pitest-maven</artifactId>"+'\n' +
					"<version>0.28</version>"+'\n' +
					"<configuration>"+'\n');

			out.write("<targetClasses>"+'\n' +
					"<param>" + sourceClass + "</param>"+'\n' +
					"</targetClasses>"+'\n');

			out.write("<targetTests>"+'\n');
			for (String testCase : relevantTests.get(sourceClass)) {
				out.write("<param>"+testCase+"</param>"+'\n');
			}
			out.write("</targetTests>"+'\n');

			out.write("<reportsDirectory>target/pit-reports/dynamic/"+sourceClass+"</reportsDirectory>"+'\n');

			out.write("<excludedClasses>"+'\n' +
					"<param>net.sourceforge.pmd.cpd.XMLRendererTest</param>"+'\n' +
					"</excludedClasses>"+'\n' +
					"</configuration>"+'\n' +
					"</plugin>"+'\n');

			printStuffInFile(out, "/Users/quinten/Desktop/CHEOPSWorkspace/be.ac.ua.ansymo.cheopsj.testtool/stuffAfterMaven.txt");
			out.close();
		}

	}

	private static void printStuffInFile(BufferedWriter out, String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String str;
		while ((str = in.readLine()) != null) {
			out.write(str + '\n');
		}
		in.close();
	}

	public static void printRelevantTests() throws IOException {
		FileWriter fstream = new FileWriter("/Users/quinten/Desktop/relevantTests/out.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		for (String sourceClass : relevantTests.keySet()) {
			if(!sourceClass.endsWith("Test")){				
				String str = sourceClass + ',';
				str += relevantTests.get(sourceClass).size();
				str += ',';

				for (String testCase : relevantTests.get(sourceClass)) {
					str += testCase + ',';
				}
				str = str.substring(0, str.lastIndexOf(','));

				out.write(str);

				out.write('\n');
			}
		}
		out.close();
	}

}
