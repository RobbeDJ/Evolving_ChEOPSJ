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

package be.ac.ua.ansymo.cheopsj.changerecorders;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;



/**
 * @author quinten
 * 
 */
public class VariableDeclarationRecoreder extends AbstractEntityRecorder {

	private VariableDeclarationStatement node;
	private FamixLocalVariable famixVariable;
	private FamixMethod parent;
	private ModelManager manager;

	/**
	 * @param node
	 */
	public VariableDeclarationRecoreder(VariableDeclarationStatement node) {
		this.node = node;
		manager = ModelManager.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {

		String variableName = ((VariableDeclarationFragment) node.fragments().get(0)).getName().getIdentifier();
		Type type = node.getType();
		String typename = "";
		if (type.isSimpleType()) {
			typename = ((SimpleType) type).getName().getFullyQualifiedName();
		}

		String containingmethodName = getContainingMethod(node);

		// TODO should somehow be able to get variable type out of the node.

		if (!manager.famixVariableExists(variableName)) {
			famixVariable = new FamixLocalVariable();

			// TODO use fully qualified name
			famixVariable.setUniqueName(variableName);
			//famixVariable.setName(variableName);

			parent = manager.getFamixMethod(containingmethodName);
			FamixClass declaredType = manager.getFamixClass(typename);
			// TODO what with primitive Types?

			famixVariable.setBelongsToBehaviour(parent);
			parent.addLocalVariable(famixVariable);
			famixVariable.setDeclaredClass(declaredType);

			manager.addFamixElement(famixVariable);

			// TODO set variable type !!! link to FamixClass OR primitiveType
		} else {
			famixVariable = manager.getFamixVariable(variableName);
			parent = (FamixMethod) famixVariable.getBelongsToBehaviour();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange(be.ac.ua.cheopsj.Model.Changes.AtomicChange)
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		change.setChangeSubject(famixVariable);
		famixVariable.addChange(change);

		setStructuralDependencies(change, famixVariable);
		manager.addChange(change);
	}

	/**
	 * @param change
	 * @param famixInvocation2
	 */
	private void setStructuralDependencies(AtomicChange change, FamixLocalVariable subject) {
		if (change instanceof Add) {
			if (parent != null) {
				Change parentChange = parent.getLatestAddition();
				if (parentChange != null) {
					change.addStructuralDependency(parentChange);
				}
			}
			Remove removalChange = subject.getLatestRemoval();
			if (removalChange != null) {
				change.addStructuralDependency(removalChange);
			}
		} else if (change instanceof Remove) {
			// set dependency to addition of this entity
			// Subject removedSubject = change.getChangeSubject();
			Add additionChange = subject.getLatestAddition();
			if (additionChange != null) {
				change.addStructuralDependency(additionChange);
			}
		}

	}

	/**
	 * @param node2
	 * @return
	 */
	private String getContainingMethod(ASTNode node) {
		if (node instanceof MethodDeclaration) {
			return ((MethodDeclaration) node).getName().getIdentifier();
		} else {
			return getContainingMethod(node.getParent());
		}
	}
}
