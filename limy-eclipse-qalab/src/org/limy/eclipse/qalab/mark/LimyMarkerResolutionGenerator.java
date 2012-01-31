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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.qalab.LimyQalabMarker;

/**
 * マーカー選択時の処理を定義したクラスです。
 * TODO 現在、Problemマーカーがある行ではQuick Fixが無効になってしまう。
 * 
 * @depend - - - LimyMarkerResolution
 * @author Naoki Iwami
 */
public class LimyMarkerResolutionGenerator implements IMarkerResolutionGenerator {

    /** NULL結果 */
    private static final IMarkerResolution[] NULL_RESOLUTORS = new IMarkerResolution[0];
    
    /** URLとヘルプ文字の対応 */
    private static final Map<String, String> URL_LINKS = new HashMap<String, String>();
    
    static {
        URL_LINKS.put("http://checkstyle.sourceforge.net/", "Browse to detail page (Checkstyle)");
        URL_LINKS.put("http://findbugs.sourceforge.net/", "Browse to detail page (Findbugs)");
        URL_LINKS.put("http://pmd.sourceforge.net/", "Browse to detail page (PMD)");
        URL_LINKS.put("/", "Jump to duplication source");
    }
    
    public IMarkerResolution[] getResolutions(IMarker marker) {
        try {
            String url = (String)marker.getAttribute(LimyQalabMarker.URL);
            if (isTarget(marker) && url != null) {
                
                String label = null;
                for (String prefix : URL_LINKS.keySet()) {
                    if (url.startsWith(prefix)) {
                        label = URL_LINKS.get(prefix);
                    }
                }
                
                return new IMarkerResolution[] {
                        new LimyMarkerResolution(label),
                }; 
            }
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return NULL_RESOLUTORS;
    }

    // ------------------------ Private Methods

    /**
     * マーカーが対象のIDを持つかどうかを判定します。
     * @param marker マーカー
     * @return マーカーが対象の場合、true
     * @throws CoreException コア例外
     */
    private boolean isTarget(IMarker marker) throws CoreException {
        return LimyQalabMarker.PROBLEM_ID.equals(marker.getType());
//                || LimyQalabMarker.TEMPORARY_ID.equals(marker.getType());
    }

}
