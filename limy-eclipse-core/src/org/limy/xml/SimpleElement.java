/*
 * Created 2006/08/19
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * XML要素を表します。
 * @author Naoki Iwami
 * @version 1.0.0
 */
public class SimpleElement implements XmlElement, Cloneable {
    
    // ------------------------ Fields

    /**
     * 親要素
     */
    private XmlElement parent;
    
    /**
     * 要素名
     */
    private String name;

    /**
     * 要素値
     * <p>
     * この値が設定されているとき、子要素一覧は無視されます。
     * </p>
     */
    private String value;
    
    /**
     * 属性一覧
     */
    private Set<XmlAttribute> attributes = new LinkedHashSet<XmlAttribute>();
    
    /**
     * 子要素一覧
     */
    private List<XmlElement> elements = new ArrayList<XmlElement>();

    // ------------------------ Constructors

    /**
     * SimpleElementインスタンスを構築します。
     * @param name 要素名
     */
    public SimpleElement(String name) {
        super();
        this.name = name;
    }

    /**
     * SimpleElementインスタンスを構築します。
     * @param parent 親要素
     * @param name 要素名
     */
    public SimpleElement(XmlElement parent, String name) {
        super();
        this.parent = parent;
        this.name = name;
        parent.addChild(this);
    }

    /**
     * SimpleElementインスタンスを構築します。
     * @param parent 親要素
     * @param name 要素名
     * @param afterEl 挿入位置
     */
    public SimpleElement(XmlElement parent, String name, XmlElement afterEl) {
        super();
        this.parent = parent;
        this.name = name;
        for (int i = 0; i < parent.getChildren().size(); i++) {
            if (parent.getChildren().get(i).equals(afterEl)) {
                parent.addChild(i, this);
                break;
            }
        }
    }

    /**
     * SimpleElementインスタンスを構築します。
     * @param parent 親要素
     * @param name 要素名
     * @param value 要素値
     */
    public SimpleElement(SimpleElement parent, String name, String value) {
        super();
        this.parent = parent;
        this.name = name;
        this.value = value;
        parent.addChild(this);
    }
    
    // ------------------------ Override Methods

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append(name).append(" : ");
        for (XmlAttribute attr : attributes) {
            buff.append(attr).append(',');
        }
        for (XmlElement el : elements) {
            buff.append('\n');
            buff.append(el);
        }
        return buff.toString();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        SimpleElement obj = (SimpleElement)super.clone();
        List<XmlElement> orgElements = new ArrayList<XmlElement>();
        orgElements.addAll(obj.getChildren());
        obj.setElements(orgElements);
        return obj;
    }

    // ------------------------ Implement Methods
    
    public XmlElement cloneSelf() {
        try {
            return (XmlElement)clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public XmlElement copyBeforeSelf() {
        SimpleElement result = new SimpleElement(parent, name, this);
        for (XmlAttribute attr : getAttributes()) {
            result.setAttribute(attr);
        }
        for (XmlElement child : getChildren()) {
            result.addChild(child);
        }
        return result;
    }
    
    /**
     * 属性を追加します。
     * @param attr 属性
     */
    public void setAttribute(XmlAttribute attr) {
        attributes.remove(attr);
        attributes.add(attr);
    }

    /**
     * 属性を追加します。
     * @param name 属性名
     * @param value 属性値
     */
    public void setAttribute(String name, String value) {
        setAttribute(new SimpleAttribute(name, value));
    }

    /**
     * 子要素を削除します。
     * @param child 子要素
     */
    public void removeChild(XmlElement child) {
        elements.remove(child);
    }

    /**
     * 属性を持っているかどうかを返します。
     * @return 属性を持っていれば真
     */
    public boolean hasAttributes() {
        return attributes != null && !attributes.isEmpty();
    }
    
    /**
     * 子要素を持っているかどうかを返します。
     * @return 子要素を持っていれば真
     */
    public boolean hasChildren() {
        return elements != null && !elements.isEmpty();
    }
    
    /**
     * 子要素一覧を返します。
     * @return 子要素一覧
     */
    public List<XmlElement> getChildren() {
        return elements;
    }

    public String getAttribute(String name) {
        for (XmlAttribute attr : attributes) {
            if (attr.getName().equals(name)) {
                return attr.getValue();
            }
        }
        return null;
    }
    
    /**
     * 子要素を追加します。
     * @param child 子要素
     */
    public void addChild(XmlElement child) {
        elements.add(child);
    }
    
    /**
     * 子要素を追加します。
     * @param index 挿入位置
     * @param child 子要素
     */
    public void addChild(int index, XmlElement child) {
        elements.add(index, child);
    }

    // ------------------------ Getter/Setter Methods

    /**
     * 要素名を取得します。
     * @return 要素名
     */
    public String getName() {
        return name;
    }

    /**
     * 要素値を取得します。
     * @return 要素値
     */
    public String getValue() {
        return value;
    }

    /**
     * 属性一覧を取得します。
     * @return 属性一覧
     */
    public Collection<XmlAttribute> getAttributes() {
        return attributes;
    }
    
    // ------------------------ Private Methods
    
    /**
     * 子要素一覧を設定します。
     * @param elements 子要素一覧
     */
    private void setElements(List<XmlElement> elements) {
        this.elements = elements;
    }
    
}
