package com.liferay.ide.theme.ui;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.freemarker.editor.IFreemarkerEditorConfigurator;
import com.liferay.ide.project.core.BaseLiferayProject;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.ContextValue;
import org.jboss.ide.eclipse.freemarker.configuration.ProjectClassLoader;


public class ThemeFreemarkerEditorConfigurator implements IFreemarkerEditorConfigurator
{

    private static final Map<String, String> contextVariableDefinitions =
        CoreUtil.loadProperties( ThemeFreemarkerEditorConfigurator.class, "ThemeFreemarkerContextVariables.properties" );

    private static final Map<String, WeakReference<Class<?>>> classMap = new HashMap<String, WeakReference<Class<?>>>();

    public IStatus configure( ILiferayProject liferayProject, IEditorInput editorInput )
    {
        IStatus retval = null;

        if( liferayProject instanceof BaseLiferayProject && editorInput instanceof IFileEditorInput )
        {
            final IProject project = ( (BaseLiferayProject) liferayProject ).getProject();
            final IFile editorFile = ( (IFileEditorInput) editorInput ).getFile();

            ConfigurationManager configManager = ConfigurationManager.getInstance( project );
            try
            {
                URL[] urls = new URL[] { liferayProject.getLibraryPath( "portal-impl.jar" ).toFile().toURI().toURL() };
                configManager.setExtraUrls( urls );
            }
            catch( MalformedURLException e2 )
            {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            if( configManager != null && ! CoreUtil.isNullOrEmpty( contextVariableDefinitions ) )
            {
                ContextValue[] existingValues = configManager.getContextValues( editorFile, true );

                if( CoreUtil.isNullOrEmpty( existingValues ) )
                {
                    try
                    {
                        ProjectClassLoader projClassloader = configManager.getProjectClassLoader();

                        for( String key : contextVariableDefinitions.keySet() )
                        {
                            final String value = contextVariableDefinitions.get( key ).toString();
                            Class<?> objClass = null;
                            Class<?> singularClass = null;

                            if( value.indexOf( ',' ) >= 0 )
                            {
                                String[] classes = value.split( "," );

                                WeakReference<Class<?>> objClassref = classMap.get( project.getName()+":"+classes[0] );
                                WeakReference<Class<?>> singularClassref = classMap.get( project.getName()+":"+classes[1] );

                                if( objClassref != null && objClassref.get() != null )
                                {
                                    objClass = objClassref.get();
                                }
                                else
                                {
                                    try
                                    {
                                        objClass = projClassloader.loadClass( classes[0] );
                                        objClassref = new WeakReference<Class<?>>( objClass );
                                        classMap.put( project.getName() + ":" + classes[0], objClassref );
                                        //singularClass = projClassloader.loadClass( classes[1] );
                                    }
                                    catch( Exception e )
                                    {
                                        e.printStackTrace();
                                    }
                                }

                                if( singularClassref != null && singularClassref.get() != null )
                                {
                                    singularClass = singularClassref.get();
                                }
                                else
                                {
                                    try
                                    {
                                        singularClass = projClassloader.loadClass( classes[1] );
                                        singularClassref = new WeakReference<Class<?>>( singularClass );
                                        classMap.put( project.getName() + ":" + classes[1], singularClassref );
                                    }
                                    catch( Exception e )
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else
                            {
                                WeakReference<Class<?>> objClassref = classMap.get( project.getName()+":"+value );


                                if( objClassref != null && objClassref.get() != null )
                                {
                                    objClass = objClassref.get();
                                    singularClass = objClassref.get();
                                }
                                else
                                {
                                    try
                                    {
                                        objClass = projClassloader.loadClass( value );
                                        objClassref = new WeakReference<Class<?>>( objClass );
                                        singularClass = objClass;
                                        classMap.put( project.getName() + ":" + value, objClassref );
                                    }
                                    catch( Exception e )
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if( objClass != null && singularClass != null )
                            {
                                configManager.addContextValue( new ContextValue( key, objClass, singularClass ), editorFile );
                            }
                        }
                    }
                    catch( Exception e1 )
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        }

        return retval;
    }

}
