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

package com.liferay.ide.project.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Greg Amerson
 */
public class ProjectUI extends AbstractUIPlugin
{

//    private static final String MIGRATION_ROOT = "Migration";
//    private static final String MARKER_PROBLEMS_ROOT = "Migration";

    // Shared images
    public static final String WAR_IMAGE_ID = "war.image"; //$NON-NLS-1$
    public static final String CHECKED_IMAGE_ID = "checked.image"; //$NON-NLS-1$
    public static final String UNCHECKED_IMAGE_ID = "unchecked.image"; //$NON-NLS-1$

    public static final String LAST_SDK_IMPORT_LOCATION_PREF = "last.sdk.import.location"; //$NON-NLS-1$

    // The shared instance
    private static ProjectUI plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.project.ui"; //$NON-NLS-1$

//    private File _migrationFile;
//    private File _markerProblemsFile;

    public static IStatus createErrorStatus( String msg )
    {
        return createErrorStatus( msg, null );
    }

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    public static IStatus createInfoStatus( String msg )
    {
        return new Status( IStatus.INFO, PLUGIN_ID, msg, null );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static ProjectUI getDefault()
    {
        return plugin;
    }

    public static void logError( Exception e )
    {
        getDefault().getLog().log( createErrorStatus( e.getMessage(), e ) );
    }

    // private static IConfigurationElement[] pluginWizardFragmentElements;

    // public static IPluginWizardFragment getPluginWizardFragment(String pluginFacetId) {
    // if (CoreUtil.isNullOrEmpty(pluginFacetId)) {
    // return null;
    // }
    //
    // IConfigurationElement[] fragmentElements = getPluginWizardFragmentsElements();
    //
    // for (IConfigurationElement fragmentElement : fragmentElements) {
    // if (pluginFacetId.equals(fragmentElement.getAttribute("facetId"))) {
    // try {
    // Object o = fragmentElement.createExecutableExtension("class");
    //
    // if (o instanceof IPluginWizardFragment) {
    // IPluginWizardFragment fragment = (IPluginWizardFragment) o;
    // fragment.setFragment(true);
    // return fragment;
    // }
    // }
    // catch (CoreException e) {
    // ProjectUIPlugin.logError("Could not load plugin wizard fragment for " + pluginFacetId, e);
    // }
    // }
    // }
    //
    // return null;
    // }

    // public static IConfigurationElement[] getPluginWizardFragmentsElements() {
    // if (pluginWizardFragmentElements == null) {
    // pluginWizardFragmentElements =
    // Platform.getExtensionRegistry().getConfigurationElementsFor(IPluginWizardFragment.ID);
    // }
    //
    // return pluginWizardFragmentElements;
    // }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }

    public static void logInfo( String msg )
    {
        getDefault().getLog().log( createInfoStatus( msg ) );
    }

    /**
     * The constructor
     */
    public ProjectUI()
    {
    }

    @Override
    protected void initializeImageRegistry( ImageRegistry registry )
    {
        Bundle bundle = Platform.getBundle( PLUGIN_ID );
        IPath path = new Path( "icons/e16/war.gif" ); //$NON-NLS-1$
        URL url = FileLocator.find( bundle, path, null );
        ImageDescriptor desc = ImageDescriptor.createFromURL( url );
        registry.put( WAR_IMAGE_ID, desc );

        IPath checked = new Path( "icons/e16/checked.png" ); //$NON-NLS-1$
        URL checkedurl = FileLocator.find( bundle, checked, null );
        ImageDescriptor checkeddesc = ImageDescriptor.createFromURL( checkedurl );
        registry.put( CHECKED_IMAGE_ID, checkeddesc );

        IPath unchecked = new Path( "icons/e16/unchecked.png" ); //$NON-NLS-1$
        URL uncheckedurl = FileLocator.find( bundle, unchecked, null );
        ImageDescriptor uncheckeddesc = ImageDescriptor.createFromURL( uncheckedurl );
        registry.put( UNCHECKED_IMAGE_ID, uncheckeddesc );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    @Override
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );

        plugin = this;

//        _migrationFile = getStateLocation().append( Migration.class.getName() + ".xml" ).toFile();
//        _markerProblemsFile = getStateLocation().append( "markerProblems.xml" ).toFile();
    }


//    public void saveMigrationTask( final MigrationTask task ) throws CoreException
//    {
//        XMLMemento memento = null;
//
//        if( _migrationFile.exists() )
//        {
//            try
//            {
//                memento = XMLMemento.createReadRoot( new BufferedReader( new FileReader( _migrationFile ) ) );
//            }
//            catch( WorkbenchException | FileNotFoundException e )
//            {
//                throw new CoreException( createErrorStatus( "Could not read migration tasks data.", e ) );
//            }
//        }
//        else
//        {
//            _migrationFile.getParentFile().mkdirs();
//
//            try
//            {
//                _migrationFile.createNewFile();
//
//                memento = XMLMemento.createWriteRoot( MIGRATION_ROOT );
//            }
//            catch( IOException e )
//            {
//                throw new CoreException( createErrorStatus( "Could not write migration tasks data.", e ) );
//            }
//        }
//
//        if( memento != null )
//        {
//            final IMemento taskNode = memento.createChild( MigrationTask.class.getSimpleName() );
//
//            taskNode.putString( "date", new SimpleDateFormat().format( new Date( task.getTimestamp() ) ) );
//
//            final List<Problem> problems = task.getProblems();
//
//            for( Problem problem : problems )
//            {
//                final IMemento problemNode = taskNode.createChild( Problem.class.getSimpleName() );
//
//                try
//                {
//                    if( problem instanceof TaskProblem )
//                    {
//                        writeProblemToMemento( (TaskProblem) problem, problemNode );
//                    }
//                    else
//                    {
//                        writeProblemToMemento( new TaskProblem( problem, false ), problemNode );
//                    }
//                }
//                catch( IOException e )
//                {
//                    logError( "Unable to write problem data", e );
//                }
//            }
//
//            try( OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream( _migrationFile ) ) )
//            {
//                memento.save( writer );
//            }
//            catch( IOException e )
//            {
//                throw new CoreException( createErrorStatus( "Could not save migration tasks data.", e ) );
//            }
//        }
//    }

//    private void writeProblemToMemento( TaskProblem problem, IMemento problemTag ) throws IOException
//    {
//        problemTag.putInteger( "number", problem.number );
//        problemTag.putString( "title", problem.title );
//        problemTag.putString( "summary", problem.summary );
//        problemTag.putString( "url", problem.url );
//        problemTag.putString( "type", problem.type );
//        problemTag.putString( "ticket", problem.ticket );
//        problemTag.putString( "file", problem.file.getCanonicalPath() );
//        problemTag.putInteger( "lineNumber", problem.lineNumber );
//        problemTag.putInteger( "startOffset", problem.startOffset );
//        problemTag.putInteger( "endOffset", problem.endOffset );
//        problemTag.putBoolean( "resolved", problem.isResolved() );
//
//    }

//    public List<TaskProblem> getTaskProblems( boolean includeResolved ) throws CoreException
//    {
//        final List<TaskProblem> taskProblems = new ArrayList<>();
//
//        try
//        {
//            final XMLMemento root =
//                XMLMemento.createReadRoot( new BufferedReader( new FileReader( _markerProblemsFile ) ) );
//
//            final IMemento[] taskProblemNodes = root.getChildren( TaskProblem.class.getSimpleName() );
//
//
//            for( IMemento taskProblemNode : taskProblemNodes )
//            {
//                final TaskProblem taskProblem = readProblem( taskProblemNode );
//
//                taskProblems.add( taskProblem );
//            }
//        }
//        catch( FileNotFoundException e )
//        {
//            throw new CoreException( createErrorStatus( "Could not read migration tasks data.", e ) );
//        }
//
//        return taskProblems;
//    }

//    public List<MigrationTask> getMigrationTasks( boolean includeResolved ) throws CoreException
//    {
//        final List<MigrationTask> tasks = new ArrayList<>();
//
//        try
//        {
//            final XMLMemento root =
//                XMLMemento.createReadRoot( new BufferedReader( new FileReader( _migrationFile ) ) );
//
//            final IMemento[] taskNodes = root.getChildren( MigrationTask.class.getSimpleName() );
//
//
//            for( IMemento taskNode : taskNodes )
//            {
//                final String dateString = taskNode.getString( "date" );
//
//                final Date date = new SimpleDateFormat().parse( dateString );
//
//                final MigrationTask task = new MigrationTask( date.getTime() );
//
//                final IMemento[] problemNodes = taskNode.getChildren( Problem.class.getSimpleName() );
//
//                for( IMemento problemNode : problemNodes )
//                {
//                    final TaskProblem problem = readProblem( problemNode );
//
//                    if( !problem.isResolved() || includeResolved )
//                    {
//                        task.addProblem( problem );
//                    }
//                }
//
//                tasks.add( task );
//            }
//        }
//        catch( WorkbenchException | FileNotFoundException | ParseException e )
//        {
//            throw new CoreException( createErrorStatus( "Could not read migration tasks data.", e ) );
//        }
//
//        return tasks;
//    }

//    private TaskProblem readProblem( IMemento problem )
//    {
//        return new TaskProblem(
//            problem.getString( "title" ), problem.getString( "url" ), problem.getString( "summary" ),
//            problem.getString( "type" ), problem.getString( "ticket" ), new File( problem.getString( "file" ) ),
//            problem.getInteger( "lineNumber" ), problem.getInteger( "startOffset" ),
//            problem.getInteger( "endOffset" ), problem.getBoolean( "resolved" ) );
//    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    @Override
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;

