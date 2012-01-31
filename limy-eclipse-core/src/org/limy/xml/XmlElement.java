/*
 * Created 2007/02/07
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
package org.limy.xml;

import java.util.Collection;
import java.util.List;

/**
 * XML要素を表すインターフェイスです。
 * @author Naoki Iwami
 */
public interface XmlElement {

    /**
     * 要素名を返します。
     * @return 要素名
     */
    String getName();

    /**
     * 要素値を返します。
     * @return 要素値
     */
    String getValue();
    
    /**
     * 自分自身のコピーを作成します。
     * @return コピーされた要素
     */
    XmlElement cloneSelf();

    /**
     * 自分自身の直前（同系列）に自身のコピーを作成します。
     * @return コピーされた要素
     */
    XmlElement copyBeforeSelf();

    /**
     * 属性一覧を返します。
     * @return 属性一覧
     */
    Collection<XmlAttribute> getAttributes();
    
    /**
     * 属性を追加します。
     * @param attr 属性
     */
    void setAttribute(XmlAttribute attr);
    
    /**
     * 属性を追加します。
     * @param name 属性名
     * @param value 属性値
     */
    void setAttribute(String name, String value);

    /**
     * 子要素を削除します。
     * @param child 子要素
     */
    void removeChild(XmlElement child);

    /**
     * 属性を持っているかどうかを返します。
     * @return 属性を持っていれば真
     */
    boolean hasAttributes();
    
    /**
     * 子要素を持っているかどうかを返します。
     * @return 子要素を持っていれば真
     */
    boolean hasChildren();
    
    /**
     * 子要素一覧を返します。
     * @return 子要素一覧
     */
    List<XmlElement> getChildren();

    /**
     * 属性値を返します。
     * @param name 属性名
     * @return 属性値
     */
    String getAttribute(String name);
    
    /**
     * 子要素を追加します。
     * @param child 子要素
     */
    void addChild(XmlElement child);
    
    /**
     * 子要素を追加します。
     * @param index 挿入位置
     * @param child 子要素
     */
    void addChild(int index, XmlElement child);




}
