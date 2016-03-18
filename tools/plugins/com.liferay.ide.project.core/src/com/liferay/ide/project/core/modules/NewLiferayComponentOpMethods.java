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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.template.NewLiferayComponentActivatorOperation;
import com.liferay.ide.project.core.modules.template.NewLiferayComponentMvcPortletOperation;
import com.liferay.ide.project.core.modules.template.NewLiferayComponentPortletOperation;
import com.liferay.ide.project.core.modules.template.NewLiferayComponentServiceOperation;
import com.liferay.ide.project.core.modules.template.NewLiferayComponentServiceWrapperOperation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentOpMethods
{

    public static void createNewModule( NewLiferayComponentOp op, IProgressMonitor monitor ) throws CoreException
    {
        ILiferayModuleOperation<NewLiferayComponentOp> moduleOperation = null;

        final String templateName = op.getComponentTemplateName().content( true );

        if( templateName.equals( "mvcportlet" ) )
        {
            moduleOperation = new NewLiferayComponentMvcPortletOperation( op );
        }
        else if( templateName.equals( "portlet" ) )
        {
            moduleOperation = new NewLiferayComponentPortletOperation( op );
        }
        else if( templateName.equals( "service" ) )
        {
            moduleOperation = new NewLiferayComponentServiceOperation( op );
        }
        else if( templateName.equals( "servicewrapper" )  )
        {
            moduleOperation = new NewLiferayComponentServiceWrapperOperation( op );
        }
        else if( templateName.equals( "activator" ) )
        {
            moduleOperation = new NewLiferayComponentActivatorOperation( op );
        }

        if( moduleOperation != null )
        {
            moduleOperation.doExecute();
        }
    }

    public static final Status execute( final NewLiferayComponentOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating new Liferay component", 100 );

        Status retval = Status.createOkStatus();

        try
        {
            String projectName = op.getProjectName().content( true );
            IProject project = CoreUtil.getProject( projectName );

            if ( project != null )
            {
                createNewModule( op, monitor );
            }
            else
            {
                return Status.createErrorStatus( " Can't find valid project." );
            }
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay component.";
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }
}