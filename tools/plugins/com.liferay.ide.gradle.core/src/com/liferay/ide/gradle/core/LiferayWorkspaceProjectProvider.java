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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Andy Wu
 * @author Terry Jia
 */
public class LiferayWorkspaceProjectProvider extends AbstractLiferayProjectProvider
    implements NewLiferayProjectProvider<NewLiferayWorkspaceOp>
{

    public static final String defaultBundleUrl =
                    "https://sourceforge.net/projects/lportal/files/Liferay%20Portal/7.0.1%20GA2/liferay-ce-portal-tomcat-7.0-ga2-20160610113014153.zip";

    public LiferayWorkspaceProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public IStatus createNewProject( NewLiferayWorkspaceOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        IPath location = PathBridge.create( op.getLocation().content() );
        String wsName = op.getWorkspaceName().toString();

        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );

        if( op.getUseDefaultLocation().content() )
        {
            sb.append( "\"" + location.toFile().getAbsolutePath() + "\" " );
        }
        else
        {
            sb.append( "\"" + location.append( wsName ).toFile().getAbsolutePath() + "\" " );
        }

        sb.append( "init" );

        try
        {
            BladeCLI.execute( sb.toString() );
        }
        catch( BladeCLIException e )
        {
            retval = ProjectCore.createErrorStatus( e );
        }

        return retval;
    }

    public IStatus importProject( String location, IProgressMonitor monitor, String extraOperation, String bundleUrl )
    {
        try
        {
            final IStatus importJob =  GradleUtil.importGradleProject( new File(location) , monitor );

            if( !importJob.isOK() || importJob.getException() != null )
            {
                return importJob;
            }

            if( !CoreUtil.empty( extraOperation ) )
            {
                IPath path = new Path(location);

                path.lastSegment();

                IProject project = CoreUtil.getProject( path.lastSegment() );

                if( !bundleUrl.equals( defaultBundleUrl ) )
                {
                    final File gradlePropertiesFile = project.getFile( "gradle.properties" ).getLocation().toFile();

                    try(InputStream in = new FileInputStream( gradlePropertiesFile );
                                    OutputStream out = new FileOutputStream( gradlePropertiesFile ))
                    {
                        final Properties properties = new Properties();
                        properties.load( in );

                        properties.put( "liferay.workspace.bundle.url", bundleUrl );

                        properties.store( out, "" );
                    }
                    catch( IOException e )
                    {
                    }
                }

                GradleUtil.runGradleTask( project, extraOperation, monitor );

                project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
            }

        }
        catch( CoreException e )
        {
            return GradleCore.createErrorStatus( "import Liferay workspace project error" , e );
        }

        return Status.OK_STATUS;
    }

    @Override
    public synchronized ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            if( LiferayWorkspaceUtil.isValidWorkspace( project ) )
            {
                return new LiferayWorkspaceProject( project );
            }
        }

        return retval;
    }

    @Override
    public IStatus validateProjectLocation( String projectName, IPath path )
    {
        IStatus retval = Status.OK_STATUS;

        // TODO validation gradle project location

        return retval;
    }
}
