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

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentDefaultValueService extends DefaultValueService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayComponentOp.PROP_PROJECT_NAME ).attach( this.listener );
        op().property( NewLiferayComponentOp.PROP_COMPONENT_TEMPLATE_NAME ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        String retVal = "";

        final String projectName = op().getProjectName().content( true );

        if( projectName == null )
        {
            return retVal;
        }

        final String projectTemplate = op().getComponentTemplateName().content( true );

        final String pName = projectName.replaceAll( "-", "" );

        final String finalProjectName = pName.replaceAll( "\\.", "" );

        final StringBuffer componentNameBuffer = new StringBuffer( finalProjectName );

        componentNameBuffer.append( projectTemplate );

        return componentNameBuffer.toString();
    }

    private NewLiferayComponentOp op()
    {
        return context( NewLiferayComponentOp.class );
    }

    @Override
    public void dispose()
    {
        if( op() != null )
        {
            op().property( NewLiferayComponentOp.PROP_COMPONENT_TEMPLATE_NAME ).detach( this.listener );
            op().property( NewLiferayComponentOp.PROP_PROJECT_NAME ).detach( this.listener );
        }

        super.dispose();
    }
}