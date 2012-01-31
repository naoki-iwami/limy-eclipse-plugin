/*
 * Created 2005/07/21
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextAttribute;

/**
 * 複数の文字属性を表現するクラスです。
 * @author Naoki Iwami
 */
public class MultiTextAttribute /*extends TextAttribute*/ {
    
    /**
     * 属性リスト
     */
    private List<TextAttribute> textAttributes;
    
    /**
     * 位置リスト
     */
    private List<Position> positions;
    
    /**
     * MultiTextAttributeインスタンスを構築します。
     */
    public MultiTextAttribute() {
//        super(new Color(null, 0, 0, 0));
        textAttributes = new ArrayList<TextAttribute>();
        positions = new ArrayList<Position>();
    }

    /**
     * インスタンスに属性を追加します。
     * @param attribute 属性
     * @param length 文字長
     */
    public void add(TextAttribute attribute, int length) {
        if (positions.isEmpty()) {
            positions.add(new Position(0, length));
        } else {
            Position pos = positions.get(positions.size() - 1);
            int offset = pos.getOffset() + pos.getLength();
            if (offset < 0 || length - offset < 0) {
//                LimyEclipsePluginUtils.log("offset = " + offset + ", length = " + (length - offset));
                return;
            } else {
                positions.add(new Position(offset, length - offset));
            }
        }
        textAttributes.add(attribute);
    }

    /**
     * インスタンスに複数属性を追加します。
     * @param multiAttr 複数属性
     * @param adjustOffset 調整オフセット値
     */
    public void addMulti(MultiTextAttribute multiAttr, int adjustOffset) {

        int offset;
        if (positions.isEmpty()) {
            offset = 0;
        } else {
            Position pos = positions.get(positions.size() - 1);
            offset = pos.getOffset() + pos.getLength();
        }
        offset += adjustOffset;
        
        for (int i = 0; i < multiAttr.size(); i++) {
            TextAttribute attr = multiAttr.get(i);
            int multiOffset = multiAttr.getOffset(i);
            int multiLength = multiAttr.getLength(i);
            positions.add(new Position(offset + multiOffset, multiLength));
            textAttributes.add(attr);
        }
    }

    /**
     * 属性を取得します。
     * @param index インデックス番号
     * @return 属性
     */
    public TextAttribute get(int index) {
        return textAttributes.get(index);
    }

    /**
     * オフセットを取得します。
     * @param index インデックス番号
     * @return オフセット
     */
    public int getOffset(int index) {
        return positions.get(index).getOffset();
    }

    /**
     * 文字長を取得します。
     * @param index インデックス番号
     * @return 文字長
     */
    public int getLength(int index) {
        return positions.get(index).getLength();
    }
    
    /**
     * 保持する属性のサイズを取得します。
     * @return 属性のサイズ
     */
    public int size() {
        return textAttributes.size();
    }
}
