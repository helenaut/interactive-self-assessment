/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.berkeley.eduride.isa.ui.text;


import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;

import org.eclipse.ui.IEditorPart;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;

import edu.berkeley.eduride.isa.ui.actions.SelectionConverter;
import edu.berkeley.eduride.isa.ui.javaeditor.EditorUtility;
import edu.berkeley.eduride.isa.ui.javaeditor.JavaEditor;

/**
 * Provides a Java element to be displayed in by an information presenter.
 */
public class JavaElementProvider implements IInformationProvider, IInformationProviderExtension {

	private JavaEditor fEditor;
	private boolean fUseCodeResolve;

	public JavaElementProvider(IEditorPart editor) {
		fUseCodeResolve= false;
		if (editor instanceof JavaEditor)
			fEditor= (JavaEditor)editor;
	}

	public JavaElementProvider(IEditorPart editor, boolean useCodeResolve) {
		this(editor);
		fUseCodeResolve= useCodeResolve;
	}

	/*
	 * @see IInformationProvider#getSubject(ITextViewer, int)
	 */
	public IRegion getSubject(ITextViewer textViewer, int offset) {
		if (textViewer != null && fEditor != null) {
			IRegion region= JavaWordFinder.findWord(textViewer.getDocument(), offset);
			if (region != null)
				return region;
			else
				return new Region(offset, 0);
		}
		return null;
	}

	/*
	 * @see IInformationProvider#getInformation(ITextViewer, IRegion)
	 */
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		return getInformation2(textViewer, subject).toString();
	}

	/*
	 * @see IInformationProviderExtension#getElement(ITextViewer, IRegion)
	 */
	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
		if (fEditor == null)
			return null;

		try {
			if (fUseCodeResolve) {
				IStructuredSelection sel= SelectionConverter.getStructuredSelection(fEditor);
				if (!sel.isEmpty())
					return sel.getFirstElement();
			}
			IJavaElement element= SelectionConverter.getElementAtOffset(fEditor, false);
			if (element != null)
				return element;

			return EditorUtility.getEditorInputJavaElement(fEditor, false);
		} catch (JavaModelException e) {
			return null;
		}
	}
}
