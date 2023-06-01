/*******************************************************************************
 * Copyright (c) 2019, 2020 IBM Corporation and others.
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
 *******************************************************************************/

package org.eclipse.jdt.internal.ui.text.correction.proposals;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import org.eclipse.jdt.internal.corext.fix.CompilationUnitRewriteOperationsFixCore.CompilationUnitRewriteOperation;
import org.eclipse.jdt.internal.corext.fix.InitializeFinalFieldProposalOperation;
import org.eclipse.jdt.internal.corext.refactoring.structure.CompilationUnitRewrite;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.text.java.IProblemLocation;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.text.correction.CorrectionMessages;
import org.eclipse.jdt.internal.ui.text.correction.IProblemLocationCore;

public class InitializeFinalFieldProposal extends LinkedCorrectionProposal {

	private CompilationUnitRewrite fCompilationUnitRewrite;

	private CompilationUnitRewriteOperation fASTRewriteProposalCore;

	public static final int UPDATE_AT_DECLARATION= 0;

	public InitializeFinalFieldProposal(IProblemLocation problem, ICompilationUnit cu, ASTNode astNode, IVariableBinding variableBinding, int relevance) {
		super(Messages.format(CorrectionMessages.InitializeFieldAtDeclarationCorrectionProposal_description, problem.getProblemArguments()[0]), cu, null, relevance, null);
		fASTRewriteProposalCore= new InitializeFinalFieldProposalOperation((IProblemLocationCore) problem, astNode, variableBinding, UPDATE_AT_DECLARATION);
		fCompilationUnitRewrite= new CompilationUnitRewrite(cu, (CompilationUnit)astNode.getRoot());
		setImage(JavaPluginImages.get(JavaPluginImages.IMG_FIELD_PRIVATE));
	}

	public InitializeFinalFieldProposal(IProblemLocation problem, ICompilationUnit cu, ASTNode astNode, int relevance, int updateType) {
		super(Messages.format(CorrectionMessages.InitializeFieldInConstructorCorrectionProposal_description, problem.getProblemArguments()[0]), cu, null, relevance, null);
		fASTRewriteProposalCore= new InitializeFinalFieldProposalOperation((IProblemLocationCore) problem, astNode, null, updateType);
		fCompilationUnitRewrite= new CompilationUnitRewrite(cu, (CompilationUnit)astNode.getRoot());
		setImage(JavaPluginImages.get(JavaPluginImages.IMG_FIELD_PRIVATE));
	}

	public boolean hasProposal() throws CoreException {
		return getRewrite() != null;
	}

	@Override
	protected ASTRewrite getRewrite() throws CoreException {
		fASTRewriteProposalCore.rewriteAST(fCompilationUnitRewrite, getLinkedProposalModel());
		return fCompilationUnitRewrite.getASTRewrite();
	}

}
