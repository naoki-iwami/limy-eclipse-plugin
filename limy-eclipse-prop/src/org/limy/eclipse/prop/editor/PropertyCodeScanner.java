/*
 * Created 2004/12/02
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
package org.limy.eclipse.prop.editor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.limy.eclipse.common.LimyStoreColorProvider;
import org.limy.eclipse.common.jface.AbstractLimyCodeScanner;
import org.limy.eclipse.common.jface.LimyColorFieldEditor;
import org.limy.eclipse.prop.LimyPropConstants;

/**
 * プロパティエディタ内のコードスキャナ(色付けルール)を定義したクラスです。
 * @depend - - - PropertyRule
 * @author Naoki Iwami
 */
public class PropertyCodeScanner extends AbstractLimyCodeScanner {
    
    /**
     * @param colorProvider
     */
    public PropertyCodeScanner(LimyStoreColorProvider colorProvider) {
        super();
        Map<String, Color> colors = getColorsFromProvider(colorProvider);
        setRules(createRules(colors));
    }

    public void refreshViewer(SourceViewer sourceViewer, LimyColorFieldEditor[] colorFieldEditors) {
        LimyStoreColorProvider provider = ProviderManager.getInstance().getColorProvider();
        PropertyCodeScanner scanner = new PropertyCodeScanner(provider);
        Map<String, Color> colors = getColorsFromProvider(provider);
        
        for (int i = 0; i < colorFieldEditors.length; i++) {
            LimyColorFieldEditor editor = colorFieldEditors[i];
            RGB color = editor.getColorSelector().getColorValue();
            String preferenceName = editor.getPreferenceName();
            
            addColor(colors, color, preferenceName);
            scanner.setRules(createRules(colors));
        }
        
        sourceViewer.unconfigure();
        sourceViewer.configure(new PropertySourceViewerConfiguration(scanner));
        sourceViewer.refresh();
    }
    
    //--------------------------------------------------------- Private Methods

    /**
     * 色情報を取得します。
     * @param provider
     * @return 色情報
     */
    private Map<String, Color> getColorsFromProvider(LimyStoreColorProvider provider) {
        Map<String, Color> colors = new LinkedHashMap<String, Color>();
        // 以下は順番が重要なので変更しない事
        addColorFromProvider(colors, provider, LimyPropConstants.P_COLOR_NAME);
        addColorFromProvider(colors, provider, LimyPropConstants.P_COLOR_VALUE);
        addColorFromProvider(colors, provider, LimyPropConstants.P_COLOR_COMMENT);
        return colors;
    }

    /**
     * ルールを作成します。
     * @param colors 色一覧（順序を持ったマップ）
     * @return 作成されたルール
     */
    private IRule[] createRules(Map<String, Color> colors) {

        List<IRule> rules = new ArrayList<IRule>();

        TextAttribute nameAttr = null;
        TextAttribute valueAttr = null;
        TextAttribute commentAttr = null;
        
        for (Entry<String, Color> entry : colors.entrySet()) {
            
            String colorKey = (String)entry.getKey();
            Color color = (Color)entry.getValue();
            
            if (colorKey.equals(LimyPropConstants.P_COLOR_NAME)) {
                nameAttr = new TextAttribute(color);
            }
            if (colorKey.equals(LimyPropConstants.P_COLOR_VALUE)) {
                valueAttr = new TextAttribute(color);
            }
            if (colorKey.equals(LimyPropConstants.P_COLOR_COMMENT)) {
                commentAttr = new TextAttribute(color);
            }
        }

        rules.add(new PropertyRule(nameAttr, valueAttr, commentAttr));

        return rules.toArray(new IRule[rules.size()]);
    }
    
}
