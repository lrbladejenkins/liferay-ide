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

package com.liferay.ide.project.ui.migration;

import blade.migrate.api.Problem;

import com.liferay.ide.core.util.CoreUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Gregory Amerson
 */
public class MigrationContentProvider implements ITreeContentProvider
{

    MigrationTask[] _tasks;
    Map<MigrationTask, MXMTree> _fileTrees;
    Map<IFile, List<Problem>> _problemsMap;

    @Override
    public void dispose()
    {
    }

    @Override
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        if( newInput instanceof List<?> )
        {
            List<?> tasks = (List<?>) newInput;

            _tasks = tasks.toArray( new MigrationTask[0] );
        }
        else if( newInput instanceof MigrationTask[] )
        {
            _tasks = (MigrationTask[]) newInput;
        }

        _problemsMap = new HashMap<>();
        _fileTrees = getFileTrees( _tasks );
    }

    private Map<MigrationTask, MXMTree> getFileTrees( MigrationTask[] tasks )
    {
        final Map<MigrationTask, MXMTree> retval = new HashMap<>();

        for( MigrationTask task : tasks )
        {
            final MXMTree tree = new MXMTree( new MXMNode( "", "" ) );

            for( Problem problem : task.getProblems() )
            {
                final IFile[] files =
                    CoreUtil.getWorkspace().getRoot().findFilesForLocationURI( problem.file.toURI() );

                for( IFile file : files )
                {
                    List<Problem> fileProblems = _problemsMap.get( file );

                    if( fileProblems == null )
                    {
                        fileProblems = new ArrayList<>();
                        _problemsMap.put( file, fileProblems );

                        tree.addElement( file.getFullPath().toPortableString() );
                    }

                    fileProblems.add( problem );
                }
            }

            retval.put( task, tree );
        }

        return retval;
    }

    @Override
    public Object[] getElements( Object inputElement )
    {
        return _tasks;
    }

    @Override
    public Object[] getChildren( Object parentElement )
    {
        if( parentElement instanceof MigrationTask )
        {
            final MigrationTask task = (MigrationTask) parentElement;

            final MXMNode commonRoot = _fileTrees.get( task ).getCommonRoot();
            commonRoot.data = commonRoot.incrementalPath;

            return new Object[] { commonRoot };
        }
        else if( parentElement instanceof MXMNode )
        {
            MXMNode node = (MXMNode) parentElement;

            if( node.isLeaf() )
            {
                return new Object[] { CoreUtil.getWorkspace().getRoot().getFile( new Path( node.incrementalPath ) ) };
            }
            else
            {
                final List<Object> children = new ArrayList<>();

                children.addAll( node.childs );

                for( MXMNode leaf : node.leafs )
                {
                    children.add( CoreUtil.getWorkspace().getRoot().getFile( new Path( leaf.incrementalPath ) ) );
                }

                return children.toArray();
            }
        }

        return null;
    }

    @Override
    public Object getParent( Object element )
    {
        if( element instanceof TaskProblem )
        {
            final TaskProblem problem = (TaskProblem) element;

            return problem.getParent();
        }

        return null;
    }

    @Override
    public boolean hasChildren( Object element )
    {
        if( element instanceof MigrationTask )
        {
            return true;
        }
        else if( element instanceof MXMNode )
        {
            MXMNode node = (MXMNode) element;

            return node.childs.size() > 0 || node.leafs.size() > 0;
        }

        return false;
    }

}
