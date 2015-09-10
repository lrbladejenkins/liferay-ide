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
package com.liferay.ide.project.ui.migration;

import blade.migrate.api.Problem;

import com.liferay.ide.project.ui.ProjectUI;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.navigator.CommonNavigator;


public class MigrationView2 extends CommonNavigator
{

    private FormText _form;


    @Override
    public void createPartControl( Composite parent )
    {
        SashForm viewParent = new SashForm( parent, SWT.HORIZONTAL );

        super.createPartControl( viewParent );

        _form = new FormText( viewParent, SWT.NONE );

        getCommonViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged( SelectionChangedEvent event )
            {
                updateForm( event );
            }
        });
    }

    private void updateForm( SelectionChangedEvent event )
    {
        ISelection selection = event.getSelection();

        TaskProblem taskProblem = getTaskProblemFromSelection( selection );

        if( taskProblem != null )
        {
            _form.setText( generateFormText( taskProblem ), true, false );
        }
    }


    private TaskProblem getTaskProblemFromSelection( ISelection selection )
    {
        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            Object element = ss.getFirstElement();

            if( element instanceof IFile )
            {
                MigrationContentProvider content =
                    (MigrationContentProvider) getCommonViewer().getContentProvider();

                List<Problem> problems = content._problemsMap.get( element );

                return (TaskProblem) problems.get( 0 );
            }
        }

        return null;
    }


    private String generateFormText( TaskProblem taskProblem )
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "<form><p>" );

        sb.append( "<b>Problem:</b><br/>" );
        sb.append( "\t" + taskProblem.title + "<br/>" );

        sb.append( "<b>Description:</b><br/>" );
        sb.append( "\t" + taskProblem.summary + "<br/>" );

        sb.append( "<b>Type:</b><br/>" );
        sb.append( "\t" + taskProblem.type + "<br/>" );

        sb.append( "<b>Line:</b><br/>" );
        sb.append( "\t" + taskProblem.lineNumber + "<br/>" );

        sb.append( "<b>Tickets:</b><br/>" );
        sb.append( "\t" + "<a href='lps'>" + taskProblem.ticket + "</a><br/>" );

        sb.append( "<b>More details:</b><br/>" );
        sb.append( "\t" + "<a href='lps'>See full Breaking Changes document</a><br/>" );

        sb.append( "</p></form>" );

        return sb.toString();
    }


    protected Object getInitialInput()
    {
        try
        {
            return ProjectUI.getDefault().getMigrationTasks( false );
        }
        catch( CoreException e )
        {

        }

        return super.getInitialInput();
    };

}
