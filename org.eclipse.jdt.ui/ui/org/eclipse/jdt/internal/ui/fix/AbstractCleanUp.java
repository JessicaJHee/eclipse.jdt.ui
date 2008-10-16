/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.fix;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.jdt.ui.cleanup.CleanUpContext;
import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.CleanUpRequirements;
import org.eclipse.jdt.ui.cleanup.ICleanUp;
import org.eclipse.jdt.ui.cleanup.ICleanUpFix;

public abstract class AbstractCleanUp implements ICleanUp {

	private CleanUpOptions fOptions;

	protected AbstractCleanUp() {
	}

	protected AbstractCleanUp(Map settings) {
		if (settings != null)
			setOptions(new MapCleanUpOptions(settings));
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOptions(CleanUpOptions options) {
		fOptions= options;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getStepDescriptions() {
		return new String[0];
	}

	/**
	 * @return code snipped complying to current options
	 */
	public String getPreview() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public CleanUpRequirements getRequirements() {
		return new CleanUpRequirements(false, false, false, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public RefactoringStatus checkPreConditions(IJavaProject project, ICompilationUnit[] compilationUnits, IProgressMonitor monitor) throws CoreException {
		return new RefactoringStatus();
	}

	/**
	 * {@inheritDoc}
	 */
	public ICleanUpFix createFix(CleanUpContext context) throws CoreException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public RefactoringStatus checkPostConditions(IProgressMonitor monitor) throws CoreException {
		return new RefactoringStatus();
	}

	/**
	 * @param key the name of the option
	 * @return true if option with <code>key</code> is enabled
	 */
	protected boolean isEnabled(String key) {
		Assert.isNotNull(fOptions);
		Assert.isNotNull(key);

		return fOptions.isEnabled(key);
	}

}
