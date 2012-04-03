/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.berkeley.eduride.isa.ui.wizards.buildpaths.newsourcepage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;


import org.eclipse.jdt.ui.JavaElementLabels;

import edu.berkeley.eduride.isa.corext.buildpath.BuildpathDelta;
import edu.berkeley.eduride.isa.corext.buildpath.CPJavaProject;
import edu.berkeley.eduride.isa.corext.buildpath.ClasspathModifier;
import edu.berkeley.eduride.isa.corext.util.JavaModelUtil;
import edu.berkeley.eduride.isa.corext.util.Messages;
import edu.berkeley.eduride.isa.ui.JavaPlugin;
import edu.berkeley.eduride.isa.ui.JavaPluginImages;
import edu.berkeley.eduride.isa.ui.packageview.ClassPathContainer;
import edu.berkeley.eduride.isa.ui.wizards.NewWizardMessages;
import edu.berkeley.eduride.isa.ui.wizards.buildpaths.CPListElement;
import edu.berkeley.eduride.isa.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.IRemoveLinkedFolderQuery;

//SelectedElements iff enabled: IPackageFramgentRoot || IJavaProject || ClassPathContainer
public class RemoveFromBuildpathAction extends BuildpathModifierAction {

	private final IRunnableContext fContext;

	public RemoveFromBuildpathAction(IWorkbenchSite site) {
		this(site, null, PlatformUI.getWorkbench().getProgressService());

		setImageDescriptor(JavaPluginImages.DESC_ELCL_REMOVE_FROM_BP);
	}

	public RemoveFromBuildpathAction(IRunnableContext context, ISetSelectionTarget selectionTarget) {
		this(null, selectionTarget, context);

		setImageDescriptor(JavaPluginImages.DESC_ELCL_REMOVE_AS_SOURCE_FOLDER);
		setDisabledImageDescriptor(JavaPluginImages.DESC_DLCL_REMOVE_AS_SOURCE_FOLDER);
    }

