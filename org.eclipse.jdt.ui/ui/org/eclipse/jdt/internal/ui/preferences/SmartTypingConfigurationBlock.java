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

package org.eclipse.jdt.internal.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.eclipse.ui.dialogs.PreferencesUtil;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.PreferenceConstants;

import org.eclipse.jdt.internal.ui.JavaPlugin;

/**
 * Configures Java Editor typing preferences.
 * 
 * @since 3.1
 */
class SmartTypingConfigurationBlock extends AbstractConfigurationBlock {

	public SmartTypingConfigurationBlock(OverlayPreferenceStore store) {
		super(store);
		
		store.addKeys(createOverlayStoreKeys());
	}
	
	private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {
		
		return new OverlayPreferenceStore.OverlayKey[] {
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_SMART_PASTE),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_IMPORTS_ON_PASTE),
				
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_STRINGS),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_BRACKETS),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_BRACES),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_JAVADOCS),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_WRAP_STRINGS),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_ESCAPE_STRINGS),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_ADD_JAVADOC_TAGS),
				
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_SMART_SEMICOLON),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_SMART_TAB),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_SMART_OPENING_BRACE),
		};
	}	

	/**
	 * Creates page for mark occurrences preferences.
	 * 
	 * @param parent the parent composite
	 * @return the control for the preference page
	 */
	public Control createControl(Composite parent) {
		boolean useCollapsibleSections= false;
		
		SectionManager manager;
		Composite control, body;
		if (useCollapsibleSections) {
			manager= new SectionManager(JavaPlugin.getDefault().getPreferenceStore(), "smart_typing_preference_dialog_last_open_section"); //$NON-NLS-1$
			control= manager.createSectionComposite(parent);
			body= control;
		} else {
			manager= null;
			control= new ScrolledPageContent(parent, SWT.NONE);
			body= ((ScrolledPageContent) control).getBody();
			GridLayout layout= new GridLayout();
			layout.numColumns= 1;
			body.setLayout(layout);
		}

		Composite composite;
		
		composite= createSubsection(body, manager, PreferencesMessages.SmartTypingConfigurationBlock_autoclose_title); 
		addAutoclosingSection(composite);
		
		composite= createSubsection(body, manager, PreferencesMessages.SmartTypingConfigurationBlock_automove_title); 
		addAutopositionSection(composite);
		
		composite= createSubsection(body, manager, PreferencesMessages.SmartTypingConfigurationBlock_tabs_title); 
		addTabSection(composite);

		composite= createSubsection(body, manager, PreferencesMessages.SmartTypingConfigurationBlock_pasting_title); 
		addPasteSection(composite);
		
		composite= createSubsection(body, manager, PreferencesMessages.SmartTypingConfigurationBlock_strings_title); 
		addStringsSection(composite);
		
		return control;
	}

	private void addStringsSection(Composite composite) {
		GridLayout layout= new GridLayout();
		composite.setLayout(layout);

		String label;
		Button master, slave;
		label= PreferencesMessages.JavaEditorPreferencePage_wrapStrings; 
		master= addCheckBox(composite, label, PreferenceConstants.EDITOR_WRAP_STRINGS, 0);
		
		label= PreferencesMessages.JavaEditorPreferencePage_escapeStrings; 
		slave= addCheckBox(composite, label, PreferenceConstants.EDITOR_ESCAPE_STRINGS, 0);
		createDependency(master, slave);
	}

	private void addPasteSection(Composite composite) {
		GridLayout layout= new GridLayout();
		composite.setLayout(layout);

		String label;
		label= PreferencesMessages.JavaEditorPreferencePage_smartPaste; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_SMART_PASTE, 0);

		label= PreferencesMessages.JavaEditorPreferencePage_importsOnPaste; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_IMPORTS_ON_PASTE, 0);
	}

	private void addTabSection(Composite composite) {
		GridLayout layout= new GridLayout();
		composite.setLayout(layout);

		String label;
		label= PreferencesMessages.JavaEditorPreferencePage_typing_smartTab; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_SMART_TAB, 0);
		
		createMessage(composite);
	}

	private void addAutopositionSection(Composite composite) {
		
		GridLayout layout= new GridLayout();
		composite.setLayout(layout);

		String label;
		
		label= PreferencesMessages.JavaEditorPreferencePage_typing_smartSemicolon; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_SMART_SEMICOLON, 0);
		
		label= PreferencesMessages.JavaEditorPreferencePage_typing_smartOpeningBrace; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_SMART_OPENING_BRACE, 0);
	}

	private void addAutoclosingSection(Composite composite) {
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		composite.setLayout(layout);

		String label;
		Button master, slave;

		label= PreferencesMessages.JavaEditorPreferencePage_closeStrings; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_CLOSE_STRINGS, 0);

		label= PreferencesMessages.JavaEditorPreferencePage_closeBrackets; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_CLOSE_BRACKETS, 0);

		label= PreferencesMessages.JavaEditorPreferencePage_closeBraces; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_CLOSE_BRACES, 0);

		label= PreferencesMessages.JavaEditorPreferencePage_closeJavaDocs; 
		master= addCheckBox(composite, label, PreferenceConstants.EDITOR_CLOSE_JAVADOCS, 0);

		label= PreferencesMessages.JavaEditorPreferencePage_addJavaDocTags; 
		slave= addCheckBox(composite, label, PreferenceConstants.EDITOR_ADD_JAVADOC_TAGS, 0);
		createDependency(master, slave);
	}
	
	private void createMessage(final Composite composite) {
		// TODO create a link with an argument, so the formatter preference page can open the 
		// current profile automatically.
		String linkTooltip= PreferencesMessages.SmartTypingConfigurationBlock_tabs_message_tooltip; 
		String text= Messages.format(PreferencesMessages.SmartTypingConfigurationBlock_tabs_message_text, new String[] {Integer.toString(getIndentSize()), getIndentChar()}); 
		
		final Link link= new Link(composite, SWT.NONE);
		link.setText(text);
		link.setToolTipText(linkTooltip);
		GridData gd= new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.widthHint= 300; // don't get wider initially
		link.setLayoutData(gd);
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(link.getShell(), "org.eclipse.jdt.ui.preferences.CodeFormatterPreferencePage", null, null); //$NON-NLS-1$
			}
		});
		
		final IPreferenceStore combinedStore= JavaPlugin.getDefault().getCombinedPreferenceStore();
		final IPropertyChangeListener propertyChangeListener= new IPropertyChangeListener() {
			private boolean fHasRun= false;
			public void propertyChange(PropertyChangeEvent event) {
				if (fHasRun)
					return;
				if (composite.isDisposed())
					return;
				String property= event.getProperty();
				if (DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR.equals(property)
						|| DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE.equals(property)) {
					fHasRun= true;
					link.dispose();
					createMessage(composite);
					Dialog.applyDialogFont(composite);
					composite.redraw();
					composite.layout();
				}
			}
		};
		combinedStore.addPropertyChangeListener(propertyChangeListener);
		link.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
					combinedStore.removePropertyChangeListener(propertyChangeListener);
				}
		});
	}
	
	private String getIndentChar() {
		boolean useSpace= JavaCore.SPACE.equals(JavaPlugin.getDefault().getCombinedPreferenceStore().getString(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR));
		if (useSpace)
			return PreferencesMessages.SmartTypingConfigurationBlock_tabs_message_spaces; 
		else
			return PreferencesMessages.SmartTypingConfigurationBlock_tabs_message_tabs; 
	}

	private int getIndentSize() {
		return JavaPlugin.getDefault().getCombinedPreferenceStore().getInt(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
	}


}
