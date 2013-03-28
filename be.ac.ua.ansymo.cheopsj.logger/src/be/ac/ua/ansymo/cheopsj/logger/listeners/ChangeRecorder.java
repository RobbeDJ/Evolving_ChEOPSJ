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
package be.ac.ua.ansymo.cheopsj.logger.listeners;

import java.util.Collection;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import be.ac.ua.ansymo.cheopsj.changerecorders.ClassRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.FieldRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.InheritanceRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.LocalVariableRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodInvocationRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.PackageRecorder;
import be.ac.ua.ansymo.cheopsj.logger.astdiffer.ASTComparator;
import be.ac.ua.ansymo.cheopsj.logger.util.CompilationUnitHistory;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;

public class ChangeRecorder {

	/**
	 * Unique instance of this class
	 */
	private static ChangeRecorder instance = null;

	/**
	 * Private constructor to avoid multiple instantiations
	 */
	private ChangeRecorder() {
	}

	/**
	 * 
	 * @return the unique instance of this class
	 */
	public static ChangeRecorder getInstance() {
		if (instance == null) {
			instance = new ChangeRecorder();
		}
		return instance;
	}

	/**
	 * This method is used to record fine grained changes inside the java
	 * editor. Like the additon/removal of methods and fields.
	 * 
	 * @param element
	 * @param delta
	 */
	public void recordElementChanges(IJavaElementDelta delta) {
		// System.out.println(delta.toString());
		IJavaElement element = delta.getElement();
		switch (delta.getKind()) {
		case IJavaElementDelta.ADDED:
			storeChange(element, new Add());

			if(element instanceof IMethod){
				try {
					ILocalVariable[] parameters = ((IMethod) element).getParameters();
					for(ILocalVariable param: parameters){
						storeChange(param, new Add());
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}else if (element instanceof IType){
				recordInheritanceRelationships((IType)element);
			}
			break;
		case IJavaElementDelta.REMOVED:
			if(element instanceof IMethod){
				/*try {
				 //Won't work, cant getParameters() on something that doesn't exist anymore!
					ILocalVariable[] parameters = ((IMethod) element).getParameters();
					//IJavaElement[] methodChildren = ((IMethod) element).getChildren();
					for(ILocalVariable param: parameters){
						storeChange(param, new Remove());
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}*/
			}
			storeChange(element, new Remove());
			break;
		case IJavaElementDelta.CHANGED:

			if((delta.getFlags() & IJavaElementDelta.F_SUPER_TYPES) != 0){
				if(element instanceof IType)
					recordInheritanceRelationships((IType)element);
			}

			// need to diff
			if ((delta.getFlags() & IJavaElementDelta.F_AST_AFFECTED) != 0) {
				// AST is affected
				if ((delta.getFlags() & IJavaElementDelta.F_CHILDREN) == 0) {
					// no more children
					if (element instanceof ICompilationUnit) {
						// AST of the compilationunit was affected AND there are
						// no child delta's indicating an addition or removal of
						// a method or field
						// (i.e. there could be a changes in the method body)
						CompilationUnit oldAST = getOldAST(element);
						CompilationUnit newAST = delta.getCompilationUnitAST();
						
						CompilationUnitHistory.storeNewAST(newAST, element.getResource().getProject().getLocation(), element.getResource().getProjectRelativePath() );
						//CompilationUnitHistory.storeNewAST(newAST, element.getResource().getLocation());
						if(oldAST != null && newAST != null)
							findAndLogChanges(oldAST, newAST);
					}
				}
			}
			break;
		}
		IJavaElementDelta[] children = delta.getAffectedChildren();
		for (IJavaElementDelta child : children) {
			recordElementChanges(child);
		}
	}

	private void recordInheritanceRelationships(IType element) {
		//IF the element in our model already contained a superclass that inheritance relationship needs to be removed.
		//The new inheritance relationship needs to be added.
		//Also the way to deal with interfaces!
		InheritanceRecorder recorder = new InheritanceRecorder(element);
		recorder.storeChanges();
	}

	private void findAndLogChanges(CompilationUnit oldAST, CompilationUnit newAST) {
		// Compare oldAST with newAST to figure out what changed!
		ASTComparator differ = new ASTComparator();
		differ.setSource(oldAST);
		differ.setTarget(newAST);
		differ.diff();

		Collection<ASTNode> added = differ.getAddedElements();
		Collection<ASTNode> removed = differ.getRemovedElements();

		for (ASTNode node : added) {
			storeChange(node, new Add());
		}

		for (ASTNode node : removed) {
			storeChange(node, new Remove());
		}
	}

	private CompilationUnit getOldAST(IJavaElement element) {
		CompilationUnit oldAST = null;

		oldAST = CompilationUnitHistory.getOldAST(element.getResource().getProject().getLocation(), element.getResource().getProjectRelativePath());

		return oldAST;
	}

	/**
	 * @param element
	 * @param add
	 */
	private void storeChange(IJavaElement element, IChange change) {
		if (element instanceof IMethod) {
			new MethodRecorder((IMethod) element).storeChange(change);
		} else if (element instanceof IField) {
			new FieldRecorder((IField) element).storeChange(change);
			//} else if (element instanceof ILocalVariable){
			//This only works for formal parameters!
			//	new LocalVariableRecorder((ILocalVariable)element).storeChange(change);
		} else if (element instanceof IType) {
			new ClassRecorder((IType) element).storeChange(change);
		} else if (element instanceof ICompilationUnit) {
			if (change instanceof Remove) {
				// super.storeChange(change);
				String filename = element.getElementName();
				String classname = filename.substring(0, filename.indexOf('.'));
				String uniquename = classname;
				String packagename = "";
				if (element.getParent() instanceof IPackageFragment) {
					packagename = ((IPackageFragment) element.getParent()).getElementName();
					uniquename = packagename + '.' + classname;
				}
				new ClassRecorder(uniquename, classname, packagename).storeChange(change);
			}
			//new CompilationUnitRecorder((ICompilationUnit) element).storeChange(change);
		} else if (element instanceof IPackageFragment) {
			new PackageRecorder((IPackageFragment) element).storeChange(change);
		}
	}

	/**
	 * @param node
	 * @param add
	 */
	private void storeChange(ASTNode node, IChange change) {
		if (node instanceof MethodInvocation) {
			new MethodInvocationRecorder((MethodInvocation) node).storeChange(change);
		}else if (node instanceof VariableDeclaration){
			//This is to get changes to other local vars.
			//new LocalVariableRecorder((VariableDeclaration) node).storeChange(change);
		}
	}
}
