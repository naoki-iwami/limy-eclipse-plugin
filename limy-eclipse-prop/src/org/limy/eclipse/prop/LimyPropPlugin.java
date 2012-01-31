/*
 * Created 2005/09/11
 * Copyright (C) 2003-2009  Naoki Iwami (naoki@limy.org)
 *
 * This file is part of Limy Eclipse Plugin.
 *
 * Limy Eclipse Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Limy Eclipse Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Limy Eclipse Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.limy.eclipse.prop;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 *
 * @author Naoki Iwami
 */
public class LimyPropPlugin extends AbstractUIPlugin {

    /**
     * 唯一のLimyPropPluginインスタンス
     */
    private static LimyPropPlugin plugin;

    // ------------------------ Constructors
    
    /**
     * The constructor.
     */
    public LimyPropPlugin() {
        super();
        plugin = this;
    }

    /**
     * Returns the shared instance.
     * @return 唯一のLimyPropPluginインスタンス
     */
    public static LimyPropPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.limy.eclipse.lrd", path);
    }
    
    // ------------------------ Private Methods

}
