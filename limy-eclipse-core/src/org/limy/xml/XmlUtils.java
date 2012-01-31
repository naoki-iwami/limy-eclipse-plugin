/*
 * Created 2006/11/22
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * XML関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class XmlUtils {

    // ------------------------ Constants

    /** XMLドキュメントビルダ */
    private static DocumentBuilder builder;

    // ------------------------ Constructors

    /**
     * private constructor
     */
    private XmlUtils() { }

    // ------------------------ Static Inititalizer
    
    static {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(false);
        factory.setCoalescing(true);
        factory.setValidating(false);
        factory.setNamespaceAware(false);
        try {
            builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver() {

                public InputSource resolveEntity(String publicId,
                        String systemId) throws SAXException, IOException {
                    return new InputSource(new StringReader(""));
                }
                
            });
        } catch (ParserConfigurationException e) {
            e.printStackTrace(); // ログが使えないので標準エラー出力に吐く
        }
        
    }
    
    // ------------------------ Public Methods
    
    /**
     * XMLをパースしてDOM Elementを返します。
     * @param xmlInput XML入力ストリーム
     * @return DOM Element
     * @throws IOException I/O例外
     */
    public static Element parse(InputStream xmlInput) throws IOException {
        try {
            Document doc = builder.parse(xmlInput);
            return doc.getDocumentElement();
        } catch (SAXException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * XMLをパースしてDOM Elementを返します。
     * @param xmlFile XMLファイル
     * @return DOM Element
     * @throws IOException I/O例外
     */
    public static Element parse(File xmlFile) throws IOException {
        try {
            Document doc = builder.parse(xmlFile);
            return doc.getDocumentElement();
        } catch (SAXException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * XML要素を作成します。
     * @param parent 親要素
     * @param name 要素名
     * @return 作成された要素
     */
    public static XmlElement createElement(XmlElement parent, String name) {
        return new SimpleElement(parent, name);
    }

    /**
     * XML要素を作成します。
     * @param name 要素名
     * @return 作成された要素
     */
    public static XmlElement createElement(String name) {
        return new SimpleElement(name);
    }

    /**
     * XMLの属性値に使用する文字列をエスケープします。
     * @param str 文字列
     * @return エスケープした文字列
     */
    public static String escapeAttributeValue(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
        
    }

}
