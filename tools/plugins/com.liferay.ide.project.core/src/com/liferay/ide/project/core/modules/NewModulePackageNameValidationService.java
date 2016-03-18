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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class NewModulePackageNameValidationService extends ValidationService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewModuleOp.PROP_COMPONENT_NAME ).attach( this.listener );
    }

    @Override
    protected Status compute()
    {
        final String packageName = op().getPackageName().content( true );
        Status retval = Status.createOkStatus();

        int packageNameStatus = IStatus.OK;

        if( !CoreUtil.isNullOrEmpty( packageName ) )
        {
            packageNameStatus = JavaConventions.validatePackageName(
                packageName, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5 ).getSeverity();

            if( packageNameStatus == IStatus.ERROR )
            {
                retval = Status.createErrorStatus( "Invalid package name" );
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        if( this.listener != null )
        {
            op().property( NewModuleOp.PROP_COMPONENT_NAME ).detach( this.listener );

            this.listener = null;
        }
        super.dispose();
    }

    private NewModuleOp op()
    {
        return context( NewModuleOp.class );
    }
}