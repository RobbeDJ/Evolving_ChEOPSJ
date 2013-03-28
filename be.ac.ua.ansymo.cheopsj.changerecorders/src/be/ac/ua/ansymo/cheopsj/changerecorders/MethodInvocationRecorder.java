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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixObject;

/**
 * @author quinten
 * 
 */
public class MethodInvocationRecorder extends StatementRecorder {
	private ModelManager manager;
	private FamixInvocation famixInvocation;
	private FamixMethod invokedby;
	private FamixMethod calledmethod;

	private String calledMethodName;

	private String stringrepresentation;



	private MethodInvocationRecorder(){
		manager = ModelManager.getInstance();
	}

	/**
	 * @param node
	 */
	public MethodInvocationRecorder(MethodInvocation node) {
		this();

		containingMethodName = getContainingMethod(node);
		if(manager.famixMethodExists(containingMethodName)){
			invokedby = manager.getFamixMethod(containingMethodName);
		}

		if(invokedby == null)
			return;

		//This only works when logging changes!!! 
		IMethodBinding binding = node.resolveMethodBinding();
		if(binding != null){
			calledMethodName = binding.getName();
			if(binding.getDeclaringClass() != null){
				calledMethodName = binding.getDeclaringClass().getName() + '.'+ calledMethodName;
				if(binding.getDeclaringClass().getPackage() != null)
					calledMethodName = binding.getDeclaringClass().getPackage().getName() + '.'+ calledMethodName;
			}
		}else{
			//TODO needs to be fixed
			calledMethodName = node.getName().getFullyQualifiedName();
			if (node.getExpression() != null) {
				String localVarOrField = node.getExpression().toString();

				calledmethod = findMethodInType(localVarOrField, calledMethodName);
				if(calledmethod == null){
					calledmethod = new FamixMethod();
					calledmethod.setUniqueName(calledMethodName);
					calledmethod.setIsDummy(true);
					manager.addFamixElement(calledmethod);
				}
			}else{
				calledMethodName = invokedby.getBelongsToClass().getUniqueName() + '.' + calledMethodName;
				if(manager.famixMethodExists(calledMethodName)){
					calledmethod = manager.getFamixMethod(calledMethodName);
				}else{
					calledmethod = new FamixMethod();
					calledmethod.setUniqueName(calledMethodName);
					calledmethod.setIsDummy(true);
					manager.addFamixElement(calledmethod);
				}
			}

		}
	}

	public MethodInvocationRecorder(SourceCodeEntity entity){
		this();
		entity.getUniqueName();
		//TODO
	}

	/**
	 * @param localVarOrField
	 * @return
	 */
	private FamixMethod findMethodInType(String localVarOrField, String invokedMethodName) {

		FamixObject famixVar = null;

		if(invokedby.containsVariable(invokedby.getUniqueName() + '{' + localVarOrField + '}')){
			//is it a variable or parameter?
			localVarOrField = invokedby.getUniqueName() +'{' + localVarOrField + '}';
			famixVar = invokedby.findLocalVariable(localVarOrField);
		}else{
			//is it mayhaps a field?
			localVarOrField = invokedby.getBelongsToClass().getUniqueName() + '.' + localVarOrField;
			famixVar = manager.getFamixField(localVarOrField);
		}

		//TODO expression can also be a type.

		// find out what type it is
		FamixClass type = new FamixClass();
		if (famixVar != null) {
			if (famixVar instanceof FamixLocalVariable) {
				type = ((FamixLocalVariable) famixVar).getDeclaredClass();
			} else if (famixVar instanceof FamixAttribute) {
				type = ((FamixAttribute) famixVar).getDeclaredClass();
			}
			// TODO handle primitive types!!!
		}
		String methodName;
		if(type != null){
			methodName = type.getUniqueName() + '.' + invokedMethodName;
			return type.findMethod(methodName);
		}else{
			methodName = invokedMethodName;
			return manager.getFamixMethod(methodName);
		}


	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		if(invokedby == null)
			return;

		if (manager.famixMethodExists(containingMethodName)) {
			invokedby = manager.getFamixMethod(containingMethodName);
		} //invokedby should exist

		if(manager.famixMethodExists(calledMethodName)){
			calledmethod = manager.getFamixMethod(calledMethodName);
		} else {
			calledmethod = new FamixMethod();
			calledmethod.setUniqueName(calledMethodName);
			calledmethod.setIsDummy(true);
			manager.addFamixElement(calledmethod);
		}

		stringrepresentation = invokedby.getUniqueName() + '{' + calledMethodName + '}';

		//TODO make sure a method can be called several times from another method!!!! (use invocation counter?) > don't care for now!
		if (!manager.famixInvocationExists(stringrepresentation)) {
			famixInvocation = new FamixInvocation();
			famixInvocation.setStringRepresentation(stringrepresentation);
			//calledmethod = manager.getFamixMethod(calledMethodName);
			//invokedby = manager.getFamixMethod(containingMethodName);

			famixInvocation.setCandidate(calledmethod);
			calledmethod.addInvokedBy(famixInvocation);

			famixInvocation.setInvokedBy(invokedby);
			invokedby.addInvocation(famixInvocation);

			manager.addFamixElement(famixInvocation);
		} else {
			famixInvocation = manager.getFamixInvocation(stringrepresentation);
			invokedby = (FamixMethod) famixInvocation.getInvokedBy();

			//calledMethodCandidates = convert(famixInvocation.getCandidates());
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
		if(invokedby == null)
			return;

		change.setChangeSubject(famixInvocation);
		famixInvocation.addChange(change);

		setStructuralDependencies(change, famixInvocation);
		manager.addChange(change);
	}

	/**
	 * @param change
	 * @param famixInvocation2
	 */
	private void setStructuralDependencies(AtomicChange change, FamixInvocation subject) {
		if (change instanceof Add) {
			if(calledmethod != null){
				Change calledMethodChange = calledmethod.getLatestAddition();
				if (calledMethodChange != null) {
					change.addStructuralDependency(calledMethodChange);
				}
			}
			if (invokedby != null) {
				Change invokedByChange = invokedby.getLatestAddition();
				if (invokedByChange != null) {
					change.addStructuralDependency(invokedByChange);
				}
			}
			Remove removalChange = subject.getLatestRemoval();
			if (removalChange != null) {
				change.addStructuralDependency(removalChange);
			}
		} else if (change instanceof Remove) {
			// set dependency to addition of this entity
			// Subject removedSubject = change.getChangeSubject();
			AtomicChange additionChange = subject.getLatestAddition();
			if (additionChange != null) {
				change.addStructuralDependency(additionChange);
			}
		}

	}
}
