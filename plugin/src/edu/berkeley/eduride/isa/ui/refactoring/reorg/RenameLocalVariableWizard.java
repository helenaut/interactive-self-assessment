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
package edu.berkeley.eduride.isa.ui.refactoring.reorg;

import org.eclipse.ltk.core.refactoring.Refactoring;

import edu.berkeley.eduride.isa.ui.IJavaHelpContextIds;
import edu.berkeley.eduride.isa.ui.JavaPluginImages;
import edu.berkeley.eduride.isa.ui.refactoring.RefactoringMessages;

public final class RenameLocalVariableWizard extends RenameRefactoringWizard {

	public RenameLocalVariableWizard(Refactoring refactoring) {
		super(
				refactoring,
				RefactoringMessages.RenameLocalVariableWizard_defaultPageTitle,
				RefactoringMessages.RenameTypeParameterWizard_inputPage_description,
				JavaPluginImages.DESC_WIZBAN_REFACTOR, IJavaHelpContextIds.RENAME_LOCAL_VARIABLE_WIZARD_PAGE);
	}
}
