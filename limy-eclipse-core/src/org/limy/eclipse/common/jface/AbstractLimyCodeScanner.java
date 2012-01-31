/*
 * Created 2004/12/17
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
package org.limy.eclipse.common.jface;

import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.limy.eclipse.common.LimyStoreColorProvider;

/**
 * LimyEclipseで使用する基底コードスキャナクラスです。
 * @author Naoki Iwami
 */
public abstract class AbstractLimyCodeScanner extends RuleBasedScanner
        implements IPropertyChangeListener {

    // ------------------------ Abstract Methods

    /**
     * ビューアを更新します。
     * @param sourceViewer
     * @param colorFieldEditors
     */
    public abstract void refreshViewer(SourceViewer sourceViewer,
            LimyColorFieldEditor[] colorFieldEditors);

    // ------------------------ Implement Methods
    
    public void propertyChange(PropertyChangeEvent event) {
        LimyColorFieldEditor colorFieldEditor = (LimyColorFieldEditor)event.getSource();
        refreshViewer(colorFieldEditor.getSourceViewer(),
                new LimyColorFieldEditor[] { colorFieldEditor });
    }

    // ------------------------ Protected Methods

    /**
     * トークンを取得します。
     * @param provider
     * @param colorKey
     * @return トークン
     */
    protected IToken simpleToken(LimyStoreColorProvider provider, String colorKey) {
        return new Token(new TextAttribute(provider.getColor(colorKey)));
    }

    /**
     * トークンを取得します。
     * @param color
     * @return トークン
     */
    protected IToken simpleToken(Color color) {
        return new Token(new TextAttribute(color));
    }

    /**
     * @param colors
     * @param provider
     * @param colorKey
     */
    protected void addColorFromProvider(Map<String, Color> colors,
            LimyStoreColorProvider provider, String colorKey) {
        colors.put(colorKey, provider.getColor(colorKey));
    }

    /**
     * @param colors
     * @param color
     * @param colorKey
     */
    protected void addColor(Map<String, Color> colors, RGB color, String colorKey) {
        colors.put(colorKey, new Color(null, color));
    }

}
