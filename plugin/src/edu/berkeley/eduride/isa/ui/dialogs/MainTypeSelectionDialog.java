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
package edu.berkeley.eduride.isa.ui.dialogs;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jface.operation.IRunnableContext;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchScope;

import org.eclipse.jdt.ui.JavaElementLabelProvider;

import edu.berkeley.eduride.isa.ui.IJavaHelpContextIds;
import edu.berkeley.eduride.isa.ui.JavaUIMessages;
import edu.berkeley.eduride.isa.ui.util.ExceptionHandler;
import edu.berkeley.eduride.isa.ui.util.MainMethodSearchEngine;

/**
 * A dialog to select a type from a list of types. The dialog allows
 * multiple selections.
 */
public class MainTypeSelectionDialog extends TwoPaneElementSelector {

	private IRunnableContext fRunnableContext;
	private IJavaSearchScope fScope;
	private int fStyle;

	private static class PackageRenderer extends JavaElementLabelProvider {
		public PackageRenderer() {
			super(JavaElementLabelProvider.SHOW_PARAMETERS | JavaElementLabelProvider.SHOW_POST_QUALIFIED | JavaElementLabelProvider.SHOW_ROOT);
		}

		public Image getImage(Object element) {
			return super.getImage(((IType)element).getPackageFragment());
		}

		public String getText(Object element) {
			return super.getText(((IType)element).getPackageFragment());
		}
	}

	public MainTypeSelectionDialog(Shell shell, IRunnableContext context, IJavaSearchScope scope, int style) {
		super(shell, new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_BASICS | JavaElementLabelProvider.SHOW_OVERLAY_ICONS),
			new PackageRenderer());

		Assert.isNotNull(context);
		Assert.isNotNull(scope);

		fRunnableContext= context;
		fScope= scope;
		fStyle= style;
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.MAINTYPE_SELECTION_DIALOG);
	}

	/*
	 * @see Window#open()
	 */
	public int open() {
		MainMethodSearchEngine engine= new MainMethodSearchEngine();
		IType[] types;
		try {
			types= engine.searchMainMethods(fRunnableContext, fScope, fStyle);
		} catch (InterruptedException e) {
			return CANCEL;
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, JavaUIMessages.MainTypeSelectionDialog_errorTitle, e.getMessage());
			return CANCEL;
		}

		setElements(types);
		return super.open();
	}

}
