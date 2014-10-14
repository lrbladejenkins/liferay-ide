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
package com.liferay.ide.server.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.internal.commons.notifications.ui.popup.NotificationPopup;
import org.eclipse.mylyn.internal.commons.notifications.ui.popup.PopupNotificationSink;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class ImportGlobalSettingsNotificationSink extends PopupNotificationSink
{
    private NotificationPopup popup;

    public ImportGlobalSettingsNotificationSink()
    {
        super();
    }

    @Override
    public void showPopup()
    {
        if( popup != null )
        {
            popup.close();
        }

        Shell shell = new Shell( PlatformUI.getWorkbench().getDisplay() );
        popup = new NotificationPopup( shell );
        popup.setFadingEnabled( isAnimationsEnabled() );
        List<AbstractNotification> toDisplay = new ArrayList<AbstractNotification>( getNotifications() );
        Collections.sort( toDisplay );
        popup.setContents( toDisplay );
        getNotifications().clear();
        popup.setBlockOnOpen( false );
        popup.setDelayClose( 15 * 1000 );
        popup.open();
    }

}
