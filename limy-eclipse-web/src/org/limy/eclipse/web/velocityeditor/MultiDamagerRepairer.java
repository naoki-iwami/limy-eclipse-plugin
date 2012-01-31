/*
 * Created 2006/01/14
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
package org.limy.eclipse.web.velocityeditor;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.swt.custom.StyleRange;
import org.limy.eclipse.common.jface.MultiTextAttribute;

/**
 * マルチ属性を使用する文字表示カスタマイズクラスです。
 * @author Naoki Iwami
 */
public class MultiDamagerRepairer extends DefaultDamagerRepairer {
    
    /**
     * デフォルト属性
     */
    private MultiTextAttribute defaultAttr = new MultiTextAttribute();

    /**
     * @param scanner
     */
    public MultiDamagerRepairer(ITokenScanner scanner) {
        super(scanner);
    }
    
    /**
     * @param presentation 評価を反映する先
     * @param region 評価する範囲
     */
    public void createPresentation(TextPresentation presentation,
            ITypedRegion region) {
        
        if (fScanner == null) {
            addRange(presentation, region.getOffset(), region.getLength(), fDefaultTextAttribute);
            return;
        }
        
        fScanner.setRange(fDocument, region.getOffset(), region.getLength());
        
        while (true) {
            IToken token = fScanner.nextToken();         
            if (token.isEOF()) {
                break;
            }
            
            MultiTextAttribute multiAttribute = getMultiTextAttribute(token);
            for (int i = 0; i < multiAttribute.size(); i++) {
                int start = fScanner.getTokenOffset() + multiAttribute.getOffset(i);
                int length = multiAttribute.getLength(i);
                addRange(presentation, start, length, multiAttribute.get(i));
            }
        }

    }
    
    /**
     * 属性を取得します。
     * @param token トークン
     * @return 属性
     */
    private MultiTextAttribute getMultiTextAttribute(IToken token) {
        Object data = token.getData();
        if (data instanceof MultiTextAttribute) {
            return (MultiTextAttribute)data;
        }
        return defaultAttr;
    }
    
    protected void addRange(
            TextPresentation presentation, int offset, int length, TextAttribute attr) {
        if (attr != null) {
            presentation.addStyleRange(
                    new StyleRange(offset, length, attr.getForeground(),
                            attr.getBackground(), attr.getStyle()));
        }
    }

}
