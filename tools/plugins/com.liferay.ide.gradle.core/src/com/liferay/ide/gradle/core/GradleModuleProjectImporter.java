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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectImporter;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Andy Wu
 */
public class GradleModuleProjectImporter extends AbstractLiferayProjectImporter
{

    @Override
    public IStatus canImport( String location )
    {
        IStatus retval = null;

        File file = new File( location );

        if( findGradleFile( file ) && findSettingsFile( file ) )
        {
            retval = Status.OK_STATUS;
        }
        else
        {
            File parent = file.getParentFile();

            while( parent != null )
            {
                if( findGradleFile( parent ) && findSettingsFile( file ) )
                {
                    retval = Status.OK_STATUS;
                    break;
                }

                parent = parent.getParentFile();
            }
        }

        if( retval == null )
        {
            retval = new Status(
                IStatus.ERROR, GradleCore.PLUGIN_ID,
                "Location is not the root location of a multi-module project." );
        }

        return retval;

    }

    private boolean findFile( File dir, String name )
    {
        boolean retval = false;

        if( dir.exists() )
        {
            File[] files = dir.listFiles();

            for( File file : files )
            {
                if( !file.isDirectory() && file.getName().equals( name ) )
                {
                    retval = true;
                }
            }
        }

        return retval;
    }

    private boolean findGradleFile( File dir )
    {
        return findFile( dir, "build.gradle" );
    }

    private boolean findSettingsFile( File dir )
    {
        return findFile( dir, "settings.gradle" );
    }

    @Override
    public void importProject( String location, IProgressMonitor monitor ) throws CoreException
    {
        File projectLocation = new File( location );

        GradleUtil.importGradleProject( projectLocation, monitor );

        ModuleCoreUtil.addFacetsIfNeeded( projectLocation, monitor );
    }

}
