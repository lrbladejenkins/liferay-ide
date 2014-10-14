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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.internal.IMemento;
import org.eclipse.wst.server.core.internal.XMLMemento;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class SDKCorePlugin extends Plugin
{

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.sdk.core"; //$NON-NLS-1$
    public static final String PREFERENCE_ID = "com.liferay.ide.eclipse.sdk"; //$NON-NLS-1$

    public static final String PREF_KEY_OVERWRITE_USER_BUILD_FILE = "OVERWRITE_USER_BUILD_FILE"; //$NON-NLS-1$
    public static final String PREF_KEY_SDK_NAME = "sdk-name"; //$NON-NLS-1$

    // The shared instance
    private static SDKCorePlugin plugin;

    public static IStatus createErrorStatus( String msg )
    {
        return LiferayCore.createErrorStatus( PLUGIN_ID, msg );
    }

    public static IStatus createErrorStatus( Throwable t )
    {
        return LiferayCore.createErrorStatus( PLUGIN_ID, t );
    }

    public static IStatus createErrorStatus( String pluginId, String msg, Throwable e )
    {
        return new Status( IStatus.ERROR, pluginId, msg, e );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static SDKCorePlugin getDefault()
    {
        return plugin;
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( new Status( IStatus.ERROR, PLUGIN_ID, e.getMessage(), e ) );
    }

    public static void logError( String msg, Throwable t )
    {
        getDefault().getLog().log( createErrorStatus( PLUGIN_ID, msg, t ) );
    }

    private ISDKListener sdkListener;

    /**
     * The constructor
     */
    public SDKCorePlugin()
    {
    }

    private synchronized void saveGlobalSDKSettings( SDK[] sdks )
    {
        try
        {
            LiferayCore.GLOBAL_SETTINGS_PATH.toFile().mkdirs();

            final XMLMemento sdkMementos = XMLMemento.createWriteRoot( "sdks" );

            for( SDK sdk : sdks )
            {
                final IMemento memento = sdkMementos.createChild( "sdk" );

                addSDKToMemento( sdk, memento );
            }

            final FileOutputStream fos =
                new FileOutputStream( LiferayCore.GLOBAL_SETTINGS_PATH.append( "sdks.xml" ).toFile() );

            sdkMementos.save( fos );
        }
        catch( Exception e )
        {
            logError( "Unable to save global sdk settings", e );
        }
    }

    private void addSDKToMemento( SDK sdk, IMemento memento )
    {
        memento.putString( "name", sdk.getName() );
        memento.putString( "location", sdk.getLocation().toOSString() );
        memento.putBoolean( "default", sdk.isDefault() );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;

        this.sdkListener = new ISDKListener()
        {
            public void sdksRemoved( SDK[] sdks )
            {
                saveGlobalSDKSettings( sdks );
            }

            public void sdksChanged( SDK[] sdks )
            {
                saveGlobalSDKSettings( sdks );
            }

            public void sdksAdded( SDK[] sdks )
            {
                saveGlobalSDKSettings( sdks );
            }
        };

        SDKManager.getInstance().addSDKListener( this.sdkListener );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop( BundleContext context ) throws Exception
    {
        // delete tmp folder
        File createDir = getDefault().getStateLocation().append( "create" ).toFile(); //$NON-NLS-1$

        if( createDir.exists() )
        {
            FileUtil.deleteDir( createDir, true );
        }

        plugin = null;
        super.stop( context );
    }

}
