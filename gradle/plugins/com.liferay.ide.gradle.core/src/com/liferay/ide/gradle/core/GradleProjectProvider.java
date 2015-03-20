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
import com.liferay.ide.core.ILiferayProjectProvider;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.gradle.tooling.model.DomainObjectSet;
import org.gradle.tooling.model.eclipse.EclipseProject;
import org.gradle.tooling.model.eclipse.EclipseProjectDependency;
import org.springsource.ide.eclipse.gradle.core.ClassPath;
import org.springsource.ide.eclipse.gradle.core.GradleCore;
import org.springsource.ide.eclipse.gradle.core.GradleNature;
import org.springsource.ide.eclipse.gradle.core.GradleProject;
import org.springsource.ide.eclipse.gradle.core.classpathcontainer.FastOperationFailedException;


/**
 * @author Gregory Amerson
 */
public class GradleProjectProvider extends AbstractLiferayProjectProvider implements ILiferayProjectProvider
{

    public GradleProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    @Override
    public ILiferayProject provide( Object adaptable )
    {
        ILiferayProject retval = null;

        if( adaptable instanceof IProject )
        {
            final IProject project = (IProject) adaptable;

            if( GradleNature.hasNature( project ) )
            {
                final GradleProject gradleProject = GradleCore.create( project );

                if( hasGradleBundlePlugin( gradleProject ) )
                {
                    return new GradleBundlePluginProject( project );
                }
                else if( hasGradleBndPlugin( gradleProject ) )
                {
                    return new GradleBndPluginProject( project );
                }
            }
        }

        return retval;
    }

    private boolean hasGradleBndPlugin( GradleProject gradleProject )
    {


        return false;
    }

    private boolean hasGradleBundlePlugin( GradleProject gradleProject )
    {
        try
        {
            ClassPath cp = gradleProject.getClassPath();
            for( IClasspathEntry cpe : cp.getLibraryEntries() )
            {
                System.out.println(cpe);
            }
            EclipseProject eclipseProject = gradleProject.requestGradleModel();
            DomainObjectSet<? extends EclipseProjectDependency> projectDeps = eclipseProject.getProjectDependencies();
            List<? extends EclipseProjectDependency> allDeps = projectDeps.getAll();

            for( EclipseProjectDependency dep : allDeps )
            {
                System.out.println(dep);
            }
        }
        catch( FastOperationFailedException | CoreException e )
        {
            e.printStackTrace();
        }

        return false;
    }

}
