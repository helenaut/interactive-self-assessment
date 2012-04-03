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
package edu.berkeley.eduride.isa.ui.actions;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

import org.eclipse.jdt.core.ICompilationUnit;


import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.ICleanUp;

import edu.berkeley.eduride.isa.corext.fix.CleanUpConstants;
import edu.berkeley.eduride.isa.ui.IJavaHelpContextIds;
import edu.berkeley.eduride.isa.ui.fix.ImportsCleanUp;
import edu.berkeley.eduride.isa.ui.javaeditor.JavaEditor;

public class MultiOrganizeImportAction extends CleanUpAction {

	public MultiOrganizeImportAction(IWorkbenchSite site) {
		super(site);

		setText(ActionMessages.OrganizeImportsAction_label);
		setToolTipText(ActionMessages.OrganizeImportsAction_tooltip);
		setDescription(ActionMessages.OrganizeImportsAction_description);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.ORGANIZE_IMPORTS_ACTION);
	}

	public MultiOrganizeImportAction(JavaEditor editor) {
		super(editor);

		setText(ActionMessages.OrganizeImportsAction_label);
		setToolTipText(ActionMessages.OrganizeImportsAction_tooltip);
		setDescription(ActionMessages.OrganizeImportsAction_description);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.ORGANIZE_IMPORTS_ACTION);
	}

	protected ICleanUp[] getCleanUps(ICompilationUnit[] units) {
		Map settings= new Hashtable();
		settings.put(CleanUpConstants.ORGANIZE_IMPORTS, CleanUpOptions.TRUE);
		ImportsCleanUp importsCleanUp= new ImportsCleanUp(settings);

		return new ICleanUp[] {
			importsCleanUp
		};
	}

	protected String getActionName() {
		return ActionMessages.OrganizeImportsAction_error_title;
	}
}