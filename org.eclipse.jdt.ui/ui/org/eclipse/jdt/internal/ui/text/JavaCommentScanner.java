/**********************************************************************
Copyright (c) 2000, 2002 IBM Corp. and others.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html

Contributors:
    IBM Corporation - Initial implementation
**********************************************************************/
package org.eclipse.jdt.internal.ui.text;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jdt.core.JavaCore;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.util.PropertyChangeEvent;


/**
 * AbstractJavaCommentScanner.java
 */
public class JavaCommentScanner extends AbstractJavaScanner{

	private static class TaskTagDetector implements IWordDetector {

		public boolean isWordStart(char c) {
			return Character.isLetter(c);
		}

		public boolean isWordPart(char c) {
			return Character.isLetter(c);
		}
	};

	private class TaskTagRule extends WordRule {

		private IToken fToken;

		public TaskTagRule(IToken token) {
			super(new TaskTagDetector(), Token.UNDEFINED);
			fToken= token;
		}
	
		public void clearTaskTags() {
			fWords.clear();
		}
	
		public void addTaskTags(String value) {
			String[] tasks= value.split(",");
			for (int i= 0; i < tasks.length; i++) {
				if (tasks[i].length() > 0) {
					addWord(tasks[i], fToken);
				}
			}
		}
	
	}
	
	private static final String COMPILER_TASK_TAGS= JavaCore.COMPILER_TASK_TAGS;	
	protected static final String TASK_TAG= IJavaColorConstants.TASK_TAG;

	private TaskTagRule fTaskTagRule;
	private Preferences fCorePreferenceStore;
	private String fDefaultTokenProperty;
	private String[] fTokenProperties;

	public JavaCommentScanner(IColorManager manager, IPreferenceStore store, Preferences coreStore, String defaultTokenProperty) {
		this(manager, store, coreStore, defaultTokenProperty, new String[] { defaultTokenProperty, TASK_TAG });
	}
	
	public JavaCommentScanner(IColorManager manager, IPreferenceStore store, Preferences coreStore, String defaultTokenProperty, String[] tokenProperties) {
		super(manager, store);
		
		fCorePreferenceStore= coreStore;
		fDefaultTokenProperty= defaultTokenProperty;
		fTokenProperties= tokenProperties;

		initialize();
	}

	/*
	 * @see AbstractJavaScanner#createRules()
	 */
	protected List createRules() {
		List list= new ArrayList();
		
		if (fCorePreferenceStore != null) {
			// Add rule for Task Tags.
			fTaskTagRule= new TaskTagRule(getToken(TASK_TAG));
			String tasks= fCorePreferenceStore.getString(COMPILER_TASK_TAGS);
			fTaskTagRule.addTaskTags(tasks);
			list.add(fTaskTagRule);
		}

		setDefaultReturnToken(getToken(fDefaultTokenProperty));

		return list;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#affectsBehavior(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public boolean affectsBehavior(PropertyChangeEvent event) {
		return event.getProperty().equals(COMPILER_TASK_TAGS) || super.affectsBehavior(event);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#adaptToPreferenceChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void adaptToPreferenceChange(PropertyChangeEvent event) {
		if (fTaskTagRule != null && event.getProperty().equals(COMPILER_TASK_TAGS)) {
			Object value= event.getNewValue();

			if (value instanceof String) {
				fTaskTagRule.clearTaskTags();
				fTaskTagRule.addTaskTags((String) value);
			}
			
		} else if (super.affectsBehavior(event)) {
			super.adaptToPreferenceChange(event);
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#getTokenProperties()
	 */
	protected String[] getTokenProperties() {
		return fTokenProperties;
	}

}

