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

package com.liferay.ide.project.core.modules.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentLoginPreActionOperation extends BaseNewLiferayComponentOperation
{

    private static final String TEMPLATE_FILE = "loginpreaction.ftl";

    private final static String SUPER_CLASS = "LifecycleAction";
    private final static String EXTENSION_CLASS = "LifecycleAction.class";

    private final static String[] PROPERTIES_LIST = new String[] { "key=login.events.pre" };

    public NewLiferayComponentLoginPreActionOperation()
    {
        super();
    }

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.addAll( super.getImports() );
        imports.add( "com.liferay.portal.kernel.exception.PortalException" );
        imports.add( "com.liferay.portal.kernel.log.Log" );
        imports.add( "com.liferay.portal.kernel.log.LogFactoryUtil" );
        imports.add( "com.liferay.portal.kernel.security.auth.AuthException" );
        imports.add( "com.liferay.portal.kernel.security.auth.AuthFailure" );
        imports.add( "java.util.Map" );

        return imports;
    }

    @Override
    protected List<String> getProperties()
    {
        List<String> properties = new ArrayList<String>();
        properties.addAll( Arrays.asList( PROPERTIES_LIST ) );

        for( String property : super.getProperties() )
        {
            properties.add( property );
        }
        return properties;
    }

    @Override
    protected String getExtensionClass()
    {
        return EXTENSION_CLASS;
    }

    @Override
    protected String getSuperClass()
    {
        return SUPER_CLASS;
    }

    @Override
    protected String getTemplateFile()
    {
        return TEMPLATE_FILE;
    }
}
