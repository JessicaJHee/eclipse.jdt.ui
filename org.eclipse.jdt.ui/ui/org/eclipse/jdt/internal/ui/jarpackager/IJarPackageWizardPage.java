/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.internal.ui.jarpackager;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * Common interface for all JAR package wizard pages.
 */
public interface IJarPackageWizardPage extends IWizardPage {
	/**
	 * Persists resource specification control setting that are to be restored
	 * in the next instance of this page. Subclasses wishing to persist
	 * settings for their controls should extend the hook method 
	 * <code>internalSaveWidgetValues</code>.
	 */
	void saveWidgetValues();
}
