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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaPackageName;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
@Service( impl = IJavaProjectConversionService.class )
public interface NewLiferayComponentOp extends ExecutableElement
{

    ElementType TYPE = new ElementType( NewLiferayComponentOp.class );

    @Label( standard = "project name" )
    @Required
    @Service( impl = NewLiferayComponentProjectNameDefaultValueService.class )
    @Service( impl = NewLiferayComponentProjectNamePossibleService.class )
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );

    Value<String> getProjectName();
    void setProjectName( String value );

    // *** Package ***

    @Type( base = JavaPackageName.class )
    @Required
    ValueProperty PROP_PACKAGE = new ValueProperty( TYPE, "Package" );

    Value<JavaPackageName> getPackage();
    void setPackage( String value );
    void setPackage( JavaPackageName value );

    // *** Component Class Template Name ***

    @DefaultValue( text = "Portlet" )
    @Label( standard = "Component Class Template" )
    @PossibleValues
    (
        values =
        {
            "Authenticator",
            "Auth Failure",
            "Auth Max Failures",
            "Configuration Action",
            "Friendly URL Mapper",
            "GOGO Command",
            "Indexer Post Processor",
            "Login Pre Action",
            "Model Listener", // need to allow user to select which model class
            "Poller Processor",
            "Portlet",
            "Portlet Action Command",
            "Portlet Filter",
            "Rest",
            "Service Wrapper",
            "Struts Action",
            "Struts Portlet Action"
        }
    )
    ValueProperty PROP_COMPONENT_CLASS_TEMPLATE_NAME = new ValueProperty( TYPE, "ComponentClassTemplateName" );

    Value<String> getComponentClassTemplateName();
    void setComponentClassTemplateName( String value );

    // *** ComponentName ***
    @Label( standard = "Component Name" )
    @Services
    (
        {
            @Service( impl = NewLiferayComponentDefaultValueService.class ),
            @Service( impl = NewLiferayComponentValidationService.class )
        }
    )
    ValueProperty PROP_COMPONENT_NAME = new ValueProperty( TYPE, "ComponentName" );

    Value<String> getComponentName();

    void setComponentName( String value );

    // *** ServiceName ***
    @Label( standard = "Service Name" )
    @Required
    @Services
    (
        {
            @Service( impl = NewLiferayComponentServicePossibleValuesService.class ),
            @Service( impl = NewLiferayComponentServiceNameValidataionService.class )
        }
    )
    ValueProperty PROP_SERVICE_NAME = new ValueProperty( TYPE, "ServiceName" );

    Value<String> getServiceName();
    void setServiceName( String value );

    // *** PackageeName ***

    @Label( standard = "Package Name" )
    @Services
    (
        {
            @Service( impl = NewLiferayComponentPackageNameValidationService.class ),
            @Service( impl = PackageNameDefaultValueService.class )
        }
    )
    ValueProperty PROP_PACKAGE_NAME = new ValueProperty( TYPE, "PackageName" );

    Value<String> getPackageName();
    void setPackageName( String value );

    // *** PropertyKeys ***
    @Type( base = PropertyKey.class )
    @Label( standard = "Properties" )
    ListProperty PROP_PROPERTYKEYS = new ListProperty( TYPE, "PropertyKeys" );

    ElementList<PropertyKey> getPropertyKeys();

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewLiferayComponentOpMethods.class )
    Status execute( ProgressMonitor monitor );
}