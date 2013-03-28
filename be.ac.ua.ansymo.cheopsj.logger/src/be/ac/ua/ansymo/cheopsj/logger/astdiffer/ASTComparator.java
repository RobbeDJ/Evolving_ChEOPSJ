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

import java.util.Collection;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author quinten
 * 
 */
public class ASTComparator {

	private CompilationUnit sourceAST;
	private CompilationUnit targetAST;
	private Collection<ASTNode> addedElements;
	private Collection<ASTNode> removedElements;

	/**
	 * @param oldAST
	 */
	public void setSource(CompilationUnit oldAST) {
		sourceAST = oldAST;
	}

	/**
	 * @param newAST
	 */
	public void setTarget(CompilationUnit newAST) {
		targetAST = newAST;
	}

	/**
	 * 
	 */
	public void diff() {
		DiffVisitor visitor = new DiffVisitor();
		sourceAST.accept(visitor);
		visitor.beginSecondAST();
		targetAST.accept(visitor);
		visitor.CalculateAddedAndRemovedNodes();

		addedElements = visitor.getAddedNodes();
		removedElements = visitor.getRemovedNodes();
	}

	/**
	 * @return
	 */
	public Collection<ASTNode> getAddedElements() {
		return addedElements;
	}

	/**
	 * @return
	 */
	public Collection<ASTNode> getRemovedElements() {
		return removedElements;
	}

}
