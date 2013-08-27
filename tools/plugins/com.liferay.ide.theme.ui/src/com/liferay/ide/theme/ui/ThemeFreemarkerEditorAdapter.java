package com.liferay.ide.theme.ui;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.ILiferayProjectAdapter;
import com.liferay.ide.freemarker.editor.IFreemarkerEditorConfigurator;
import com.liferay.ide.project.core.BaseLiferayProject;
import com.liferay.ide.project.core.util.ProjectUtil;


/**
 * @author Gregory Amerson
 */
public class ThemeFreemarkerEditorAdapter implements ILiferayProjectAdapter
{

    public ThemeFreemarkerEditorAdapter()
    {
        super();
    }

    public <T> T adapt( ILiferayProject liferayProject, Class<T> adapterType )
    {
        if( liferayProject instanceof BaseLiferayProject && IFreemarkerEditorConfigurator.class.equals( adapterType ) )
        {
            BaseLiferayProject baseLiferayProject = (BaseLiferayProject) liferayProject;

            if( ProjectUtil.isThemeProject( baseLiferayProject.getProject() ) )
            {
                return adapterType.cast( new ThemeFreemarkerEditorConfigurator() );
            }
        }

        return null;
    }

}
