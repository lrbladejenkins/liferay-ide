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
import org.eclipse.wst.server.core.ServerCore;
import org.w3c.dom.DOMException;

/**
 * @author Kamesh Sampath
 */
public enum LiferayRuntimeMementoUtil
{
    INSTANCE;

    final File LIFERAY_IDE_PREF_DIR = new File( System.getProperty( "user.home" ), ".liferay-ide" );

    XMLMemento runtimesMemento;

    private void init()
    {
        if( !LIFERAY_IDE_PREF_DIR.exists() )
        {
            LIFERAY_IDE_PREF_DIR.mkdirs();
        }
    }

    public void saveOrDeleteRuntimes()
    {
        init();
        List<IRuntime> liferayRuntimes = getLiferayRuntimes();
        if( !liferayRuntimes.isEmpty() )
        {
            saveRuntimeMemento( liferayRuntimes );
        }
    }

    public void loadRuntimes( IRuntime iRuntime )
    {
        try
        {
            FileReader fileReader = new FileReader( new File( LIFERAY_IDE_PREF_DIR, "runtimes.xml" ) );
            runtimesMemento = XMLMemento.createReadRoot( fileReader );
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

    public void saveRuntimeMemento( List<IRuntime> liferayRuntimes )
    {
        try
        {
            FileWriter fileWriter = new FileWriter( new File( LIFERAY_IDE_PREF_DIR, "runtimes.xml" ) );
            for( IRuntime iRuntime : liferayRuntimes )
            {
                IMemento iMemento = runtimesMemento.createChild( "runtime" );
                addRuntimeToMemento( iMemento, iRuntime );

            }
            runtimesMemento.save( fileWriter );
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

    private List<IRuntime> getLiferayRuntimes()
    {
        IRuntime[] runtimes = ServerCore.getRuntimes();
        List<IRuntime> liferayRuntimes = new ArrayList<IRuntime>();

        for( int i = 0; i < runtimes.length; i++ )
        {
            IRuntime liferayRuntime = runtimes[i];
            String runtimeTypeId = liferayRuntime.getRuntimeType().getId();

            if( "com.liferay.".equals( runtimeTypeId ) )
            {
                liferayRuntimes.add( liferayRuntime );
            }
        }
        return liferayRuntimes;
    }

    private void addRuntimeToMemento( IMemento iMemento, IRuntime iRuntime )
    {
        iMemento.putString( "id", iRuntime.getId() );
        iMemento.putString( "typeId", iRuntime.getRuntimeType().getId() );
        iMemento.putString( "name", iRuntime.getName() );
        iMemento.putString( "location", iRuntime.getLocation().toOSString() );
        iMemento.putBoolean( "readOnly", iRuntime.isReadOnly() );
        iMemento.putBoolean( "isStub", iRuntime.isStub() );
        iMemento.putBoolean( "isWorkingCopy", iRuntime.isWorkingCopy() );
    }
}
