/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.berkeley.eduride.isa.ui.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.core.resources.IProject;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import edu.berkeley.eduride.isa.ui.IJavaHelpContextIds;


/**
 * Content Assist > Static Members preference page.
 * <p>
 * Note: Must be public since it is referenced from plugin.xml
 * </p>
 *
 * @since 3.3
 */
public class CodeAssistFavoritesPreferencePage extends PropertyAndPreferencePage {

	/**
	 * The ID of this preference page.
	 * 
	 * @since 3.6
	 */
	public static final String PAGE_ID= "org.eclipse.jdt.ui.preferences.CodeAssistPreferenceFavorites"; //$NON-NLS-1$

	private CodeAssistFavoritesConfigurationBlock fConfigurationBlock;

	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container= (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock= new CodeAssistFavoritesConfigurationBlock(getNewStatusChangedListener(), container);

		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaHelpContextIds.JAVA_EDITOR_PREFERENCE_PAGE);
	}

	protected Control createPreferenceContent(Composite composite) {
		return fConfigurationBlock.createContents(composite);
	}

	protected boolean hasProjectSpecificOptions(IProject project) {
		return false;
	}

	protected String getPreferencePageID() {
		return PAGE_ID;
	}

	protected String getPropertyPageID() {
		return null;
	}

		/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.dispose();
		}
		super.dispose();
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		super.performDefaults();
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performDefaults();
		}
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		if (fConfigurationBlock != null && !fConfigurationBlock.performOk()) {
			return false;
		}
		return super.performOk();
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performApply()
	 */
	public void performApply() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performApply();
		}
	}

}
