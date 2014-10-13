/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.server.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.wst.server.core.internal.IStartup;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class ServerStartup implements IStartup
{

    private static final String GLOBAL_SETTINGS_CHECKED = "global-settings-checked";

    public void startup()
    {
        if( shouldCheckForGlobalSettings() )
        {
            final UIJob job = new UIJob( "Checking for saved liferay info" )
            {
                @Override
                public IStatus runInUIThread( IProgressMonitor monitor )
                {
                    final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                    if( window != null )
                    {
                        try
                        {
                            IEclipsePreferences prefs =
                                InstanceScope.INSTANCE.getNode( LiferayServerUIPlugin.PLUGIN_ID );
                            prefs.putBoolean( GLOBAL_SETTINGS_CHECKED, true );
                            prefs.flush();
                        }
                        catch( BackingStoreException e )
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        this.schedule( 1000 );
                    }

                    return Status.OK_STATUS;
                }
            };

            job.schedule( 10000 );
        }

    }

    private boolean shouldCheckForGlobalSettings()
    {
        IScopeContext[] scopes = new IScopeContext[] { InstanceScope.INSTANCE };

        return !( Platform.getPreferencesService().getBoolean(
            LiferayServerUIPlugin.PLUGIN_ID, GLOBAL_SETTINGS_CHECKED, false, scopes ) );
    }

}
