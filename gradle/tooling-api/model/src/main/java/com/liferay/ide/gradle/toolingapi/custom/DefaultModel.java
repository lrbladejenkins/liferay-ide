package com.liferay.ide.gradle.toolingapi.custom;

import java.io.Serializable;
import java.util.List;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("serial")
public class DefaultModel implements Serializable {

    private final List<String> pluginClassNames;

    public DefaultModel(List<String> pluginClassNames) {
        this.pluginClassNames = pluginClassNames;
    }

    public boolean hasPlugin(String pluginClassName) {
    	return pluginClassNames.contains(pluginClassName);
    }
}
