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

package be.ac.ua.ansymo.cheopsj.logger.astdiffer;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * @author quinten
 * 
 */
public class DiffVisitor extends ASTVisitor {

	private Collection<MethodInvocation> oldMethodInvocations;
	private Collection<MethodInvocation> newMethodInvocations;
	private Collection<VariableDeclaration> oldVariableDeclarations;
	private Collection<VariableDeclaration> newVariableDeclarations;
	private Collection<ASTNode> addedNodes;
	private Collection<ASTNode> removedNodes;
	private boolean firstAST;

	public DiffVisitor() {
		super();
		oldMethodInvocations = new ArrayList<MethodInvocation>();
		newMethodInvocations = new ArrayList<MethodInvocation>();
		oldVariableDeclarations = new ArrayList<VariableDeclaration>();
		newVariableDeclarations = new ArrayList<VariableDeclaration>();

		addedNodes = new ArrayList<ASTNode>();
		removedNodes = new ArrayList<ASTNode>();
		firstAST = true;
	}

	public Collection<ASTNode> getAddedNodes() {
		return addedNodes;
	}

	public Collection<ASTNode> getRemovedNodes() {
		return removedNodes;
	}

	public void beginSecondAST() {
		firstAST = false;
	}

	// TODO also need to use ConstructorInvocation and SuperMethodInvocation
	// and/or SuperConstructorInvocation
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		if (firstAST) {
			oldMethodInvocations.add(node);
		} else {
			newMethodInvocations.add(node);
		}
		return true;
	}

	public boolean visit(FieldDeclaration node){
		return false;
	}

	public boolean visit(VariableDeclarationFragment node) {
		if (firstAST) {
			oldVariableDeclarations.add(node);
		} else {
			newVariableDeclarations.add(node);
		}
		return true;
	}

	public boolean visit(SingleVariableDeclaration node){
		if (firstAST) {
			oldVariableDeclarations.add(node);
		} else {
			newVariableDeclarations.add(node);
		}
		return true;
	}


	public void CalculateAddedAndRemovedNodes() {
		CalculateAddedAndRemovedMethodInvocations();
		CalculateAddedAndRemovedVariableDeclarations();
	}

	@SuppressWarnings("unchecked")
	private void CalculateAddedAndRemovedMethodInvocations() {
		//Compare oldSuperClasses with newSuperClasses to see which was added and which was removed.
		/*Collection<MethodInvocation> intersection = 
					CollectionUtils.intersection(oldMethodInvocations, newMethodInvocations);
		Collection<MethodInvocation> addedInvokes = 
				ListUtils.removeAll(newMethodInvocations, intersection);
		Collection<MethodInvocation> removedInvokes = 
				ListUtils.removeAll(oldMethodInvocations, intersection);

		addedNodes.addAll(addedInvokes);
		removedNodes.addAll(removedInvokes);*/

		//OLD CODE TO COMPARE COLLECTIONS
		boolean found = false;
		for (MethodInvocation oldMI : oldMethodInvocations) {
			for (MethodInvocation newMI : newMethodInvocations) {
				if (oldMI.getName().getFullyQualifiedName().equals
						(newMI.getName().getFullyQualifiedName())) {
					found = true;
					continue;
				}
			}
			if (!found) {
				removedNodes.add(oldMI);
			}
			found = false;
		}

		found = false;
		for (MethodInvocation newMI : newMethodInvocations) {
			for (MethodInvocation oldMI : oldMethodInvocations) {
				if (newMI.getName
						().getFullyQualifiedName().equals(oldMI.getName().getFullyQualifiedName
								())) {
					found = true;
					continue;
				}
			}
			if (!found) {
				addedNodes.add(newMI);
			}
			found = false;
		}
	}

	//TODO need to fix this (somehow) this only works if a variable name is used only once in the entire FILE!
	private void CalculateAddedAndRemovedVariableDeclarations() {
		/*
		Collection<VariableDeclaration> addedVars = CollectionUtils.removeAll(
				newVariableDeclarations, CollectionUtils.intersection(oldVariableDeclarations, newVariableDeclarations));
		Collection<VariableDeclaration> removedVars = CollectionUtils.removeAll(
				oldVariableDeclarations, CollectionUtils.intersection(oldVariableDeclarations, newVariableDeclarations));

		addedNodes.addAll(addedVars);
		removedNodes.addAll(removedVars);*/

		// OLD CODE TO COMPARE COLLECTIONS
		boolean found = false;
		for (VariableDeclaration oldVD : oldVariableDeclarations) {
			for (VariableDeclaration newVD : newVariableDeclarations) {
				if (oldVD.getName().getFullyQualifiedName().equals
						(newVD.getName().getFullyQualifiedName())) {
					found = true;
					continue;
				}
			}
			if (!found) {
				removedNodes.add(oldVD);
			}
			found = false;
		}

		found = false;
		for (VariableDeclaration newVD : newVariableDeclarations) {
			for (VariableDeclaration oldVD : oldVariableDeclarations) {
				if (newVD.getName
						().getFullyQualifiedName().equals(oldVD.getName().getFullyQualifiedName
								())) {
					found = true;
					continue;
				}
			}
			if (!found) {
				addedNodes.add(newVD);
			}
			found = false;
		}
	}
}
