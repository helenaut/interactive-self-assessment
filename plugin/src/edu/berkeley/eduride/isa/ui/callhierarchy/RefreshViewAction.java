/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation
 * 			(report 36180: Callers/Callees view)
 *******************************************************************************/
package edu.berkeley.eduride.isa.ui.callhierarchy;

import org.eclipse.jface.action.Action;

import org.eclipse.ui.PlatformUI;

import edu.berkeley.eduride.isa.ui.IJavaHelpContextIds;
import edu.berkeley.eduride.isa.ui.JavaPluginImages;

class RefreshViewAction extends Action {
    private CallHierarchyViewPart fPart;

	public RefreshViewAction(CallHierarchyViewPart part) {
		fPart= part;
		setText(CallHierarchyMessages.RefreshViewAction_text);
		setToolTipText(CallHierarchyMessages.RefreshViewAction_tooltip);
		JavaPluginImages.setLocalImageDescriptors(this, "refresh_nav.gif");//$NON-NLS-1$
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.CALL_HIERARCHY_REFRESH_VIEW_ACTION);
		setEnabled(false);
	}

    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        fPart.refresh();
    }
}
