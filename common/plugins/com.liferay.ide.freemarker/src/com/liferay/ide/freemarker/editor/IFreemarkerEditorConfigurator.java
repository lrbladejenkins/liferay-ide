package com.liferay.ide.freemarker.editor;

import com.liferay.ide.core.ILiferayProject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorInput;


/**
 * @author Gregory Amerson
 */
public interface IFreemarkerEditorConfigurator
{

    IStatus configure( ILiferayProject liferayProject, IEditorInput editorInput );

}
