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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "rawtypes" )
public class MigrationAdapterFactory implements IAdapterFactory, IWorkbenchAdapter
{
    private static final Object instance = new MigrationAdapterFactory();

    @Override
    public Object getAdapter( Object adaptableObject, Class adapterType )
    {
        return instance;
    }

    @Override
    public Class[] getAdapterList()
    {
        return new Class[] { MXMNode.class, MigrationTask.class };
    }

    @Override
    public Object[] getChildren( Object o )
    {
        return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor( Object element )
    {
        if( element instanceof MigrationTask )
        {
            return ImageDescriptor.createFromImage( PlatformUI.getWorkbench().getSharedImages().getImage(
                ISharedImages.IMG_OBJ_FOLDER ) );
        }
        else if( element instanceof MXMNode )
        {
            return ImageDescriptor.createFromImage( PlatformUI.getWorkbench().getSharedImages().getImage(
                ISharedImages.IMG_OBJ_FOLDER ) );
        }

        return null;
    }

    @Override
    public String getLabel( Object element )
    {
        if( element instanceof MigrationTask )
        {
            return "Migration Task";
        }
        else if( element instanceof MXMNode )
        {
            MXMNode node = (MXMNode) element;

            String label = node.data;

            if( label.startsWith( "/" ) )
            {
                label = label.substring( 1 );
            }

            return label;
        }

        return null;
    }

    @Override
    public Object getParent( Object o )
    {
        return null;
    }


}
