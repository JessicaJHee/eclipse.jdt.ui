/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation 
 *          (report 36180: Callers/Callees view)
 ******************************************************************************/
package org.eclipse.jdt.internal.ui.callhierarchy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.dialogs.Dialog;

import org.eclipse.ui.help.WorkbenchHelp;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;

import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;

public class FiltersDialog extends Dialog {
    private Button fFilterOnNames;
    private Text fNames;
    private Text fMaxCallDepth;

    private SelectionListener selectionListener = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
            FiltersDialog.this.widgetSelected(e);
        }
    };

    /**
     * @param parentShell
     */
    protected FiltersDialog(Shell parentShell) {
        super(parentShell);
    }

    /* (non-Javadoc)
     * Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent) {
        Composite superComposite = (Composite) super.createDialogArea(parent);

        Font font = parent.getFont();
        Composite composite = new Composite(superComposite, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        composite.setFont(font);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        createNamesArea(composite);
        createMaxCallDepthArea(composite);
    
        updateUIFromFilter();
        
        return composite;
    }

    /* (non-Javadoc)
     * Method declared on Window.
     */
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(CallHierarchyMessages.getString("FiltersDialog.filter")); //$NON-NLS-1$
        WorkbenchHelp.setHelp(newShell, IJavaHelpContextIds.CALL_HIERARCHY_FILTERS_DIALOG);
    }
    
    void createMaxCallDepthArea(Composite parent) {
        new Label(parent, SWT.NONE).setText(CallHierarchyMessages.getString("FiltersDialog.maxCallDepth")); //$NON-NLS-1$
        
        fMaxCallDepth = new Text(parent, SWT.SINGLE | SWT.BORDER);
        fMaxCallDepth.setTextLimit(6);

        GridData gridData = new GridData();
        gridData.widthHint = convertWidthInCharsToPixels(10);
        fMaxCallDepth.setLayoutData(gridData);
        fMaxCallDepth.setFont(parent.getFont());
    }

    void createNamesArea(Composite parent) {
        fFilterOnNames = createCheckbox(parent,
                CallHierarchyMessages.getString("FiltersDialog.filterOnNames"), false); //$NON-NLS-1$
        fFilterOnNames.setLayoutData(new GridData());
        fNames= new Text(parent, SWT.SINGLE | SWT.BORDER);

        GridData gridData = new GridData();
        gridData.widthHint = convertWidthInCharsToPixels(30);
        fNames.setLayoutData(gridData);
        fNames.setFont(parent.getFont());
    }

    /**
     * Creates a check box button with the given parent and text.
     *
     * @param parent the parent composite
     * @param text the text for the check box
     * @param grabRow <code>true</code>to grab the remaining horizontal space,
     *        <code>false</code> otherwise
     *
     * @return the check box button
     */
    Button createCheckbox(Composite parent, String text, boolean grabRow) {
        Button button = new Button(parent, SWT.CHECK);

        if (grabRow) {
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            button.setLayoutData(gridData);
        }

        button.setText(text);
        button.addSelectionListener(selectionListener);
        button.setFont(parent.getFont());

        return button;
    }

    /**
     * Updates the enabled state of the widgetry.
     */
    void updateEnabledState() {
        fNames.setEnabled(fFilterOnNames.getSelection());
    }
    
    /**
     * Updates the given filter from the UI state.
     *
     * @param filter the filter to update
     */
    void updateFilterFromUI() {
        int maxCallDepth = Integer.parseInt(this.fMaxCallDepth.getText());

        CallHierarchyUI.getDefault().setMaxCallDepth(maxCallDepth);
        CallHierarchy.getDefault().setFilters(fNames.getText());
        CallHierarchy.getDefault().setFilterEnabled(fFilterOnNames.getSelection());
    }
    
    /**
     * Updates the UI state from the given filter.
     *
     * @param filter the filter to use
     */
    void updateUIFromFilter() {
      fMaxCallDepth.setText(""+CallHierarchyUI.getDefault().getMaxCallDepth()); //$NON-NLS-1$
      fNames.setText(CallHierarchy.getDefault().getFilters());
      fFilterOnNames.setSelection(CallHierarchy.getDefault().isFilterEnabled());
      updateEnabledState();
    }
    
    /**
     * Handles selection on a check box or combo box.
     */
    void widgetSelected(SelectionEvent e) {
        updateEnabledState();
    }
    
    /**
     * Updates the filter from the UI state.
     * Must be done here rather than by extending open()
     * because after super.open() is called, the widgetry is disposed.
     */
    protected void okPressed() {
        try {
            int maxCallDepth = Integer.parseInt(this.fMaxCallDepth.getText());
    
            if (maxCallDepth < 1 || maxCallDepth > 99) {
                throw new NumberFormatException();
            }           
            
            updateFilterFromUI();
            super.okPressed();
        }
        catch (NumberFormatException eNumberFormat) {
            MessageBox messageBox = new MessageBox(getShell(), 
                    SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_ERROR);
            messageBox.setText(CallHierarchyMessages.getString(
                    "FiltersDialog.titleMaxCallDepthInvalid")); //$NON-NLS-1$
            messageBox.setMessage(CallHierarchyMessages.getString(
                    "FiltersDialog.messageMaxCallDepthInvalid")); //$NON-NLS-1$
            messageBox.open();
    
            if (fMaxCallDepth.forceFocus()) {
                fMaxCallDepth.setSelection(0, fMaxCallDepth.getCharCount());
                fMaxCallDepth.showSelection();
            }
        }
    }
}
