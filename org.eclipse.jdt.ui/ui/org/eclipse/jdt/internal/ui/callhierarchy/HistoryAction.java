/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation 
 * 			(report 36180: Callers/Callees view)
 ******************************************************************************/
package org.eclipse.jdt.internal.ui.callhierarchy;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;

import org.eclipse.ui.help.WorkbenchHelp;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;

import org.eclipse.jdt.ui.JavaElementLabelProvider;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;

/**
 * Action used for the type hierarchy forward / backward buttons
 */
class HistoryAction extends Action {
    private static JavaElementLabelProvider fLabelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_POST_QUALIFIED |
            JavaElementLabelProvider.SHOW_PARAMETERS |
            JavaElementLabelProvider.SHOW_RETURN_TYPE);
    private CallHierarchyViewPart fView;
    private IMethod fMethod;

    public HistoryAction(CallHierarchyViewPart viewPart, IMethod element) {
        super();
        fView = viewPart;
        fMethod = element;

        String elementName = getElementLabel(element);
        setText(elementName);
        setImageDescriptor(getImageDescriptor(element));

        setDescription(CallHierarchyMessages.getFormattedString(CallHierarchyMessages.getString("HistoryAction.description"), elementName)); //$NON-NLS-1$
        setToolTipText(CallHierarchyMessages.getFormattedString(CallHierarchyMessages.getString("HistoryAction.tooltip"), elementName)); //$NON-NLS-1$
        
        WorkbenchHelp.setHelp(this, IJavaHelpContextIds.CALL_HIERARCHY_HISTORY_ACTION);
    }

    private ImageDescriptor getImageDescriptor(IJavaElement elem) {
        JavaElementImageProvider imageProvider = new JavaElementImageProvider();
        ImageDescriptor desc = imageProvider.getBaseImageDescriptor(elem, 0);
        imageProvider.dispose();

        return desc;
    }

    /*
     * @see Action#run()
     */
    public void run() {
        fView.gotoHistoryEntry(fMethod);
    }

    /**
     * @param element
     * @return String
     */
    private String getElementLabel(IJavaElement element) {
        Assert.isNotNull(element);
        return fLabelProvider.getText(element);
    }
}
