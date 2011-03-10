/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.eclipse.project.ui.dialog;

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.ui.LiferayUIPlugin;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * A dialog for selecting a project to configure project specific settings for
 * 
 * @since 1.0.0
 */
public class LiferayProjectSelectionDialog extends SelectionStatusDialog {

	class LiferayJavaElementContentProvider extends StandardJavaElementContentProvider {
		public Object[] getChildren(Object element) {
			if (element instanceof IJavaModel) {
				IJavaModel model = (IJavaModel) element;
				Set<IJavaProject> set = new HashSet<IJavaProject>();
				try {
					IJavaProject[] projects = model.getJavaProjects();
					for (int i = 0; i < projects.length; i++) {
						if (ProjectUtil.isLiferayProject(projects[i].getProject())) {
							set.add(projects[i]);
						}
					}
				} catch (JavaModelException jme) {
					//ignore
				}
				return set.toArray();
			}
			return super.getChildren(element);
		}
	}

	// the visual selection widget group
	private TableViewer fTableViewer;

	// sizing constants
	private final static int SIZING_SELECTION_WIDGET_HEIGHT = 250;
	private final static int SIZING_SELECTION_WIDGET_WIDTH = 300;

	/**
	 * The filter for the viewer
	 */
	private ViewerFilter fFilter;

	/**
	 * Constructor
	 * @param parentShell
	 * @param projectsWithSpecifics
	 */
	public LiferayProjectSelectionDialog(Shell parentShell, ViewerFilter filter) {
		super(parentShell);
		setTitle("Project Selection");
		setMessage("Select project");

		fFilter = filter;
	}

	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {
		// page group
		Composite composite = (Composite) super.createDialogArea(parent);

		Font font = parent.getFont();
		composite.setFont(font);

		createMessageArea(composite);

		fTableViewer = new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				doSelectionChanged(((IStructuredSelection) event.getSelection()).toArray());
			}
		});
		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				okPressed();
			}
		});
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
		data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
		fTableViewer.getTable().setLayoutData(data);

		fTableViewer.setLabelProvider(new JavaElementLabelProvider());
		fTableViewer.setContentProvider(new LiferayJavaElementContentProvider());
		fTableViewer.setComparator(new JavaElementComparator());
		fTableViewer.getControl().setFont(font);

		updateFilter(true);

		IJavaModel input = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
		fTableViewer.setInput(input);

		doSelectionChanged(new Object[0]);
		Dialog.applyDialogFont(composite);
		return composite;
	}

	/**
	 * Handles the change in selection of the viewer and updates the status of the dialog at the same time
	 * @param objects
	 */
	private void doSelectionChanged(Object[] objects) {
		if (objects.length != 1) {
			updateStatus(new Status(IStatus.ERROR, LiferayUIPlugin.PLUGIN_ID, "")); //$NON-NLS-1$
			setSelectionResult(null);
		} else {
			updateStatus(new Status(IStatus.OK, LiferayUIPlugin.PLUGIN_ID, "")); //$NON-NLS-1$
			setSelectionResult(objects);
		}
	}

	/**
	 * Updates the viewer filter based on the selection of the 'show project with...' button
	 * @param selected
	 */
	protected void updateFilter(boolean selected) {
		if (fFilter == null) {
			return;
		}

		if (selected) {
			fTableViewer.addFilter(fFilter);
		} else {
			fTableViewer.removeFilter(fFilter);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
	 */
	protected void computeResult() {
	}
}
