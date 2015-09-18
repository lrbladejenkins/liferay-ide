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

package com.liferay.ide.project.core.upgrade;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;

/**
 * @author Simon Jiang
 */
public interface NamedItem extends Element
{

    ElementType TYPE = new ElementType( NamedItem.class );

    // *** Name ***

    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

    Value<String> getName();

    void setName( String value );

    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );

    Value<String> getLocation();

    void setLocation( String value );
}
