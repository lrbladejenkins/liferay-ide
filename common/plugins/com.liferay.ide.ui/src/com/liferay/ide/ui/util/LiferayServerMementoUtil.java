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

package com.liferay.ide.ui.util;

import com.liferay.ide.ui.LiferayUIPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.w3c.dom.DOMException;

/**
 * @author Kamesh Sampath
 */
public enum LiferayServerMementoUtil
{
    INSTANCE;

    final File LIFERAY_IDE_PREF_DIR = new File( System.getProperty( "user.home" ), "liferay-ide" );

    XMLMemento serversMemento;

    LiferayServerMementoUtil()
    {
        if( !LIFERAY_IDE_PREF_DIR.exists() )
        {
            LIFERAY_IDE_PREF_DIR.mkdirs();
        }
    }

    public void saveOrDeleteRuntimes()
    {
        List<IServer> liferayServers = getLiferayRuntimes();
        saveRuntimeMemento( liferayServers );
    }

    public void loadRuntimes( IRuntime iRuntime )
    {
        try
        {
            FileReader fileReader = new FileReader( new File( LIFERAY_IDE_PREF_DIR, "servers.xml" ) );
            serversMemento = XMLMemento.createReadRoot( fileReader );
        }
        catch( IOException e )
        {
            LiferayUIPlugin.logError( e );
        }
        catch( WorkbenchException e )
        {
            LiferayUIPlugin.logError( e );
        }

    }

    public void saveRuntimeMemento( List<IServer> liferayServers )
    {
        try
        {
            FileWriter fileWriter = new FileWriter( new File( LIFERAY_IDE_PREF_DIR, "runtimes.xml" ) );
            for( IServer iServer : liferayServers )
            {
                IMemento iMemento = serversMemento.createChild( "runtime" );
                addRuntimeToMemento( iMemento, iServer );

            }
            serversMemento.save( fileWriter );
        }
        catch( DOMException e )
        {
            LiferayUIPlugin.logError( e );
        }
        catch( IOException e )
        {
            LiferayUIPlugin.logError( e );
        }

    }

    private List<IServer> getLiferayRuntimes()
    {
        IServer[] servers = ServerCore.getServers();
        List<IServer> liferayServers = new ArrayList<IServer>();

        for( int i = 0; i < servers.length; i++ )
        {
            IServer liferayServer = servers[i];

            String runtimeTypeId = liferayServer.getServerType().getId();

            if( "com.liferay.".equals( runtimeTypeId ) )
            {
                liferayServers.add( liferayServer );
            }
        }
        return liferayServers;
    }

    private void addRuntimeToMemento( IMemento iMemento, IServer iServer )
    {

        // TODO need to discuss on what more information needed to persist
        iMemento.putString( "id", iServer.getId() );
        iMemento.putString( "typeId", iServer.getServerType().getId() );
        iMemento.putString( "name", iServer.getName() );
        iMemento.putString( "host", iServer.getHost() );
        iMemento.putString( "mode", iServer.getMode() );
        iMemento.putString( "runtimeId", iServer.getRuntime().getId() );
        iMemento.putBoolean( "readOnly", iServer.isReadOnly() );
        iMemento.putBoolean( "isWorkingCopy", iServer.isWorkingCopy() );
    }
}
