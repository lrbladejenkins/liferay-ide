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
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.upgrade.NamedItem;
import com.liferay.ide.project.core.upgrade.UpgradeLiferayProjectsOp;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;
import org.osgi.framework.Version;


/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class ProjectItemCheckboxCustomPart extends AbstractCheckboxCustomPart
{

    class ProjectItemUpgradeLabelProvider extends ElementLabelProvider implements IColorProvider, IStyledLabelProvider
    {
        private static final String GREY_COLOR = "actual portal version"; //$NON-NLS-1$
        private final ColorRegistry COLOR_REGISTRY = JFaceResources.getColorRegistry();
        private final Styler GREYED_STYLER;

        public ProjectItemUpgradeLabelProvider()
        {
            COLOR_REGISTRY.put( GREY_COLOR, new RGB( 128, 128, 128 ) );
            GREYED_STYLER = StyledString.createColorRegistryStyler( GREY_COLOR, null );
        }

        @Override
        public Image getImage( Object element )
        {
            if( element instanceof CheckboxElement )
            {
                final String projectName = ( (CheckboxElement) element ).name;
                final IProject project = ProjectUtil.getProject( projectName );

                if ( project != null)
                {
                    String suffix = ProjectUtil.getLiferayPluginType( project.getLocation().toOSString() );
                    return this.getImageRegistry().get( suffix );
                }
            }

            return null;
        }

        @Override
        public StyledString getStyledText( Object element )
        {
            if( element instanceof CheckboxElement )
            {
                final String srcLableString = ( (CheckboxElement) element ).context;
                final String projectName = srcLableString.substring( 0, srcLableString.lastIndexOf( "[" ) );
                final StyledString styled = new StyledString(projectName);
                return StyledCellLabelProvider.styleDecoratedString( srcLableString, GREYED_STYLER, styled);
            }

            return new StyledString( ( ( CheckboxElement ) element ).context );
        }

        @Override
        protected void initalizeImageRegistry( ImageRegistry imageRegistry )
        {
            imageRegistry.put( PluginType.portlet.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/portlet.png" ) );
            imageRegistry.put( PluginType.hook.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/hook.png" ) );
            imageRegistry.put( PluginType.layouttpl.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/layout.png" ) );
            imageRegistry.put( PluginType.servicebuilder.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/portlet.png" ) );
            imageRegistry.put( PluginType.ext.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/ext.png" ) );
            imageRegistry.put( PluginType.theme.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/theme.png" ) );
            imageRegistry.put( PluginType.web.name(),
                ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/web.png" ) );
        }

    }


    @Override
    protected ElementList<NamedItem> getSelectedElements()
    {
        return op().getSelectedProjects();
    }

    @Override
    protected List<CheckboxElement> getInitItemsList()
    {
        List<CheckboxElement> checkboxElementList = new ArrayList<CheckboxElement>();
        IProject[] projects = ProjectUtil.getAllPluginsSDKProjects();
        String  context = null;

        for( IProject project : projects )
        {
            IFacetedProject facetedProject = ProjectUtil.getFacetedProject( project );

            ILiferayRuntime liferayRuntime =
                ServerUtil.getLiferayRuntime( (BridgedRuntime) facetedProject.getPrimaryRuntime() );

            if( liferayRuntime != null )
            {
                context =  project.getName() + " [Liferay Portal version: " + liferayRuntime.getPortalVersion() + "]";
            }

            CheckboxElement checkboxElement = new CheckboxElement( project.getName(), context );
            checkboxElementList.add( checkboxElement );
        }

        return checkboxElementList;
    }

    @Override
    protected void updateValidation()
    {
        retval = Status.createOkStatus();

        if( op().getSelectedProjects().size() < 1 )
        {
            retval = Status.createErrorStatus( "At least one project must be specified " );
        }
        else
        {
            final ElementList<NamedItem> projectItems = op().getSelectedProjects();

            for( NamedItem projectItem : projectItems )
            {
                if( projectItem.getName().content() != null )
                {
                    final IProject project = ProjectUtil.getProject( projectItem.getName().content().toString() );
                    final ILiferayProject lProject = LiferayCore.create( project );

                    if( lProject != null )
                    {
                        final ILiferayPortal portal = lProject.adapt( ILiferayPortal.class );

                        if( portal != null )
                        {
                            final String portalVersion = portal.getVersion();

                            if( portalVersion != null )
                            {
                                final Version version = new Version( portalVersion );

                                if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
                                {
                                    retval =
                                        Status.createErrorStatus( "Portal version of " + project.getName() +
                                            " is greater than " + ILiferayConstants.V620 );
                                }
                            }
                        }
                    }
                }
            }
        }

        refreshValidation();
    }

    @Override
    protected ElementList<NamedItem> getCheckboxList()
    {
        return op().getSelectedProjects();
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new ProjectItemUpgradeLabelProvider();
    }

    private UpgradeLiferayProjectsOp op()
    {
        return getLocalModelElement().nearest( UpgradeLiferayProjectsOp.class );
    }
}
