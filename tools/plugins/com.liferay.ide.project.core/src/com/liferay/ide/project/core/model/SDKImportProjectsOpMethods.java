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
package com.liferay.ide.project.core.model;

import com.liferay.ide.core.util.MultiStatusBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.upgrade.NamedItem;
import com.liferay.ide.project.core.util.ProjectImportUtil;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;


/**
 * @author Simon Jiang
 */
public class SDKImportProjectsOpMethods
{
    public static final Status execute( final SDKProjectsImportOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Importing Liferay plugin projects...", 100 );

        Status retval = Status.createOkStatus();

        final Path projectLocation = op.getSdkLocation().content();

        if( projectLocation == null || projectLocation.isEmpty() )
        {
            return Status.createErrorStatus( "Project can not be empty" );
        }

        final Job job = new WorkspaceJob( "Import Liferay Projects..." )
        {
            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                final MultiStatusBuilder statusBuilder = new MultiStatusBuilder(ProjectCore.PLUGIN_ID);
                ElementList<NamedItem>  projectItems = op.getSelectedProjects();

                for( NamedItem namedItem : projectItems )
                {
                    try
                    {
                        final int startPos = namedItem.getLocation().content().indexOf( "(" );
                        final int endPos = namedItem.getLocation().content().indexOf( ")" );
                        final String projectPath = namedItem.getLocation().content().substring( startPos + 1, endPos );
                        final String projectLocation = new Path(projectPath).toPortableString();
                        ProjectImportUtil.importProject( PathBridge.create( new Path(projectLocation) ), new NullProgressMonitor(), null);
                    }
                    catch(Exception e)
                    {
                        statusBuilder.add( StatusBridge.create( Status.createErrorStatus( e.getMessage() ) ) );
                    }
                }

                return statusBuilder.retval();
            }
        };

        job.schedule();

        return retval;
    }
}
