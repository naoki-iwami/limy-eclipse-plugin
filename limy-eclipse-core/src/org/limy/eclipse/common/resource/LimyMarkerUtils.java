/*
 * Created 2007/08/29
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
package org.limy.eclipse.common.resource;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * マーカー関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class LimyMarkerUtils {
    
    /**
     * private constructor
     */
    private LimyMarkerUtils() { }

    /**
     * リソースにマーカーを追加します。
     * @param markerId マーカーID
     * @param resource リソース
     * @param lineNumber 行番号
     * @param message メッセージ
     * @throws CoreException コア例外
     */
    public static void addMarker(String markerId,
            IResource resource, int lineNumber, String message) throws CoreException {
        
        Map<String, Object> attributes = createBaseAttrs(lineNumber, message);
        MarkerUtilities.createMarker(resource, attributes, markerId);

    }

    /**
     * リソースにマーカーを追加します。
     * @param markerId マーカーID
     * @param resource リソース
     * @param lineNumber 行番号
     * @param message メッセージ
     * @param attrs 追加属性
     * @throws CoreException コア例外
     */
    public static void addMarker(String markerId,
            IResource resource, int lineNumber, String message,
            Map<String, ? extends Object> attrs) throws CoreException {
        
        Map<String, Object> attributes = createBaseAttrs(lineNumber, message);
        attributes.putAll(attrs);
        MarkerUtilities.createMarker(resource, attributes, markerId);

    }

    /**
     * リソースにマーカーを追加します。
     * @param markerId マーカーID
     * @param resource リソース
     * @param attrs 追加属性
     * @throws CoreException コア例外
     */
    public static void addMarker(String markerId,
            IResource resource,
            Map<String, Object> attrs) throws CoreException {
        
        MarkerUtilities.createMarker(resource, attrs, markerId);
    }
    
    // ------------------------ Private Methods

    /**
     * マーカー用の基本属性値を生成して返します。
     * @param lineNumber 行番号
     * @param message メッセージ
     * @return 基本属性値
     */
    private static Map<String, Object> createBaseAttrs(int lineNumber, String message) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(IMarker.PRIORITY, Integer.valueOf(IMarker.PRIORITY_NORMAL));
        attributes.put(IMarker.SEVERITY, Integer.valueOf(IMarker.SEVERITY_WARNING));
        attributes.put(IMarker.LINE_NUMBER, Integer.valueOf(lineNumber));
        attributes.put(IMarker.MESSAGE, message);
        return attributes;
    }


}
