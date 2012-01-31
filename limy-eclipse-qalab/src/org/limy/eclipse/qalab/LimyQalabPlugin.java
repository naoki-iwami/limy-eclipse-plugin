/*
 * Created 2006/08/16
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
package org.limy.eclipse.qalab;

import java.io.File;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * Limy Eclipse Qalab Pluginのプラグインクラスです。
 * @depend - - - LimyQalabPluginImages
 * @author Naoki Iwami
 */
public class LimyQalabPlugin extends AbstractUIPlugin {

    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.limy.eclipse.qalab";

    /** The shared instance */
    private static LimyQalabPlugin plugin;
    
    // ------------------------ Fields

    /** Pluginルートディレクトリ */
    private File rootDir;

    // ------------------------ Constructors

    /**
     * The constructor
     */
    public LimyQalabPlugin() {
        super();
        plugin = this;
    }

//    @Override
//    public void stop(BundleContext context) throws Exception {
//        plugin = null;
//        super.stop(context);
//    }

    /**
     * Returns the shared instance
     * @return the shared instance
     */
    public static LimyQalabPlugin getDefault() {
        return plugin;
    }

    @Override
    protected ImageRegistry createImageRegistry() {
        return LimyQalabPluginImages.initializeImageRegistry();
    }

    // ------------------------ Public Methods

    public File getPluginRoot() {
        if (rootDir == null) {
            rootDir = LimyEclipsePluginUtils.getPluginRoot(plugin); // この処理に4秒くらい掛かる
        }
        return rootDir;
    }
    
}
