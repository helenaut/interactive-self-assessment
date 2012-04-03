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

package edu.berkeley.eduride.isa.corext.refactoring.reorg;


public interface ICreateTargetQuery {
	/**
	 * Creates and returns a new target.
	 *
	 * @param selection the current destination
	 * @return the newly created target
	 */
	Object getCreatedTarget(Object selection);

	/**
	 * @return the label for the "Create ***..." button
	 */
	String getNewButtonLabel();
}