/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.internal.core.refactoring.code.flow;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.core.refactoring.util.Selection;

public class InOutFlowAnalyzer extends FlowAnalyzer {
	
	private Selection fSelection;
	
	public InOutFlowAnalyzer(FlowContext context, Selection selection) {
		super(context);
		fSelection= selection;
	}
	
	public FlowInfo analyse(AbstractMethodDeclaration method, ClassScope scope) {
		FlowContext context= getFlowContext();
		method.traverse(this, scope);
		return getFlowInfo(method);
	}
	
	protected boolean traverseRange(int start, int end) {
		return fSelection.coveredBy(start, end) || fSelection.covers(start, end);
	}
	
	protected boolean createReturnFlowInfo(ReturnStatement node) {
		// Make sure that the whole return statement is selected. There can be cases like
		// return i + [x + 10] * 10; In this case we must not create a return info node.		
		return fSelection.covers(node);
	}
}

