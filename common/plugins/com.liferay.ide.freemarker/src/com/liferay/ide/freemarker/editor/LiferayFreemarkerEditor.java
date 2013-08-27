package com.liferay.ide.freemarker.editor;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.jboss.ide.eclipse.freemarker.editor.FreemarkerMultiPageEditor;


public class LiferayFreemarkerEditor extends FreemarkerMultiPageEditor
{

    public LiferayFreemarkerEditor()
    {
        super();
    }

    @Override
    public void init( IEditorSite site, IEditorInput editorInput ) throws PartInitException
    {
        final IProject project = getProject( editorInput );

        ILiferayProject liferayProject = LiferayCore.create( project );

        if( liferayProject != null )
        {
            IFreemarkerEditorConfigurator configurator = liferayProject.adapt( IFreemarkerEditorConfigurator.class );

            if( configurator != null )
            {
                configurator.configure( liferayProject, editorInput );
            }
        }

        super.init( site, editorInput );
    }

    private IProject getProject( IEditorInput editorInput )
    {
        if( editorInput instanceof IFileEditorInput )
        {
            IFileEditorInput fileInput = (IFileEditorInput) editorInput;

            return fileInput.getFile().getProject();
        }

        return null;
    }
}
