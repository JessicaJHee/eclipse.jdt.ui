package org.eclipse.jdt.internal.ui.javaeditor;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;

import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaElementLabelProvider;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.codemanipulation.AddImportsOperation;
import org.eclipse.jdt.internal.ui.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.ui.dialogs.ElementListSelectionDialog;

public class AddImportOnSelectionAction extends Action implements IUpdate {
		
	private ITextEditor fEditor;
	
	public AddImportOnSelectionAction(ITextEditor editor) {	
		super(JavaEditorMessages.getString("AddImportOnSelection.label"));		 //$NON-NLS-1$
		setToolTipText(JavaEditorMessages.getString("AddImportOnSelection.tooltip")); //$NON-NLS-1$
		setDescription(JavaEditorMessages.getString("AddImportOnSelection.description")); //$NON-NLS-1$
		
		fEditor= editor;
		WorkbenchHelp.setHelp(this,	new Object[] { IJavaHelpContextIds.ADD_IMPORT_ON_SELECTION_ACTION });	
	}
	
	public AddImportOnSelectionAction() {
		this(null);
	}
	
	public void setContentEditor(ITextEditor editor) {
		fEditor= editor;
	}
	
	public void update() {
		boolean isEnabled= false;
		ISelection selection= fEditor.getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			isEnabled= (((ITextSelection)selection).getLength() > 0);
		}
		setEnabled(isEnabled);
	}	
			
	private ICompilationUnit getCompilationUnit () {
		IWorkingCopyManager manager= JavaPlugin.getDefault().getWorkingCopyManager();				
		return manager.getWorkingCopy(fEditor.getEditorInput());
	}


	private void addImport(IJavaElement imp, ICompilationUnit cu) {
		AddImportsOperation op= new AddImportsOperation(cu, new IJavaElement[] { imp }, false);
		try {
			ProgressMonitorDialog dialog= new ProgressMonitorDialog(getShell());
			dialog.run(false, true, op);
		} catch (InvocationTargetException e) {
			MessageDialog.openError(getShell(), JavaEditorMessages.getString("AddImportOnSelection.error.title"), e.getTargetException().getMessage()); //$NON-NLS-1$
			JavaPlugin.log(e);
		} catch (InterruptedException e) {
			// Do nothing. Operation has been canceled.
		}
	}
	
	/**
	 * @see IAction#actionPerformed
	 */
	public void run() {
		ICompilationUnit cu= getCompilationUnit();
		if (cu != null) {
			ISelection s= fEditor.getSelectionProvider().getSelection();
			IDocument doc= fEditor.getDocumentProvider().getDocument(fEditor.getEditorInput());
			ITextSelection selection= (ITextSelection) s;
			
			if (selection.getLength() > 0 && doc != null) {
				try {
					int selStart= selection.getOffset();
					int nameStart= getNameStart(doc, selStart);
					int len= selStart - nameStart + selection.getLength();
					
					String name= doc.get(nameStart, len).trim();
					String typeName= Signature.getSimpleName(name);
					String packName= Signature.getQualifier(name);
					
					IType[] types= StubUtility.findAllTypes(typeName, cu.getJavaProject(), null);
					IType chosen= selectResult(types, packName, getShell());
					if (chosen != null) {
						removeQualification(doc, nameStart, chosen);
						addImport(chosen, cu);
						return;
					}
				} catch (CoreException e) {
					ErrorDialog.openError(getShell(), JavaEditorMessages.getString("AddImportOnSelection.error.title"), null, e.getStatus()); //$NON-NLS-1$
					JavaPlugin.log(e);
				} catch (BadLocationException e) {
					MessageDialog.openError(getShell(), JavaEditorMessages.getString("AddImportOnSelection.error.title"), e.getMessage()); //$NON-NLS-1$
					JavaPlugin.log(e);
				}
			}
		}
		getShell().getDisplay().beep();		
	}
	
	private int getNameStart(IDocument doc, int pos) throws BadLocationException {
		if (pos > 0 && doc.getChar(pos - 1) == '.') {
			pos--;
			while (pos > 0) {
				char ch= doc.getChar(pos - 1);
				if (!Character.isJavaIdentifierPart(ch) && ch != '.') {
					return pos;
				}
				pos--;
			}
		}
		return pos;
	}	
	
	private void removeQualification(IDocument doc, int nameStart, IType type) throws BadLocationException {
		String packName= type.getPackageFragment().getElementName();
		int packLen= packName.length();
		if (packLen > 0) {
			for (int k= 0; k < packLen; k++) {
				if (doc.getChar(nameStart + k) != packName.charAt(k)) {
					return;
				}
			}
			doc.replace(nameStart, packLen + 1, ""); //$NON-NLS-1$
		}
	}	
	
	protected Shell getShell() {
		return fEditor.getSite().getShell();
	}
		
						
	private IType selectResult(IType[] results, String packName, Shell shell) {
		int nResults= results.length;
		
		if (nResults == 0) {
			return null;
		} else if (nResults == 1) {
			return results[0];
		}
		
		if (packName.length() != 0) {
			for (int i= 0; i < results.length; i++) {
				IType curr= (IType) results[i];
				if (packName.equals(curr.getPackageFragment().getElementName())) {
					return curr;
				}
			}
		}		
		int flags= (JavaElementLabelProvider.SHOW_DEFAULT | JavaElementLabelProvider.SHOW_CONTAINER_QUALIFICATION);
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(),
			new JavaElementLabelProvider(flags), true, false);
		dialog.setTitle(			JavaEditorMessages.getString("AddImportOnSelection.dialog.title")); //$NON-NLS-1$
		dialog.setMessage(JavaEditorMessages.getString("AddImportOnSelection.dialog.message")); //$NON-NLS-1$
		dialog.setElements(results);

		return (dialog.open() == dialog.OK)
			? (IType) dialog.getSelectedElement()
			: null;
	}
}