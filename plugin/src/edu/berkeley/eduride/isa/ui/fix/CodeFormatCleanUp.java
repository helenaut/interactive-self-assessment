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
package edu.berkeley.eduride.isa.ui.fix;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.text.IRegion;

import org.eclipse.jdt.core.ICompilationUnit;


import org.eclipse.jdt.ui.cleanup.CleanUpContext;
import org.eclipse.jdt.ui.cleanup.CleanUpRequirements;
import org.eclipse.jdt.ui.cleanup.ICleanUpFix;

import edu.berkeley.eduride.isa.corext.fix.CleanUpConstants;
import edu.berkeley.eduride.isa.corext.fix.CodeFormatFix;
import edu.berkeley.eduride.isa.ui.fix.IMultiLineCleanUp.MultiLineCleanUpContext;

public class CodeFormatCleanUp extends AbstractCleanUp {

	public CodeFormatCleanUp() {
		super();
	}

	public CodeFormatCleanUp(Map options) {
		super(options);
	}

	/**
	 * {@inheritDoc}
	 */
	public CleanUpRequirements getRequirements() {
		boolean requiresChangedRegions= isEnabled(CleanUpConstants.FORMAT_SOURCE_CODE) && isEnabled(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);
		return new CleanUpRequirements(false, false, requiresChangedRegions, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public ICleanUpFix createFix(CleanUpContext context) throws CoreException {
		ICompilationUnit compilationUnit= context.getCompilationUnit();
		if (compilationUnit == null)
			return null;

		IRegion[] regions;
		if (context instanceof MultiLineCleanUpContext) {
			regions= ((MultiLineCleanUpContext)context).getRegions();
		} else {
			regions= null;
		}

		boolean removeWhitespaces= isEnabled(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES);
		return CodeFormatFix.createCleanUp(compilationUnit,
				regions,
				isEnabled(CleanUpConstants.FORMAT_SOURCE_CODE),
				removeWhitespaces && isEnabled(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES_ALL),
				removeWhitespaces && isEnabled(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES_IGNORE_EMPTY),
				isEnabled(CleanUpConstants.FORMAT_CORRECT_INDENTATION));
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getStepDescriptions() {
		ArrayList result= new ArrayList();
		if (isEnabled(CleanUpConstants.FORMAT_SOURCE_CODE))
			result.add(MultiFixMessages.CodeFormatCleanUp_description);

		if (isEnabled(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES)) {
			if (isEnabled(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES_ALL)) {
				result.add(MultiFixMessages.CodeFormatCleanUp_RemoveTrailingAll_description);
			} else if (isEnabled(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES_IGNORE_EMPTY)) {
				result.add(MultiFixMessages.CodeFormatCleanUp_RemoveTrailingNoEmpty_description);
			}
		}

		if (isEnabled(CleanUpConstants.FORMAT_CORRECT_INDENTATION))
			result.add(MultiFixMessages.CodeFormatCleanUp_correctIndentation_description);

		return (String[])result.toArray(new String[result.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPreview() {
		StringBuffer buf= new StringBuffer();
		buf.append("/**\n"); //$NON-NLS-1$
		buf.append(" *A Javadoc comment\n"); //$NON-NLS-1$
		buf.append("* @since 2007\n"); //$NON-NLS-1$
		buf.append(" */\n"); //$NON-NLS-1$
		buf.append("public class Engine {\n"); //$NON-NLS-1$
		buf.append("  public void start() {}\n"); //$NON-NLS-1$
		if (isEnabled(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES) && isEnabled(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES_ALL)) {
			buf.append("\n"); //$NON-NLS-1$
		} else {
			buf.append("    \n"); //$NON-NLS-1$
		}
		if (isEnabled(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES)) {
			buf.append("    public\n"); //$NON-NLS-1$
		} else {
			buf.append("    public \n"); //$NON-NLS-1$
		}
		buf.append("        void stop() {\n"); //$NON-NLS-1$
		buf.append("    }\n"); //$NON-NLS-1$
		buf.append("}\n"); //$NON-NLS-1$

		return buf.toString();
	}
}
