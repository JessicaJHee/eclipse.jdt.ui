/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.text.correction;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jdt.internal.corext.Assert;
import org.eclipse.jdt.internal.corext.fix.IFix;
import org.eclipse.jdt.internal.corext.fix.Java50Fix;

import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IProblemLocation;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.fix.ICleanUp;
import org.eclipse.jdt.internal.ui.fix.Java50CleanUp;

/**
 * Subprocessor for serial version quickfix proposals.
 *
 * @since 3.1
 */
public final class SerialVersionSubProcessor {

	public static final class SerialVersionHashProposal extends FixCorrectionProposal {
		public SerialVersionHashProposal(IFix fix, ICleanUp up, int relevance, Image image) {
			super(fix, up, relevance, image);
		}

		/**
		 * {@inheritDoc}
		 */
		public String getAdditionalProposalInfo() {
			return CorrectionMessages.SerialVersionHashProposal_message_generated_info;
		}
	}

	/**
	 * Determines the serial version quickfix proposals.
	 *
	 * @param context
	 *        the invocation context
	 * @param location
	 *        the problem location
	 * @param proposals
	 *        the proposal collection to extend
	 * @throws CoreException 
	 */
	public static final void getSerialVersionProposals(final IInvocationContext context, final IProblemLocation location, final Collection proposals) throws CoreException {

		Assert.isNotNull(context);
		Assert.isNotNull(location);
		Assert.isNotNull(proposals);
		
		IFix[] fixes= Java50Fix.createMissingSerialVersionFixes(context.getASTRoot(), location);
		if (fixes != null) {
			Image image= JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
			FixCorrectionProposal prop1= new SerialVersionDefaultProposal(fixes[0], null, 9, image);
			proposals.add(prop1);
			FixCorrectionProposal prop2= new SerialVersionHashProposal(fixes[1], new Java50CleanUp(Java50CleanUp.ADD_CALCULATED_SERIAL_VERSION_ID), 9, image);
			proposals.add(prop2);
		}
	}
}
