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

package edu.berkeley.eduride.isa.corext.refactoring.changes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.core.resources.IResource;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import edu.berkeley.eduride.isa.corext.refactoring.RefactoringCoreMessages;

public class CreatePackageChange extends ResourceChange {

	private IPackageFragment fPackageFragment;

	public CreatePackageChange(IPackageFragment pack) {
		fPackageFragment= pack;
	}

	public RefactoringStatus isValid(IProgressMonitor pm) {
		// Don't do any checking. Peform handles the case
		// that the package already exists. Furthermore
		// create package change isn't used as a undo
		// redo change right now
		return new RefactoringStatus();
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask(RefactoringCoreMessages.CreatePackageChange_Creating_package, 1);

			if (fPackageFragment.exists()) {
				return new NullChange();
			} else {
				IPackageFragmentRoot root= (IPackageFragmentRoot) fPackageFragment.getParent();
				root.createPackageFragment(fPackageFragment.getElementName(), false, pm);

				return new DeleteResourceChange(fPackageFragment.getPath(), true);
			}
		} finally {
			pm.done();
		}
	}

	public String getName() {
		return RefactoringCoreMessages.CreatePackageChange_Create_package;
	}

	public Object getModifiedElement() {
		return fPackageFragment;
	}

	/* (non-Javadoc)
	 * @see edu.berkeley.eduride.isa.corext.refactoring.base.JDTChange#getModifiedResource()
	 */
	protected IResource getModifiedResource() {
		return fPackageFragment.getResource();
	}
}
