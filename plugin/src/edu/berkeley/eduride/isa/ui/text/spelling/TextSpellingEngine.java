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

package edu.berkeley.eduride.isa.ui.text.spelling;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;

import edu.berkeley.eduride.isa.ui.text.spelling.engine.ISpellChecker;

/**
 * Text spelling engine
 *
 * @since 3.1
 */
public class TextSpellingEngine extends SpellingEngine {

	/*
	 * @see edu.berkeley.eduride.isa.ui.text.spelling.SpellingEngine#check(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IRegion[], edu.berkeley.eduride.isa.ui.text.spelling.engine.ISpellChecker, org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void check(IDocument document, IRegion[] regions, ISpellChecker checker, ISpellingProblemCollector collector, IProgressMonitor monitor) {
		SpellEventListener listener= new SpellEventListener(collector, document);
		for (int i= 0; i < regions.length; i++) {
			if (monitor != null && monitor.isCanceled())
				return;
			if (listener.isProblemsThresholdReached())
				return;
			checker.execute(listener, new SpellCheckIterator(document, regions[i], checker.getLocale()));
		}
	}
}
