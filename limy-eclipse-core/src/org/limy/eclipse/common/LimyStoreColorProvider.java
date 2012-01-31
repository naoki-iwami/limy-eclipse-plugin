/*
 * Created 2004/09/18
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
package org.limy.eclipse.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Limy Eclipseで使用する色提供プロバイダクラスです。
 * <p>ストアと連動して値を取得することが可能です</p>
 * @author Naoki Iwami
 * @depend - - - LimyEclipseConstants
 */
public class LimyStoreColorProvider {
    
    // ------------------------ Static Fields

    /**
     * 色情報を格納するマップ（色キー => RGB）
     */
    private static Map<String, RGB> defaultColor = new HashMap<String, RGB>(10);

    // ------------------------ Fields
    
    /** ストア */
    private IPreferenceStore store;
    
    /**
     * カラーテーブルのキャッシュ（色キー => 色）
     */
    private Map<String, Color> fColorTable = new HashMap<String, Color>(10);
    
    // ------------------------ Constructors
    
    /**
     * LimyColorProviderインスタンスを構築します。
     * @param store ストア
     */
    public LimyStoreColorProvider(IPreferenceStore store) {
        this.store = store;
        addDefaultColor("text", new RGB(0, 0, 0));
        addDefaultColor(LimyEclipseConstants.P_BGCOLOR, new RGB(240, 255, 240));
    }
    
    // ------------------------ Public Methods

    /**
     * Release all of the color resources held onto by the receiver.
     */
    public void dispose() {
        for (Color color : fColorTable.values()) {
            color.dispose();
        }
        fColorTable.clear();
    }
    
    /**
     * 色を取得します。
     * <p>ストアに色が設定してあればその色を、していなければデフォルト値として設定した色を返します。</p>
     * @param key 色キー
     * @return 色
     */
    public Color getColor(String key) {
        
        // 色キャッシュから情報を取得
        Color color = fColorTable.get(key);
        
        if (color == null) {
            // ストアからRGB値を取得
            String rgb = store.getString(key);
            if (rgb == null || rgb.length() == 0) {
                // 取得できなかった場合、デフォルト値に設定
                color = new Color(Display.getCurrent(), defaultColor.get(key));
            } else {
                // 取得できたら、そのRGB値を使って色を作成
                color = new Color(Display.getCurrent(), StringConverter.asRGB(rgb));
            }
            fColorTable.put(key, color); // キャッシュに保存
        }
        return color;
    }
    
    /**
     * デフォルト色を追加します。
     * @param key 色キー
     * @param color 色
     */
    protected final void addDefaultColor(String key, RGB color) {
        defaultColor.put(key, color);
    }
    
}