        super.stop( context );
    }

//    public void saveMarkerProblem( String string, TaskProblem taskProblem ) throws CoreException
//    {
//        XMLMemento memento = null;
//
//        if( _markerProblemsFile.exists() )
//        {
//            try
//            {
//                memento = XMLMemento.createReadRoot( new BufferedReader( new FileReader( _markerProblemsFile ) ) );
//            }
//            catch( WorkbenchException | FileNotFoundException e )
//            {
//                throw new CoreException( createErrorStatus( "Could not read problem data.", e ) );
//            }
//        }
//        else
//        {
//            _markerProblemsFile.getParentFile().mkdirs();
//
//            try
//            {
//                _markerProblemsFile.createNewFile();
//
//                memento = XMLMemento.createWriteRoot( MARKER_PROBLEMS_ROOT );
//            }
//            catch( IOException e )
//            {
//                throw new CoreException( createErrorStatus( "Could not write migration tasks data.", e ) );
//            }
//        }
//
//        if( memento != null )
//        {
//            final IMemento problemNode = memento.createChild( TaskProblem.class.getSimpleName() );
//
//            problemNode.putString( "date", new SimpleDateFormat().format( new Date( taskProblem.getTimestamp() ) ) );
//
//            try
//            {
//                writeProblemToMemento( taskProblem, problemNode );
//            }
//            catch( IOException e )
//            {
//                logError( "Unable to write problem data", e );
//            }
//
//            try( OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream( _markerProblemsFile ) ) )
//            {
//                memento.save( writer );
//            }
//            catch( IOException e )
//            {
//                throw new CoreException( createErrorStatus( "Could not save migration tasks data.", e ) );
//            }
//        }
//    }
}
