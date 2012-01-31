/*
 * Created 2007/01/15
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
package org.limy.eclipse.qalab.mark;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IMarkerResolution;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.resource.LimyResourceUtils;
import org.limy.eclipse.common.ui.LimyUIUtils;
import org.limy.eclipse.qalab.LimyQalabMarker;

/**
 * マーカー選択時のResolutionを定義します。
 * @author Naoki Iwami
 */
public final class LimyMarkerResolution implements IMarkerResolution {
    
    // ------------------------ Fields

    /** ラベル文字列 */
    private final String label;

    // ------------------------ Constructors

    /**
     * LimyMarkerResolutionインスタンスを構築します。
     * @param label ラベル文字列
     */
    public LimyMarkerResolution(String label) {
        this.label = label;
    }

    // ------------------------ Implement Methods

    public String getLabel() {
        return label;
    }

    public void run(IMarker marker) {
        
        try {
            String url = (String)marker.getAttribute(LimyQalabMarker.URL);
            if (url.startsWith("http://")) {
                LimyUIUtils.openBrowser(new URL(url));
            } else {
                String[] urls = url.split("#");
                IFile file = (IFile)LimyResourceUtils.newFile(new Path(urls[0]));
                LimyUIUtils.openFile(file, Integer.parseInt(urls[1]));
            }
        } catch (MalformedURLException e) {
            LimyEclipsePluginUtils.log(e);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }

}
