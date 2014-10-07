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
package com.liferay.ide.alloy.core.webresources;

import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.WebResourceKind;
import org.eclipse.wst.html.webresources.core.validation.IWebResourcesValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class JSPWebResourcesValidator implements IWebResourcesValidator
{

    @Override
    public boolean ignore( Object resource, WebResourceKind resourceKind, IWebResourcesContext context )
    {
        if( context != null )
        {
            final IDOMNode node = context.getHtmlNode();

            if( node instanceof IDOMAttr )
            {
                final IDOMAttr attr = (IDOMAttr) node;
                final String attrName = attr.getName();

                if( attrName != null &&
                    ( attrName.equalsIgnoreCase( "href" ) || attrName.equalsIgnoreCase( "src" ) ) )
                {
                    final String val = attr.getValue();

                    if( val != null && ( val.contains( "<%" ) || val.contains( "%>" ) ) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
