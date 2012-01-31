/*
 * Created 2006/11/24
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
package org.limy.velocity.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.context.Context;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * XMLとVelocityコンテキストに関するユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class XmlContextUtils {
    
    /**
     * private constructor
     */
    private XmlContextUtils() {
        // empty
    }

    /**
     * エレメントの内容をコンテキストに追加します。
     * @param context コンテキスト
     * @param el エレメント
     */
    public static void addValue(Context context, Element el) {
        
        String nodeName = el.getNodeName();

        NodeList childNodes = el.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                
                // エレメント用の値格納場所を作成
                Map<String, Object> values = new HashMap<String, Object>();
                // 作成した箱にエレメントの情報を詰める
                addElement(values, (Element)node);
                
                // コンテキストにエレメント内容をセット
                Object value = context.get(nodeName);
                if (value == null) {
                    context.put(nodeName, values);
                } else if (value instanceof List) {
                    ((List)value).add(values);
                    context.put(nodeName, value);
                } else {
                    for (Entry<String, Object> entry : values.entrySet()) {
                        
                        addEntry((Map)value, entry);
                    }
                    context.put(nodeName, value);
                }
            }
        }
        
        addAttrDirect((Map<String, Object>)context.get(nodeName), el); // ルート要素の内容をコンテキストに追加

    }

    // ------------------------ Private Methods

    /**
     * マップにEntryの内容を全て追加します。
     * @param value マップ
     * @param entry Entry 
     */
    private static void addEntry(Map<String, Object> value, Entry<String, Object> entry) {
        
        String entryKey = entry.getKey();
        if (value.get(createMultipleName(entryKey)) != null) {
            List<Object> orgList = (List)value.get(createMultipleName(entryKey));
            orgList.add(entry.getValue());
        } else {
            Object orgValue = value.get(entryKey);
            if (orgValue == null) {
                value.put(entryKey, entry.getValue());
            } else if (orgValue instanceof List) {
                ((List)orgValue).add(entry.getValue());
            } else {
                List<Object> newList = new ArrayList<Object>();
                newList.add(orgValue);
                newList.add(entry.getValue());
                Map<String, Object> newMap = new HashMap<String, Object>();
                newMap.put(createMultipleName(entryKey), newList);
                value.remove(entryKey);
                value.put(createMultipleName(entryKey), newList);
            }
        }
    }

    /**
     * エレメントの情報を値マップに詰めます。
     * @param parentValues 値マップ
     * @param el エレメント
     */
    private static void addElement(Map<String, Object> parentValues, Element el) {
        String nodeName = el.getNodeName();
        NodeList childNodes = el.getChildNodes();
        
        addAttrValues(parentValues, el);
        
        boolean enableElement = false;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (!enableElement && node instanceof Text) {
                if (parentValues.get(nodeName) == null) {
                    parentValues.put(nodeName, node.getNodeValue());
                } else {
                    Map<String, Object> values = (Map<String, Object>)parentValues.get(nodeName);
                    // 属性値と子要素（Anonymousテキスト）が同時に存在する場合、固定値"anontext"として子要素を追加
                    values.put("anontext", node.getNodeValue());
                }
            }
            if (node instanceof Element) {
                enableElement = true;
                Map<String, Object> subValues = new HashMap<String, Object>();
                addElement(subValues, (Element)node);
                
                if (parentValues.get(nodeName) instanceof String) {
                    parentValues.put(nodeName, subValues);
                } else if (parentValues.get(nodeName) instanceof List) {
                    addChildValue(parentValues, nodeName, subValues);
                } else {
                    addChildValue(parentValues, nodeName, subValues, node.getNodeName());
                }
            }
        }
    }
    /**
     * 要素の全属性を values に追加します（直下追加版）。
     * @param values 値格納先マップ
     * @param el 要素
     */
    private static void addAttrDirect(Map<String, Object> values, Element el) {
        NamedNodeMap attrs = el.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            values.put(attr.getNodeName(), attr.getNodeValue());
        }
    }
    
    /**
     * 要素の全属性を values に追加します（サブマップ作成版）。
     * @param values 値格納先マップ
     * @param el 要素
     */
    private static void addAttrValues(Map<String, Object> values, Element el) {
        NamedNodeMap attrs = el.getAttributes();
        if (attrs.getLength() > 0) {
            Map<String, Object> subValues = new HashMap<String, Object>();
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                subValues.put(attr.getNodeName(), attr.getNodeValue());
            }
            
            String nodeName = el.getNodeName();
            if (values.get(nodeName) == null) {
                values.put(nodeName, subValues);
            } else {
                throw new IllegalStateException("予期されていない形式のXMLファイルです。");
            }
        }
    }

    /**
     * 親の値マップに子の値を追加します。
     * @param parentValues 親の値マップ
     * @param nodeName 親エレメント名
     * @param subValues 子の値マップ
     */
    private static void addChildValue(Map<String, Object> parentValues,
            String nodeName, Map<String, Object> subValues) {
        
        // 子の先頭値を取得
        Object subValueFirst = subValues.values().toArray()[0];
//        if (subValueFirst instanceof String) {
//            // 文字列の場合、親の値マップに子の値を追加
//            List<String> parentValue = (List<String>)(parentValues.get(nodeName));
//            parentValue.add((String)subValueFirst);
//            
//        } else {
            // マップの場合、親の値マップに子の値を追加
            List<Map<String, Object>> parentValue = (List<Map<String, Object>>)
                    (parentValues.get(nodeName));
            parentValue.add((Map<String, Object>)subValueFirst);
//        }
    }

    

    /**
     * 親の値マップに子の値を追加します。
     * @param parentValues 親の値マップ
     * @param parentName 親エレメント名
     * @param subValues 子の値マップ
     * @param childName 子エレメント名
     */
    private static void addChildValue(Map<String, Object> parentValues,
            String parentName, Map<String, Object> subValues, String childName) {
        
        Map<String, Object> orgValues = (Map<String, Object>)parentValues.get(parentName);
        if (orgValues == null) {
            parentValues.put(parentName, subValues);
        } else {
            for (Entry<String, Object> entry : subValues.entrySet()) {
                
                String entryKey = entry.getKey();
                if (orgValues.get(createMultipleName(entryKey)) != null) {
                    List orgList = (List)orgValues.get(createMultipleName(entryKey));
                    orgList.add(entry.getValue());
                } else if (orgValues.get(entryKey) != null) {
                    Map<String, Object> mapByParent = (Map<String, Object>)
                            parentValues.get(parentName);
                    Object v = mapByParent.get(childName);
                    List result = makeList(v, subValues.values().toArray()[0]);
                    
                    //((Map)parentValues.get(parentName)).keySet().iterator().next() // error
                    mapByParent.remove(childName);
                    mapByParent.put(createMultipleName(childName), result);
//                    parentValues.put(parentName, result);

                    break;
                } else {
                    orgValues.put(entryKey, entry.getValue());
                }
            }
        }
    }

    /**
     * 既存の値と、今回追加する値をリストに詰めて返します。
     * @param orgValue 既存の値
     * @param nowValue 今回追加する値
     * @return 既存の値と今回追加する値を詰めたリスト
     */
    private static List makeList(Object orgValue, Object nowValue) {
        
        if (orgValue instanceof List) {
            ((List)orgValue).add(nowValue);
            return (List)orgValue;
        } else if (orgValue instanceof String) {
            List<String> list = new ArrayList<String>();
            list.add((String)orgValue);
            list.add((String)nowValue);
            return list;
        } else {
            Map<String, Object> mapValue = (Map<String, Object>)orgValue;
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            list.add(mapValue);
            list.add((Map<String, Object>)nowValue);
            return list;
        }
    }

    /**
     * 名称を複数用名称にして返します。
     * @param name 名称
     * @return 複数用名称
     */
    private static String createMultipleName(String name) {
        return name + "s";
    }

}
