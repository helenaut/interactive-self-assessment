/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation
 *          (report 36180: Callers/Callees view)
 *******************************************************************************/
package edu.berkeley.eduride.isa.ui.callhierarchy;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredViewer;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionGroup;

import edu.berkeley.eduride.isa.ui.JavaPluginImages;

/**
 * Action group to add the filter actions to a view part's toolbar
 * menu.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class CallHierarchyFiltersActionGroup extends ActionGroup {

    class ShowFilterDialogAction extends Action {
        ShowFilterDialogAction() {
            setText(CallHierarchyMessages.ShowFilterDialogAction_text);
            setImageDescriptor(JavaPluginImages.DESC_ELCL_FILTER);
			setDisabledImageDescriptor(JavaPluginImages.DESC_DLCL_FILTER);
        }

        public void run() {
            openFiltersDialog();
        }
    }

    class ShowExpandWithConstructorsDialogAction extends Action {
    	ShowExpandWithConstructorsDialogAction() {
    		setText(CallHierarchyMessages.ShowExpandWithConstructorsDialogAction_text);
    	}
    	
    	public void run() {
    		openExpandWithConstructorsDialog();
    	}
    }
    
    private IViewPart fPart;

    /**
     * Creates a new <code>CustomFiltersActionGroup</code>.
     *
     * @param part      the view part that owns this action group
     * @param viewer    the viewer to be filtered
     */
    public CallHierarchyFiltersActionGroup(IViewPart part, StructuredViewer viewer) {
        Assert.isNotNull(part);
        Assert.isNotNull(viewer);
        fPart= part;
    }

    /* (non-Javadoc)
     * Method declared on ActionGroup.
     */
    public void fillActionBars(IActionBars actionBars) {
        fillViewMenu(actionBars.getMenuManager());
    }

    private void fillViewMenu(IMenuManager viewMenu) {
        viewMenu.add(new Separator("filters")); //$NON-NLS-1$
        viewMenu.add(new ShowFilterDialogAction());
        viewMenu.add(new ShowExpandWithConstructorsDialogAction());
    }

    /* (non-Javadoc)
     * Method declared on ActionGroup.
     */
    public void dispose() {
        super.dispose();
    }

    // ---------- dialog related code ----------

    private void openFiltersDialog() {
        FiltersDialog dialog= new FiltersDialog(
            fPart.getViewSite().getShell());

        dialog.open();
    }
    
    private void openExpandWithConstructorsDialog() {
    	Shell parentShell= fPart.getViewSite().getShell();
		new ExpandWithConstructorsDialog(parentShell).open();
    }
}
