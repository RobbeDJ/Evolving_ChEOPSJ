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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
public class RelevantTestFinder {

	// private static Collection<String> relevantTests;
	private static List<?> selection;
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
			if (obj instanceof AtomicChange) {
				AtomicChange change = ((AtomicChange) obj);

				// look for which method this change is in
				AtomicChange methodAddition = findContainingMethodAddition(change);

				// look for all tests that call this method
				methodsAnalysed = new ArrayList<String>();
				findTests(methodAddition); 

			}
		}
	}

	/**
	 * @throws ClassNotFoundException
	 * 
	 */
	public static void runTests() {
		for (String testCase : relevantTests.keySet()) {

			for (String testMethodName : relevantTests.get(testCase)) {

				JUnitLaunchShortcut jUnitLaunchShortcut = new JUnitLaunchShortcut();
				try {
					IMethod testMethod = findMethod(testCase, testMethodName);
					if(testMethod != null){
						StructuredSelection sel = new StructuredSelection(testMethod);
						jUnitLaunchShortcut.launch(sel, "run");
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
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
								if (method.getElementName().equals(testMethodName)) {
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

	private static void findTests(AtomicChange addition) {
		List<Add> invocations = findInvocations(addition);
		for (AtomicChange invocation : invocations) {
			AtomicChange methodAddition = findContainingMethodAddition(invocation);

			if (methodAddition.getChangeSubject() instanceof FamixMethod) {
				String methodName = ((FamixMethod) methodAddition.getChangeSubject()).getName();

				// TODO account for Junit 4 tests (@Test)
				if (methodName.contains("test")) {

					if(!methodsAnalysed.contains(methodName)){
						methodsAnalysed.add(methodName);

						String testcase = findTestCaseofTest(methodAddition);

						if (!relevantTests.containsKey(testcase)) {
							relevantTests.put(testcase, new ArrayList<String>());
							relevantTests.get(testcase).add(methodName);
						} else {
							if (!relevantTests.get(testcase).contains(methodName)) {
								relevantTests.get(testcase).add(methodName);
							}
						}
					}
				} else {
					if(!methodsAnalysed.contains(methodName)){
						methodsAnalysed.add(methodName);
						findTests(methodAddition);
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

	public static void printRelevantTests() {

		try {
			//FileWriter fstream = new FileWriter("/Users/quinten/Desktop/out.txt");
			//BufferedWriter out = new BufferedWriter(fstream);

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

			out.write("Selected Changes:" + '\n');
			for(Object obj : selection){
				if (obj instanceof AtomicChange) {
					AtomicChange change = ((AtomicChange) obj);
					out.write(change.getChangeType() + " of " + change.getFamixType() + " " + change.getName() + '\n');
				}
			}

			int counter = 1;	
			//out.write("There are " + relevantTests.size() + " relevant tests:" + '\n');
			for (String testCase : relevantTests.keySet()) {
				for (String test : relevantTests.get(testCase)) {
					out.write(counter + ": " + testCase + "." + test + '\n');
					counter++;
				}
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
