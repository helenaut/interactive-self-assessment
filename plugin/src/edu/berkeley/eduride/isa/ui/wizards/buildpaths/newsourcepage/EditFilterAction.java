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
package edu.berkeley.eduride.isa.ui.wizards.buildpaths.newsourcepage;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;


import org.eclipse.jdt.ui.PreferenceConstants;

import edu.berkeley.eduride.isa.corext.buildpath.BuildpathDelta;
import edu.berkeley.eduride.isa.corext.buildpath.ClasspathModifier;
import edu.berkeley.eduride.isa.ui.JavaPluginImages;
import edu.berkeley.eduride.isa.ui.util.ExceptionHandler;
import edu.berkeley.eduride.isa.ui.wizards.NewWizardMessages;
import edu.berkeley.eduride.isa.ui.wizards.buildpaths.CPListElement;
import edu.berkeley.eduride.isa.ui.wizards.buildpaths.EditFilterWizard;


//SelectedElements iff enabled: (IJavaProject || IPackageFragmentRoot) && size == 1
public class EditFilterAction extends BuildpathModifierAction {

	public EditFilterAction(IWorkbenchSite site) {
		this(site, null, PlatformUI.getWorkbench().getProgressService());
	}

	public EditFilterAction(IRunnableContext context, ISetSelectionTarget selectionTarget) {
		this(null, selectionTarget, context);
    }

	private EditFilterAction(IWorkbenchSite site, ISetSelectionTarget selectionTarget, IRunnableContext context) {
		super(site, selectionTarget, BuildpathModifierAction.EDIT_FILTERS);

		setText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_label);
		setImageDescriptor(JavaPluginImages.DESC_ELCL_CONFIGURE_BUILDPATH_FILTERS);
		setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_tooltip);
		setDescription(NewWizardMessages.PackageExplorerActionGroup_FormText_Edit);
		setDisabledImageDescriptor(JavaPluginImages.DESC_DLCL_CONFIGURE_BUILDPATH_FILTERS);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDetailedDescription() {
		if (!isEnabled())
			return null;


		return NewWizardMessages.PackageExplorerActionGroup_FormText_Edit;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		Shell shell= getShell();

		try {
			EditFilterWizard wizard= createWizard();
			wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(getSelectedElements().get(0)));

			WizardDialog dialog= new WizardDialog(shell, wizard);
			PixelConverter converter= new PixelConverter(JFaceResources.getDialogFont());
			dialog.setMinimumPageSize(converter.convertWidthInCharsToPixels(70), converter.convertHeightInCharsToPixels(20));
			dialog.create();
			int res= dialog.open();
			if (res == Window.OK) {
				BuildpathDelta delta= new BuildpathDelta(getToolTipText());

				ArrayList newEntries= wizard.getExistingEntries();
				delta.setNewEntries((CPListElement[])newEntries.toArray(new CPListElement[newEntries.size()]));

				IResource resource= wizard.getCreatedElement().getCorrespondingResource();
				delta.addCreatedResource(resource);

				delta.setDefaultOutputLocation(wizard.getOutputLocation());

				informListeners(delta);

				selectAndReveal(new StructuredSelection(wizard.getCreatedElement()));
			}

			notifyResult(res == Window.OK);
		} catch (CoreException e) {
			String title= NewWizardMessages.AbstractOpenWizardAction_createerror_title;
			String message= NewWizardMessages.AbstractOpenWizardAction_createerror_message;
			ExceptionHandler.handle(e, shell, title, message);
		}
	}

	private EditFilterWizard createWizard() throws CoreException {
		IJavaProject javaProject= null;
		Object firstElement= getSelectedElements().get(0);
		if (firstElement instanceof IJavaProject) {
			javaProject= (IJavaProject)firstElement;
		} else {
			javaProject= ((IPackageFragmentRoot)firstElement).getJavaProject();
		}
		CPListElement[] existingEntries= CPListElement.createFromExisting(javaProject);
		CPListElement elementToEdit= findElement((IJavaElement)firstElement, existingEntries);
		return new EditFilterWizard(existingEntries, elementToEdit, getOutputLocation(javaProject));
	}

	private IPath getOutputLocation(IJavaProject javaProject) {
		try {
			return javaProject.getOutputLocation();
		} catch (CoreException e) {
			IProject project= javaProject.getProject();
			IPath projPath= project.getFullPath();
			return projPath.append(PreferenceConstants.getPreferenceStore().getString(PreferenceConstants.SRCBIN_BINNAME));
		}
	}

	private static CPListElement findElement(IJavaElement element, CPListElement[] elements) {
		IPath path= element.getPath();
		for (int i= 0; i < elements.length; i++) {
			CPListElement cur= elements[i];
			if (cur.getEntryKind() == IClasspathEntry.CPE_SOURCE && cur.getPath().equals(path)) {
				return cur;
			}
		}
		return null;
	}

	protected boolean canHandle(IStructuredSelection selection) {
		if (selection.size() != 1)
			return false;

		try {
			Object element= selection.getFirstElement();
			if (element instanceof IJavaProject) {
				return ClasspathModifier.isSourceFolder((IJavaProject)element);
			} else if (element instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot packageFragmentRoot= ((IPackageFragmentRoot) element);
				if (packageFragmentRoot.getKind() != IPackageFragmentRoot.K_SOURCE)
					return false;

				return packageFragmentRoot.getJavaProject() != null;
			}
		} catch (JavaModelException e) {
		}
		return false;
	}
}