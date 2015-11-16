package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.PortalSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;


/**
 * @author Gregory Amerson
 */
public class GetPortalSettingsWizard extends SapphireWizard<GetPortalSettingsOp>
{

    public GetPortalSettingsWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( GetPortalSettingsWizard.class ).wizard() );
    }

    private static GetPortalSettingsOp createDefaultOp()
    {
        return GetPortalSettingsOp.TYPE.instantiate();
    }

    public static final Status execute( final GetPortalSettingsOp op, final ProgressMonitor pm )
    {
        final File sourceLiferayLocationDir = op.getSourceLiferayLocation().content().toFile();
        final File destLiferayLocationDir = op.getDestinationLiferayLocation().content().toFile();
        final String sourceName = op.getSourceLiferayName().content();
        final String destName = op.getDestinationLiferayName().content();

        try
        {
            Liferay7UpgradeAssistantSettings settings =
                UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

            if( settings == null )
            {
                settings = new Liferay7UpgradeAssistantSettings();
            }

            PortalSettings portalSettings = settings.getPortalSettings();

            if( portalSettings == null )
            {
                portalSettings = new PortalSettings();
            }

            portalSettings.setPreviousName( sourceName );
            portalSettings.setPreviousLiferayPortalLocation( sourceLiferayLocationDir.getPath() );
            portalSettings.setNewName( destName );
            portalSettings.setNewLiferayPortalLocation( destLiferayLocationDir.getPath() );

            settings.setPortalSettings( portalSettings );

            UpgradeAssistantSettingsUtil.setObjectToStore( Liferay7UpgradeAssistantSettings.class, settings );
        }
        catch( IOException e )
        {
            return Status.createErrorStatus( e );
        }

        return Status.createOkStatus();
    }
}
