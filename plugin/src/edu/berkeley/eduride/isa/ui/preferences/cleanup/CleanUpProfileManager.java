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
package edu.berkeley.eduride.isa.ui.preferences.cleanup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;


import org.eclipse.jdt.ui.JavaUI;

import edu.berkeley.eduride.isa.corext.fix.CleanUpConstants;
import edu.berkeley.eduride.isa.ui.JavaPlugin;
import edu.berkeley.eduride.isa.ui.preferences.PreferencesAccess;
import edu.berkeley.eduride.isa.ui.preferences.formatter.IProfileVersioner;
import edu.berkeley.eduride.isa.ui.preferences.formatter.ProfileManager;

public class CleanUpProfileManager extends ProfileManager {

	public static KeySet[] KEY_SETS= {
		new KeySet(JavaUI.ID_PLUGIN, new ArrayList(JavaPlugin.getDefault().getCleanUpRegistry().getDefaultOptions(CleanUpConstants.DEFAULT_CLEAN_UP_OPTIONS).getKeys()))
	};

	private final PreferencesAccess fPreferencesAccess;

	public CleanUpProfileManager(List profiles, IScopeContext context, PreferencesAccess preferencesAccess, IProfileVersioner profileVersioner) {
	    super(profiles, context, preferencesAccess, profileVersioner, KEY_SETS, CleanUpConstants.CLEANUP_PROFILE, CleanUpConstants.CLEANUP_SETTINGS_VERSION_KEY);
		fPreferencesAccess= preferencesAccess;
    }

	/* (non-Javadoc)
     * @see edu.berkeley.eduride.isa.ui.preferences.cleanup.ProfileManager#getDefaultProfile()
     */
    public Profile getDefaultProfile() {
    	return getProfile(CleanUpConstants.DEFAULT_PROFILE);
    }

    /**
     * {@inheritDoc}
     */
    protected void updateProfilesWithName(String oldName, Profile newProfile, boolean applySettings) {
        super.updateProfilesWithName(oldName, newProfile, applySettings);

        IEclipsePreferences node= fPreferencesAccess.getInstanceScope().getNode(JavaUI.ID_PLUGIN);
        String name= node.get(CleanUpConstants.CLEANUP_ON_SAVE_PROFILE, null);
        if (name != null && name.equals(oldName)) {
        	if (newProfile == null) {
        		node.remove(CleanUpConstants.CLEANUP_ON_SAVE_PROFILE);
        	} else {
    			node.put(CleanUpConstants.CLEANUP_ON_SAVE_PROFILE, newProfile.getID());
        	}
    	}
    }

}
