/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Renaud Waldura &lt;renaud+eclipse@waldura.com&gt; - New class/interface with wizard
 *     Rabea Gransberger <rgransberger@gmx.de> - [quick fix] Fix several visibility issues - https://bugs.eclipse.org/394692
 *     Jens Reimann <jreimann@redhat.com> Bug 38201: [quick assist] Allow creating abstract method - https://bugs.eclipse.org/38201
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.fix;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;

import org.eclipse.jdt.internal.ui.text.correction.CorrectionMessages;
import org.eclipse.jdt.internal.ui.text.correction.IProblemLocationCore;

public class UnInitializedFinalFieldSubFixCore extends CompilationUnitRewriteOperationsFixCore {

	public UnInitializedFinalFieldSubFixCore(String name, CompilationUnit compilationUnit, CompilationUnitRewriteOperation operation) {
		super(name, compilationUnit, operation);
		// TODO Auto-generated constructor stub
	}

	public static UnInitializedFinalFieldSubFixCore addUnInitializedFinalFieldSubProposal(CompilationUnit cu, IProblemLocationCore problemLocation) {
		ASTNode selectedNode= problemLocation.getCoveringNode(cu);
		return new UnInitializedFinalFieldSubFixCore(CorrectionMessages.MissingAnnotationAttributesProposal_add_missing_attributes_label, cu, getProposals(selectedNode.get, problemLocation));
	}

	public static Collection<Object> getOperations(CompilationUnit astRoot, ICompilationUnit cu, IProblemLocationCore problem) throws CoreException {
		Collection<Object> operations = new ArrayList<>();
		ASTNode selectedNode= problem.getCoveringNode(astRoot);
		if (selectedNode == null) {
			return null;
		}
		int type= selectedNode.getNodeType();
		if (type == ASTNode.METHOD_DECLARATION) {
			// propose add initialization to constructor
			IMethodBinding targetBinding= null;
			MethodDeclaration node= (MethodDeclaration) selectedNode;
			if (!node.isConstructor()) {
				return null;
			}
			IMethodBinding binding= node.resolveBinding();
			if (binding != null) {
				targetBinding= binding;
			} else {
				return null;
			}
			ITypeBinding targetDecl= targetBinding.getDeclaringClass();
			ICompilationUnit targetCU= ASTResolving.findCompilationUnitForBinding(cu, astRoot, targetDecl);

			operations.add(new InitializeFinalFieldProposalOperation(null, astRoot, null));

			InitializeFinalFieldProposalOperation initializeFinalFieldProposal= new InitializeFinalFieldProposalOperation(problem, node, null, InitializeFinalFieldProposalOperation.UPDATE_CONSTRUCTOR_NEW_PARAMETER);
			operations.add(initializeFinalFieldProposal);

		} else if (type == ASTNode.SIMPLE_NAME) {
			// propose add initialization at declaration
			IVariableBinding targetBinding= null;
			SimpleName node= (SimpleName) selectedNode;
			IBinding binding= node.resolveBinding();
			if (binding instanceof IVariableBinding) {
				targetBinding= (IVariableBinding) binding;
			} else {
				return null;
			}
			ITypeBinding targetDecl= targetBinding.getDeclaringClass();
			ICompilationUnit targetCU= ASTResolving.findCompilationUnitForBinding(cu, astRoot, targetDecl);

			operations.add(new InitializeFinalFieldProposalOperation(problem, node, targetBinding));
		}

		return operations;
	}
}