	public RemoveFromBuildpathAction(IWorkbenchSite site, ISetSelectionTarget selectionTarget, IRunnableContext context) {
		super(site, selectionTarget, BuildpathModifierAction.REMOVE_FROM_BP);

		fContext= context;

		setText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_RemoveFromCP_label);
		setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_RemoveFromCP_tooltip);
    }

	/**
	 * {@inheritDoc}
	 */
	public String getDetailedDescription() {
		if (!isEnabled())
			return null;

		if (getSelectedElements().size() != 1)
			return NewWizardMessages.PackageExplorerActionGroup_FormText_Default_FromBuildpath;

		Object elem= getSelectedElements().get(0);

        if (elem instanceof IJavaProject) {
            String name= ClasspathModifier.escapeSpecialChars(JavaElementLabels.getElementLabel(((IJavaElement)elem), JavaElementLabels.ALL_DEFAULT));
        	return Messages.format(NewWizardMessages.PackageExplorerActionGroup_FormText_ProjectFromBuildpath, name);
        } else if (elem instanceof IPackageFragmentRoot) {
            String name= ClasspathModifier.escapeSpecialChars(JavaElementLabels.getElementLabel(((IJavaElement)elem), JavaElementLabels.ALL_DEFAULT));
        	return Messages.format(NewWizardMessages.PackageExplorerActionGroup_FormText_fromBuildpath, name);
        } else if (elem instanceof ClassPathContainer) {
        	return NewWizardMessages.PackageExplorerActionGroup_FormText_Default_FromBuildpath;
        }

        return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		try {

			final IJavaProject project;
			Object object= getSelectedElements().get(0);
			if (object instanceof IJavaProject) {
				project= (IJavaProject)object;
			} else if (object instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot root= (IPackageFragmentRoot)object;
				project= root.getJavaProject();
			} else {
				ClassPathContainer container= (ClassPathContainer)object;
				project= container.getJavaProject();
			}

			final List elementsToRemove= new ArrayList();
			final List foldersToDelete= new ArrayList();
			queryToRemoveLinkedFolders(elementsToRemove, foldersToDelete);

			final IRunnableWithProgress runnable= new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_RemoveFromBuildpath, foldersToDelete.size() + 10);

						CPJavaProject cpProject= CPJavaProject.createFromExisting(project);
						CPListElement[] toRemove= new CPListElement[elementsToRemove.size()];
						int i= 0;
						for (Iterator iterator= elementsToRemove.iterator(); iterator.hasNext();) {
							Object element= iterator.next();
							if (element instanceof IJavaProject) {
								toRemove[i]= ClasspathModifier.getListElement(((IJavaProject)element).getPath(), cpProject.getCPListElements());
							} else if (element instanceof IPackageFragmentRoot) {
								toRemove[i]= CPListElement.createFromExisting(((IPackageFragmentRoot)element).getRawClasspathEntry(), project);
							} else {
								toRemove[i]= CPListElement.createFromExisting(((ClassPathContainer)element).getClasspathEntry(), project);
							}
	                        i++;
                        }

						BuildpathDelta delta= ClasspathModifier.removeFromBuildpath(toRemove, cpProject);
						ClasspathModifier.commitClassPath(cpProject, new SubProgressMonitor(monitor, 10));

						deleteFolders(foldersToDelete, new SubProgressMonitor(monitor, foldersToDelete.size()));

						informListeners(delta);

						if (delta.getDeletedResources().length == foldersToDelete.size()) {
							selectAndReveal(new StructuredSelection(project));
						} else {
							List result= new ArrayList(Arrays.asList(delta.getDeletedResources()));
							result.removeAll(foldersToDelete);
							selectAndReveal(new StructuredSelection(result));
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			};
			fContext.run(false, false, runnable);

		} catch (CoreException e) {
			showExceptionDialog(e, NewWizardMessages.RemoveFromBuildpathAction_ErrorTitle);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				showExceptionDialog((CoreException)e.getCause(), NewWizardMessages.RemoveFromBuildpathAction_ErrorTitle);
			} else {
				JavaPlugin.log(e);
			}
		} catch (InterruptedException e) {
		}
	}

	private void deleteFolders(List folders, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_RemoveFromBuildpath, folders.size());

			for (Iterator iter= folders.iterator(); iter.hasNext();) {
				IFolder folder= (IFolder)iter.next();
				folder.delete(true, true, new SubProgressMonitor(monitor, 1));
			}
		} finally {
			monitor.done();
		}
	}

	private void queryToRemoveLinkedFolders(final List elementsToRemove, final List foldersToDelete) throws JavaModelException {
		final Shell shell= getShell();
		for (Iterator iter= getSelectedElements().iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IPackageFragmentRoot) {
				IFolder folder= getLinkedSourceFolder((IPackageFragmentRoot)element);
				if (folder != null) {
					RemoveLinkedFolderDialog dialog= new RemoveLinkedFolderDialog(shell, folder);

					final int result= dialog.open() == Window.OK?dialog.getRemoveStatus():IRemoveLinkedFolderQuery.REMOVE_CANCEL;

					if (result != IRemoveLinkedFolderQuery.REMOVE_CANCEL) {
						if (result == IRemoveLinkedFolderQuery.REMOVE_BUILD_PATH) {
							elementsToRemove.add(element);
						} else if (result == IRemoveLinkedFolderQuery.REMOVE_BUILD_PATH_AND_FOLDER) {
							elementsToRemove.add(element);
							foldersToDelete.add(folder);
						}
					}
				} else {
					elementsToRemove.add(element);
				}
			} else {
				elementsToRemove.add(element);
			}
		}
	}

	private IFolder getLinkedSourceFolder(IPackageFragmentRoot root) throws JavaModelException {
		if (root.getKind() != IPackageFragmentRoot.K_SOURCE)
			return null;

		final IResource resource= root.getCorrespondingResource();
		if (!(resource instanceof IFolder))
			return null;

		final IFolder folder= (IFolder) resource;
		if (!folder.isLinked())
			return null;

		return folder;
	}

	protected boolean canHandle(IStructuredSelection elements) {
		if (elements.size() == 0)
			return false;

		try {
			for (Iterator iter= elements.iterator(); iter.hasNext();) {
				Object element= iter.next();

				if (element instanceof IJavaProject) {
					IJavaProject project= (IJavaProject)element;
					if (!ClasspathModifier.isSourceFolder(project))
						return false;

				} else if (element instanceof IPackageFragmentRoot) {
					IClasspathEntry entry= JavaModelUtil.getClasspathEntry((IPackageFragmentRoot) element);
					if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER)
						return false;
					if (entry.getReferencingEntry() != null)
						return false;
				} else if (element instanceof ClassPathContainer) {
					return true;
				} else {
					return false;
				}
			}
			return true;
		} catch (JavaModelException e) {
		}
		return false;
	}
}