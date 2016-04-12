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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.ISDKProjectsImportDataModelProperties;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.ui.util.SWTUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "restriction", "unchecked", "rawtypes" } )
public class SDKProjectsImportWizardPage extends DataModelFacetCreationWizardPage
    implements ISDKProjectsImportDataModelProperties
{

    protected final class ProjectLabelProvider extends LabelProvider implements IColorProvider
    {
        public Color getBackground( Object element )
        {
            return null;
        }

        public Color getForeground( Object element )
        {
            ProjectRecord projectRecord = (ProjectRecord) element;

            if( projectRecord.hasConflicts() )
            {
                return getShell().getDisplay().getSystemColor( SWT.COLOR_GRAY );
            }

            return null;
        }

        public String getText( Object element )
        {
            return ( (ProjectRecord) element ).getProjectLabel();
        }
    }

    protected Label labelProjectsList;
    protected long lastModified;
    protected String lastPath;
    protected CheckboxTreeViewer projectsList;
    protected Text sdkLocation;
    protected Text sdkVersion;
    protected Object[] selectedProjects = new ProjectRecord[0];
    protected Combo serverTargetCombo;
    protected IProject[] wsProjects;

    public SDKProjectsImportWizardPage( IDataModel model, String pageName )
    {
        super( model, pageName );

        setTitle( Msgs.importLiferayProjects );
        setDescription( Msgs.selectLiferayPluginSDK );
    }

    protected void createPluginsSDKField( Composite parent )
    {
        SelectionAdapter selectionAdapter = new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                SDKProjectsImportWizardPage.this.synchHelper.synchAllUIWithModel();
                updateProjectsList( getDataModel().getStringProperty( SDK_LOCATION ) );
                validatePage( true );
            }
        };

        new LiferaySDKField( parent, getDataModel(), selectionAdapter, LIFERAY_SDK_NAME, this.synchHelper );
    }

    protected void createProjectsList( Composite workArea )
    {
        labelProjectsList = new Label( workArea, SWT.NONE );
        labelProjectsList.setText( Msgs.importProjectsLabel );
        labelProjectsList.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 3, 1 ) );

        projectsList = new CheckboxTreeViewer( workArea, SWT.BORDER );

        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 );
        gridData.widthHint = new PixelConverter( projectsList.getControl() ).convertWidthInCharsToPixels( 25 );
        gridData.heightHint = new PixelConverter( projectsList.getControl() ).convertHeightInCharsToPixels( 10 );

        projectsList.getControl().setLayoutData( gridData );
        projectsList.setContentProvider( new ITreeContentProvider()
        {

            public void dispose()
            {
            }

            public Object[] getChildren( Object parentElement )
            {
                return null;
            }

            public Object[] getElements( Object inputElement )
            {
                return getProjectRecords();
            }

            public Object getParent( Object element )
            {
                return null;
            }

            public boolean hasChildren( Object element )
            {
                return false;
            }

            public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
            {
            }

        } );

        projectsList.setLabelProvider( createProjectsListLabelProvider() );

        projectsList.addCheckStateListener
        ( 
            new ICheckStateListener()
            {
                public void checkStateChanged( CheckStateChangedEvent event )
                {
                    handleCheckStateChangedEvent( event );
                }
            }
        );

        projectsList.setInput( this );
        projectsList.setComparator( new ViewerComparator() );

        createSelectionButtons( workArea );
    }

    protected IBaseLabelProvider createProjectsListLabelProvider()
    {
        return new ProjectLabelProvider();
    }

    protected void createSDKLocationField( Composite topComposite )
    {
        SWTUtil.createLabel( topComposite, SWT.LEAD, Msgs.liferayPluginSDKLocationLabel, 1 );

        sdkLocation = SWTUtil.createText( topComposite, 1 );
        ( (GridData) sdkLocation.getLayoutData() ).widthHint = 300;
        this.synchHelper.synchText( sdkLocation, SDK_LOCATION, null );

        SWTUtil.createLabel( topComposite, SWT.LEAD, StringPool.EMPTY, 1 );

        // Button iconFileBrowse = SWTUtil.createPushButton(topComposite, "Browse...", null);
        // iconFileBrowse.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        // iconFileBrowse.addSelectionListener(new SelectionAdapter() {
        //
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // handleFileBrowseButton(SDKProjectsImportWizardPage.this.sdkLocation);
        // }
        //
        // });
    }

    protected void createSDKVersionField( Composite topComposite )
    {
        SWTUtil.createLabel( topComposite, SWT.LEAD, Msgs.liferayPluginSDKVersionLabel, 1 );

        sdkVersion = SWTUtil.createText( topComposite, 1 );
        this.synchHelper.synchText( sdkVersion, SDK_VERSION, null );

        SWTUtil.createLabel( topComposite, StringPool.EMPTY, 1 );
    }

    /**
     * Create the selection buttons in the listComposite.
     * 
     * @param listComposite
     */
    protected void createSelectionButtons( Composite listComposite )
    {
        Composite buttonsComposite = new Composite( listComposite, SWT.NONE );

        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;

        buttonsComposite.setLayout( layout );

        buttonsComposite.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );

        Button selectAll = new Button( buttonsComposite, SWT.PUSH );
        selectAll.setText( Msgs.selectAll );
        selectAll.addSelectionListener
        ( 
            new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    handleSelectAll( e );
                }
            } 
        );

        Dialog.applyDialogFont( selectAll );

        setButtonLayoutData( selectAll );

        Button deselectAll = new Button( buttonsComposite, SWT.PUSH );
        deselectAll.setText( Msgs.deselectAll );
        deselectAll.addSelectionListener
        ( 
            new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    handleDeselectAll( e );
                }
            }
        );

        Dialog.applyDialogFont( deselectAll );

        setButtonLayoutData( deselectAll );

        Button refresh = new Button( buttonsComposite, SWT.PUSH );
        refresh.setText( Msgs.refresh );
        refresh.addSelectionListener
        ( 
            new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    handleRefresh( e );
                }
            } 
        );

        Dialog.applyDialogFont( refresh );

        setButtonLayoutData( refresh );
    }

    protected void createTargetRuntimeGroup( Composite parent )
    {
        Label label = new Label( parent, SWT.NONE );
        label.setText( Msgs.liferayTargetRuntimeLabel );
        label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

        serverTargetCombo = new Combo( parent, SWT.BORDER | SWT.READ_ONLY );
        serverTargetCombo.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

        Button newServerTargetButton = new Button( parent, SWT.NONE );
        newServerTargetButton.setText( Msgs.newButton );
        newServerTargetButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                final DataModelPropertyDescriptor[] preAdditionDescriptors =
                    model.getValidPropertyDescriptors( FACET_RUNTIME );

                boolean isOK = ServerUIUtil.showNewRuntimeWizard( getShell(), getModuleTypeID(), null, "com.liferay." ); //$NON-NLS-1$

                if( isOK )
                {
                    DataModelPropertyDescriptor[] postAdditionDescriptors =
                        model.getValidPropertyDescriptors( FACET_RUNTIME );

                    Object[] preAddition = new Object[preAdditionDescriptors.length];

                    for( int i = 0; i < preAddition.length; i++ )
                    {
                        preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
                    }

                    Object[] postAddition = new Object[postAdditionDescriptors.length];

                    for( int i = 0; i < postAddition.length; i++ )
                    {
                        postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
                    }

                    Object newAddition = CoreUtil.getNewObject( preAddition, postAddition );

                    if( newAddition != null ) // can this ever be null?
                        model.setProperty( FACET_RUNTIME, newAddition );
                }
            }
        } );

        Control[] deps = new Control[] { newServerTargetButton };

        synchHelper.synchCombo( serverTargetCombo, FACET_RUNTIME, deps );

        if( serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0 )
        {
            serverTargetCombo.select( 0 );
        }
    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite topComposite = SWTUtil.createTopComposite( parent, 3 );

        GridLayout gl = new GridLayout( 3, false );
        // gl.marginLeft = 5;
        topComposite.setLayout( gl );
        topComposite.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );

        createPluginsSDKField( topComposite );

        SWTUtil.createSeparator( topComposite, 3 );

        createSDKLocationField( topComposite );
        createSDKVersionField( topComposite );

        SWTUtil.createVerticalSpacer( topComposite, 1, 3 );

        createProjectsList( topComposite );
        createTargetRuntimeGroup( topComposite );

        return topComposite;
    }

    @Override
    protected void enter()
    {
        String sdkName = getDataModel().getStringProperty( LIFERAY_SDK_NAME );

        if( sdkName != null )
        {
            SDK initialSdk = SDKManager.getInstance().getSDK( sdkName );

            if( initialSdk != null )
            {
                updateProjectsList( initialSdk.getLocation().toOSString() );
            }
        }
    }

    public Object[] getProjectRecords()
    {
        List projectRecords = new ArrayList();

        for( int i = 0; i < selectedProjects.length; i++ )
        {
            ProjectRecord projectRecord = (ProjectRecord) selectedProjects[i];
            if( isProjectInWorkspace( projectRecord.getProjectName() ) )
            {
                projectRecord.setHasConflicts( true );
            }

            projectRecords.add( selectedProjects[i] );
        }
        
        return (ProjectRecord[]) projectRecords.toArray( new ProjectRecord[projectRecords.size()] );
    }

    protected IProject[] getProjectsInWorkspace()
    {
        if( wsProjects == null )
        {
            wsProjects = IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getProjects();
        }
        
        return wsProjects;
    }

    @Override
    protected String[] getValidationPropertyNames()
    {
        return new String[] { SDK_LOCATION, SDK_VERSION, SELECTED_PROJECTS, FACET_RUNTIME };
    }

    protected void handleCheckStateChangedEvent( CheckStateChangedEvent event )
    {
        ProjectRecord element = (ProjectRecord) event.getElement();

        if( element.hasConflicts() )
        {
            projectsList.setChecked( element, false );
        }

        getDataModel().setProperty( SELECTED_PROJECTS, projectsList.getCheckedElements() );

        setPageComplete( projectsList.getCheckedElements().length > 0 );
    }

    protected void handleDeselectAll( SelectionEvent e )
    {
        projectsList.setCheckedElements( new Object[0] );

        getDataModel().setProperty( SELECTED_PROJECTS, projectsList.getCheckedElements() );

        validatePage( true );

        setPageComplete( false );
    }

    protected void handleFileBrowseButton( final Text text )
    {
        DirectoryDialog dd = new DirectoryDialog( this.getShell(), SWT.OPEN );

        dd.setText( Msgs.selectLiferayPluginSDKFolder );

        if( !CoreUtil.isNullOrEmpty( sdkLocation.getText() ) )
        {
            dd.setFilterPath( sdkLocation.getText() );
        }

        String dir = dd.open();

        if( !CoreUtil.isNullOrEmpty( dir ) )
        {
            sdkLocation.setText( dir );

            updateProjectsList( dir );

            synchHelper.synchAllUIWithModel();

            validatePage();
        }
    }

    protected void handleRefresh( SelectionEvent e )
    {
        // force a project refresh
        lastModified = -1;
        updateProjectsList( sdkLocation.getText().trim() );

        projectsList.setCheckedElements( new Object[0] );

        getDataModel().setProperty( SELECTED_PROJECTS, projectsList.getCheckedElements() );
    }

    // @Override
    // protected void enter() {
    // super.enter();
    //
    // if (!CoreUtil.isNullOrEmpty(sdkLocation.getText())) {
    // updateProjectsList(sdkLocation.getText());
    // }
    // else {
    // String lastLocation =
    // ProjectUIPlugin.getDefault().getPreferenceStore().getString(
    // ProjectUIPlugin.LAST_SDK_IMPORT_LOCATION_PREF);
    //
    // if (!CoreUtil.isNullOrEmpty(lastLocation)) {
    // sdkLocation.setText(lastLocation);
    //
    // updateProjectsList(lastLocation);
    //
    // synchHelper.synchAllUIWithModel();
    //
    // validatePage();
    // }
    // }
    // }

    protected void handleSelectAll( SelectionEvent e )
    {
        for( int i = 0; i < selectedProjects.length; i++ )
        {
            ProjectRecord projectRecord = (ProjectRecord) selectedProjects[i];

            if( projectRecord.hasConflicts() )
            {
                projectsList.setChecked( projectRecord, false );
            }
            else
            {
                projectsList.setChecked( projectRecord, true );
            }
        }

        getDataModel().setProperty( SELECTED_PROJECTS, projectsList.getCheckedElements() );

        validatePage( true );
        // setPageComplete(projectsList.getCheckedElements().length >
        // 0);
    }

    protected boolean isProjectInWorkspace( String projectName )
    {
        if( projectName == null )
        {
            return false;
        }

        IProject[] workspaceProjects = getProjectsInWorkspace();

        for( int i = 0; i < workspaceProjects.length; i++ )
        {
            if( projectName.equals( workspaceProjects[i].getName() ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean showValidationErrorsOnEnter()
    {
        return true;
    }

    public void updateProjectsList( final String path )
    {
        // on an empty path empty selectedProjects
        if( path == null || path.length() == 0 )
        {
            setMessage( Msgs.importProjectsDescription );

            selectedProjects = new ProjectRecord[0];

            projectsList.refresh( true );

            projectsList.setCheckedElements( selectedProjects );

            setPageComplete( projectsList.getCheckedElements().length > 0 );

            lastPath = path;

            return;
        }

        final File directory = new File( path );

        long modified = directory.lastModified();

        if( path.equals( lastPath ) && lastModified == modified )
        {
            // since the file/folder was not modified and the path did not
            // change, no refreshing is required
            return;
        }

        lastPath = path;

        lastModified = modified;

        final boolean dirSelected = true;

        try
        {
            getContainer().run( true, true, new IRunnableWithProgress()
            {
                /*
                 * (non-Javadoc)
                 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org
                 * .eclipse.core.runtime.IProgressMonitor)
                 */
                public void run( IProgressMonitor monitor )
                {
                    monitor.beginTask( Msgs.searchingMessage, 100 );

                    selectedProjects = new ProjectRecord[0];

                    Collection<File> eclipseProjectFiles = new ArrayList<File>();

                    Collection<File> liferayProjectDirs = new ArrayList<File>();

                    monitor.worked( 10 );

                    if( dirSelected && directory.isDirectory() )
                    {
                        if( !ProjectUtil.collectSDKProjectsFromDirectory(
                            eclipseProjectFiles, liferayProjectDirs, directory, null, true, monitor ) )
                        {
                            return;
                        }

                        selectedProjects = new ProjectRecord[eclipseProjectFiles.size() + liferayProjectDirs.size()];

                        int index = 0;

                        monitor.worked( 50 );

                        monitor.subTask( Msgs.processingMessage );

                        for( File eclipseProjectFile : eclipseProjectFiles )
                        {
                            selectedProjects[index++] = new ProjectRecord( eclipseProjectFile );
                        }

                        for( File liferayProjectDir : liferayProjectDirs )
                        {
                            selectedProjects[index++] = new ProjectRecord( liferayProjectDir );
                        }
                    }
                    else
                    {
                        monitor.worked( 60 );
                    }

                    monitor.done();
                }

            } );
        }
        catch( InvocationTargetException e )
        {
            ProjectUI.logError( e );
        }
        catch( InterruptedException e )
        {
            // Nothing to do if the user interrupts.
        }

        projectsList.refresh( true );

        Object[] projects = getProjectRecords();

        boolean displayWarning = false;

        for( int i = 0; i < projects.length; i++ )
        {
            ProjectRecord projectRecord = (ProjectRecord) projects[i];

            if( projectRecord.hasConflicts() )
            {
                displayWarning = true;

                projectsList.setGrayed( projects[i], true );
            }
            // else {
            // projectsList.setChecked(projects[i], true);
            // }
        }

        if( displayWarning )
        {
            setMessage( Msgs.projectsInWorkspace, WARNING );
        }
        else
        {
            setMessage( Msgs.importProjectsDescription );
        }

        setPageComplete( projectsList.getCheckedElements().length > 0 );

        if( selectedProjects.length == 0 )
        {
            setMessage( Msgs.noProjectsToImport, WARNING );
        }
        // else {
        // if (!sdkLocation.isDisposed()) {
        // ProjectUIPlugin.getDefault().getPreferenceStore().setValue(
        // ProjectUIPlugin.LAST_SDK_IMPORT_LOCATION_PREF, sdkLocation.getText());
        // }
        // }

        Object[] checkedProjects = projectsList.getCheckedElements();

        if( checkedProjects != null && checkedProjects.length > 0 )
        {
            selectedProjects = new ProjectRecord[checkedProjects.length];

            for( int i = 0; i < checkedProjects.length; i++ )
            {
                selectedProjects[i] = (ProjectRecord) checkedProjects[i];
            }
            getDataModel().setProperty( SELECTED_PROJECTS, selectedProjects );
        }
    }

    private static class Msgs extends NLS
    {
        public static String deselectAll;
        public static String importLiferayProjects;
        public static String importProjectsDescription;
        public static String importProjectsLabel;
        public static String liferayPluginSDKLocationLabel;
        public static String liferayPluginSDKVersionLabel;
        public static String liferayTargetRuntimeLabel;
        public static String newButton;
        public static String noProjectsToImport;
        public static String processingMessage;
        public static String projectsInWorkspace;
        public static String refresh;
        public static String searchingMessage;
        public static String selectAll;
        public static String selectLiferayPluginSDK;
        public static String selectLiferayPluginSDKFolder;

        static
        {
            initializeMessages( SDKProjectsImportWizardPage.class.getName(), Msgs.class );
        }
    }
}
