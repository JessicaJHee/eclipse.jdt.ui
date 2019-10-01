/*******************************************************************************
 * Copyright (c) 2019 Red Hat Inc. and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/
package org.eclipse.jdt.ui.tests.core;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.testplugin.JavaProjectHelper;

import org.eclipse.swt.SWT;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.eclipse.ui.IEditorPart;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;

import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.text.java.PostfixCompletionProposalComputer;
import org.eclipse.jdt.internal.ui.text.template.contentassist.PostfixTemplateProposal;

import junit.framework.TestCase;

public class PostFixCompletionTest extends TestCase {

	private IJavaProject fJProject;

	private IPackageFragmentRoot javaSrc;

	private IPackageFragment pkg;

	@Override
	protected void setUp() throws Exception {
		fJProject= JavaProjectHelper.createJavaProject("TestProject", "bin");
		JavaProjectHelper.addRTJar18(fJProject);
		javaSrc= JavaProjectHelper.addSourceContainer(fJProject, "src");
		pkg= javaSrc.createPackageFragment("test", false, null);
	}

	@Override
	protected void tearDown() throws Exception {
		JavaProjectHelper.delete(fJProject);
	}

	public void testStringVar() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class StringVar {\n" +
				"  public void test () {\n" +
				"    \"Some String Value\".var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "StringVar.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class StringVar {\n" +
				"  public void test () {\n" +
				"    String name = \"Some String Value\";\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testStringVar2() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"\n" +
				"public class StringVar2 {\n" +
				"  public void test () {\n" +
				"    \"foo\".$\n" +
				"    if (true);\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "StringVar2.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"\n" +
				"public class StringVar2 {\n" +
				"  public void test () {\n" +
				"    String name = \"foo\";\n" +
				"    if (true);\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testIntegerVar() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class IntegerVar {\n" +
				"  public void test () {\n" +
				"    new Integer(0).var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "IntegerVar.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class IntegerVar {\n" +
				"  public void test () {\n" +
				"    Integer name = new Integer(0);\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testBooleanVar() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class BooleanVar {\n" +
				"  public void test () {\n" +
				"    false.var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "BooleanVar.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class BooleanVar {\n" +
				"  public void test () {\n" +
				"    boolean false1 = false;\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testIntVar() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class IntVar {\n" +
				"  public void test () {\n" +
				"    (2 + 2).var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "IntVar.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class IntVar {\n" +
				"  public void test () {\n" +
				"    int name = (2 + 2);\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testStringConcatVar() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class StringConcatVar {\n" +
				"  public void test () {\n" +
				"    (\"two\" + 2).var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "StringConcatVar.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class StringConcatVar {\n" +
				"  public void test () {\n" +
				"    String name = (\"two\" + 2);\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testStringConcatVar2() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class testStringConcatVar2 {\n" +
				"  public void test () {\n" +
				"    ((((\"two\" + 2)))).var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "testStringConcatVar2.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class testStringConcatVar2 {\n" +
				"  public void test () {\n" +
				"    String name = ((((\"two\" + 2))));\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testArrayVar() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class ArrayVar {\n" +
				"  public void test () {\n" +
				"    new byte[] { 0, 1, 3 }.var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "ArrayVar.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class ArrayVar {\n" +
				"  public void test () {\n" +
				"    byte[] name = new byte[] { 0, 1, 3 };\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testBoundedExtendsTypeParameterVar() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"import java.util.List;\n" +
				"public class BoundedExtendsTypeParameterVar {\n" +
				"  public void test () {\n" +
				"    List<? extends Number> x = null;\n" +
				"    x.get(0).var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "BoundedExtendsTypeParameterVar.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"import java.util.List;\n" +
				"public class BoundedExtendsTypeParameterVar {\n" +
				"  public void test () {\n" +
				"    List<? extends Number> x = null;\n" +
				"    Number name = x.get(0);\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testBoundedSuperTypeParameterVar() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"import java.util.List;\n" +
				"public class testBoundedSuperTypeParameterVar {\n" +
				"  public void test () {\n" +
				"    List<? super Number> x = null;\n" +
				"    x.get(0).var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "testBoundedSuperTypeParameterVar.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"import java.util.List;\n" +
				"public class testBoundedSuperTypeParameterVar {\n" +
				"  public void test () {\n" +
				"    List<? super Number> x = null;\n" +
				"    Object name = x.get(0);\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testVarForMethodInvocation() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"import java.util.Arrays;\n" +
				"import java.util.List;\n" +
				"public class VarForMethodInvocation {\n" +
				"  public void test () {\n" +
				"    List<String> res = Arrays.asList(\"a\", \"b\");\n" +
				"    res.get(0).isEmpty().var$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "VarForMethodInvocation.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"import java.util.Arrays;\n" +
				"import java.util.List;\n" +
				"public class VarForMethodInvocation {\n" +
				"  public void test () {\n" +
				"    List<String> res = Arrays.asList(\"a\", \"b\");\n" +
				"    boolean name = res.get(0).isEmpty();\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testNestedQualifiedNames() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class NestedQualifiedNames {\n" +
				"public Foo foo; \n" +
				"public void foo () {\n" +
				"  Foo foo = new Foo ();\n" +
				"  foo.bar.res.$\n" +
				"}\n" +
				"public class Foo {\n" +
				"  public Bar bar;\n" +
				"}\n" +
				"public class Bar {\n" +
				"  public String res;\n" +
				"}\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "NestedQualifiedNames.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class NestedQualifiedNames {\n" +
				"public Foo foo; \n" +
				"public void foo () {\n" +
				"  Foo foo = new Foo ();\n" +
				"  String name = foo.bar.res;\n" +
				"}\n" +
				"public class Foo {\n" +
				"  public Bar bar;\n" +
				"}\n" +
				"public class Bar {\n" +
				"  public String res;\n" +
				"}\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testFieldAccess() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class FieldAccess {\n" +
				"public class Foo {\n" +
				"  public String res;\n" +
				"  public void foo () {\n" +
				"    this.res.$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "FieldAccess.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("var - Creates a new variable"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "var", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class FieldAccess {\n" +
				"public class Foo {\n" +
				"  public String res;\n" +
				"  public void foo () {\n" +
				"    String name = this.res;\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testForStatement() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"import java.util.List;\n" +
				"public class ForStatement {\n" +
				"  public void test () {\n" +
				"    List<String> a;\n" +
				"    a.$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "ForStatement.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("for - Creates a for statement"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "for", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"import java.util.List;\n" +
				"public class ForStatement {\n" +
				"  public void test () {\n" +
				"    List<String> a;\n" +
				"    for (String a2 : a) {\n" +
				"		\n" +
				"	}\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testShorthandIfStatement() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class ShorthandIfStatement {\n" +
				"  public void test () {\n" +
				"    true.$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "ShorthandIfStatement.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("sif - Creates a short if statement"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "sif", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class ShorthandIfStatement {\n" +
				"  public void test () {\n" +
				"    ((true) ?  : )\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testConcatenatedShorthandIfStatement() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"public class ConcatenatedShorthandIfStatement {\n" +
				"  public void test () {\n" +
				"    System.out.println(\"two + \" + true.$\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "ConcatenatedShorthandIfStatement.java");
		List<ICompletionProposal> proposals= computeCompletionProposals(cu, completionIndex);

		assertProposalsExist(Arrays.asList("sif - Creates a short if statement"), proposals);

		ITextViewer viewer= initializeViewer(cu);
		applyProposal(viewer, proposals, "sif", completionIndex);

		StringBuffer expected= new StringBuffer();
		expected.append("package test;\n" +
				"public class ConcatenatedShorthandIfStatement {\n" +
				"  public void test () {\n" +
				"    System.out.println(\"two + \" + ((true) ?  : )\n" +
				"  }\n" +
				"}");

		assertEquals(expected.toString(), viewer.getDocument().get());
	}

	public void testNoThrownExceptions() throws Exception {
		StringBuffer buf= new StringBuffer();
		buf.append("package test;\n" +
				"x.$\n" +
				"public class NoThrownExceptions {\n" +
				"  public void test () {\n" +
				"  }\n" +
				"}");

		int completionIndex= getCompletionIndex(buf);
		ICompilationUnit cu= getCompilationUnit(pkg, buf, "NoThrownExceptions.java");
		computeCompletionProposals(cu, completionIndex);
	}

	private ITextViewer initializeViewer(ICompilationUnit cu) throws Exception {
		IEditorPart editor= EditorUtility.openInEditor(cu);
		ITextViewer viewer= new TextViewer(editor.getSite().getShell(), SWT.NONE);
		viewer.setDocument(new Document(cu.getSource()));
		return viewer;
	}

	private ICompilationUnit getCompilationUnit(IPackageFragment pack, StringBuffer buf, String name) throws JavaModelException {
		return pack.createCompilationUnit(name, buf.toString().replace("$", ""), false, null);
	}

	private int getCompletionIndex(StringBuffer buf) {
		return buf.toString().indexOf('$');
	}

	private List<ICompletionProposal> computeCompletionProposals(ICompilationUnit cu, int completionIndex) throws Exception {
		PostfixCompletionProposalComputer comp= new PostfixCompletionProposalComputer();

		IEditorPart editor= EditorUtility.openInEditor(cu);
		ITextViewer viewer= new TextViewer(editor.getSite().getShell(), SWT.NONE);
		viewer.setDocument(new Document(cu.getSource()));
		JavaContentAssistInvocationContext ctx= new JavaContentAssistInvocationContext(viewer, completionIndex, editor);

		return comp.computeCompletionProposals(ctx, null);
	}

	private void assertProposalsExist(List<String> expected, List<ICompletionProposal> proposals) {
		for (String propDisplay : expected) {
			assertTrue(proposals.stream().anyMatch(p -> propDisplay.equals(p.getDisplayString())));
		}
	}

	private void applyProposal(ITextViewer viewer, List<ICompletionProposal> proposals, String name, int offset) throws Exception {
		PostfixTemplateProposal proposal= (PostfixTemplateProposal) proposals.stream().filter(p -> ((PostfixTemplateProposal) p).getTemplate().getName().equals(name)).findFirst().get();
		proposal.apply(viewer, '0', -1, offset);
	}

}
