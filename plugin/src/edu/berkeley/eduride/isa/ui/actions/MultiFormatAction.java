/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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

import org.eclipse.jdt.core.ICompilationUnit;


import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.ICleanUp;

import edu.berkeley.eduride.isa.corext.fix.CleanUpConstants;
import edu.berkeley.eduride.isa.ui.fix.CodeFormatCleanUp;

/**
 * @since 3.4
 */
public class MultiFormatAction extends CleanUpAction {

	public MultiFormatAction(IWorkbenchSite site) {
		super(site);
	}

	/*
	 * @see edu.berkeley.eduride.isa.ui.actions.CleanUpAction#createCleanUps(org.eclipse.jdt.core.ICompilationUnit[])
	 */
	protected ICleanUp[] getCleanUps(ICompilationUnit[] units) {
		Map settings= new Hashtable();
		settings.put(CleanUpConstants.FORMAT_SOURCE_CODE, CleanUpOptions.TRUE);

		return new ICleanUp[] {
				new CodeFormatCleanUp(settings)
		};
	}

	/*
	 * @see edu.berkeley.eduride.isa.ui.actions.CleanUpAction#getActionName()
	 */
	protected String getActionName() {
		return ActionMessages.FormatAllAction_label;
	}

}
