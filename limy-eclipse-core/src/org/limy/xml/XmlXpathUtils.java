/*
 * Created 2007/08/14
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
import java.util.List;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * XPath関連のXMLユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class XmlXpathUtils {

    /**
     * private constructor
     */
    private XmlXpathUtils() { }
    
    /**
     * XPATH文字列に従ってノード配列のイテレータを返します。
     * @param root ルート要素
     * @param xpath XPATH文字列
     * @return ノード配列のイテレータ
     */
    public static Iterable<Node> getNodeList(Element root, String xpath) {
        
        try {
            NodeList nodeList = XPathAPI.selectNodeList(root, xpath);
            List<Node> results = new ArrayList<Node>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                results.add(nodeList.item(i));
            }
            return results;
        } catch (TransformerException e) {
            throw new IllegalArgumentException(e);
        }
                
    }

}
