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
package edu.berkeley.eduride.isa.corext.fix;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.jdt.ui.cleanup.ICleanUpFix;


public abstract class AbstractFix implements ICleanUpFix, IProposableFix, ILinkedFix {

	private final String fDisplayString;

	protected AbstractFix(String displayString) {
		fDisplayString= displayString;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getAdditionalProposalInfo() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayString() {
		return fDisplayString;
	}

	/**
	 * {@inheritDoc}
	 */
	public LinkedProposalModel getLinkedPositions() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IStatus getStatus() {
		return null;
	}
}
