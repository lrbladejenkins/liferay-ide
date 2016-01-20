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
import com.liferay.ide.gradle.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Andy Wu
 */
public class LiferayWorkspaceProjectProvider extends AbstractLiferayProjectProvider
    implements NewLiferayProjectProvider<NewLiferayWorkspaceOp>
{

    public LiferayWorkspaceProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public IStatus createNewProject( NewLiferayWorkspaceOp op, IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        IPath location = PathBridge.create( op.getLocation().content() );

        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );
        sb.append( "\"" + location.toFile().getAbsolutePath() + "\" " );
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

    public void importProject(String location , IProgressMonitor monitor , String extraOperation )
    {
        try
        {
            Job importJob =  GradleUtil.importGradleProject( new File(location) , monitor );

            importJob.join();

            if( !CoreUtil.empty( extraOperation ) )
            {
                IPath path = new Path(location);

                path.lastSegment();

                IProject project = CoreUtil.getProject( path.lastSegment() );

                GradleUtil.runGradleTask( project, extraOperation, monitor );

                project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
            }

        }
        catch( CoreException e )
        {
            GradleCore.logError( "import Liferay Workspace project error" , e );
        }
        catch( InterruptedException e )
        {
        }
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
