/*
 * Created 2007/01/18
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

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.Rule;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.resource.LimyMarkerUtils;
import org.limy.eclipse.qalab.LimyQalabMarker;

/**
 * PMDのマーカー作成を担当する補助クラスです。
 * @author Naoki Iwami
 */
public class PmdMarkAppender {

    /**
     * リソースにマーカーを付けます。
     * @param resource リソース
     * @param ruleViolation PMDルールチェック結果
     * @param temporary 一時フラグ
     * @throws CoreException コア例外
     */
    public void addMarker(IResource resource, IRuleViolation ruleViolation,
            boolean temporary) throws CoreException {
        IMarker[] markers = resource.findMarkers(
                LimyQalabMarker.DEFAULT_ID, true, IResource.DEPTH_ZERO);
        
        boolean exist = false;
        for (IMarker marker : markers) {
            if (Integer.valueOf(ruleViolation.getBeginLine()).equals(
                    marker.getAttribute(IMarker.LINE_NUMBER))
                    && ruleViolation.getDescription().equals(
                            marker.getAttribute(IMarker.MESSAGE))) {
                exist = true;
                break;
            }
        }
        
        if (!exist) {
            String markerId = /*temporary ? LimyQalabMarker.TEMPORARY_ID
                    : */LimyQalabMarker.PROBLEM_ID;
            
            Map<String, Object> attrs = new HashMap<String, Object>();
            attrs.put(LimyQalabMarker.URL, getUrl(ruleViolation));

            LimyMarkerUtils.addMarker(markerId,
                    resource, ruleViolation.getBeginLine(),
                    ruleViolation.getDescription(), attrs);
        }
    }

    // ------------------------ Private Methods

    /**
     * ルールに対応したURLを返します。
     * @param ruleViolation ルール
     * @return URL
     */
    private String getUrl(IRuleViolation ruleViolation) {
        
        // http://pmd.sourceforge.net/rules/naming.html#AvoidFieldNameMatchingMethodName
        Rule rule = ruleViolation.getRule();
        if (rule.getExternalInfoUrl().length() == 0) {
            // net.sourceforge.pmd.rules.design.UnsynchronizedStaticDateFormatter
            String name = ruleViolation.getRule().getClass().getName();
            String[] names = name.split("\\.");
            if (names.length < 6) {
                LimyEclipsePluginUtils.log("Pmd name = " + name);
            } else {
                String linkName = names[5];
                String groupName = names[4];
                return "http://pmd.sourceforge.net/rules/" + groupName + ".html#" + linkName;
            }
        }
        return rule.getExternalInfoUrl();
    }

}
