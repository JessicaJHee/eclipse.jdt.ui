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
package org.eclipse.jdt.internal.ui.text.correction;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import org.eclipse.jdt.internal.corext.fix.CompilationUnitRewriteOperationsFixCore.CompilationUnitRewriteOperation;
import org.eclipse.jdt.internal.corext.fix.UnInitializedFinalFieldSubFixCore;
import org.eclipse.jdt.internal.corext.refactoring.structure.CompilationUnitRewrite;

import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.correction.ICommandAccess;

public class UnInitializedFinalFieldSubProcessor {

	public static void getProposals(IInvocationContext context, IProblemLocation problem, Collection<ICommandAccess> proposals) throws CoreException {
		ICompilationUnit cu= context.getCompilationUnit();

		CompilationUnit astRoot= context.getASTRoot();
		ASTNode selectedNode= problem.getCoveringNode(astRoot);

		CompilationUnitRewriteOperation fASTRewriteProposalCore = (CompilationUnitRewriteOperation) UnInitializedFinalFieldSubFixCore.getOperations(astRoot, cu, (IProblemLocationCore) problem);
		CompilationUnitRewrite fCompilationUnitRewrite= new CompilationUnitRewrite(cu, astRoot);
		fASTRewriteProposalCore.rewriteAST(fCompilationUnitRewrite, getLinkedProposalModel());
		proposals.add(fCompilationUnitRewrite.getASTRewrite());
	}

	private UnInitializedFinalFieldSubProcessor() {
	}
}
