/*
 * Created 2006/08/05
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
package org.limy.velocity.task;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.limy.velocity.XmlToHtml;
import org.limy.velocity.XmlToHtmlImpl;

/**
 * VMパース時に利用されるマクロです。
 * @author Naoki Iwami
 */
@SuppressWarnings("unchecked")
public final class VmParseMacro {
    
    /** xml->html変換ツール */
    private final XmlToHtml xmlWriter;
    
    public VmParseMacro(XmlToHtml xmlWriter) {
        this.xmlWriter = xmlWriter;
    }
    
    public String removeExt(String str) {
        if (str == null) {
            return null;
        }
        int index = str.lastIndexOf('.');
        if (index >= 0) {
            return str.substring(0, index);
        }
        return str;
    }
    
    public String translate(String str, String oldChar, String newChar) {
        if (str == null) {
            return null;
        }
        if (oldChar.length() == 1 && newChar.length() == 1) {
            return str.replace(oldChar.charAt(0), newChar.charAt(0));
        }
        return str;
    }

    public String translateBackSlash(String str) {
        if (str == null) {
            return null;
        }
        return str.replace('\\', '/');
    }

    public int toInt(String numberString) {
        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String avg(int total, int count, int scale) {
        if (count == 0) {
            if (total == 0) {
                return BigDecimal.ZERO.setScale(scale).toString();
            }
            return "!!" + Integer.toString(total) + " / 0!!";
        }
        BigDecimal result = new BigDecimal(total);
        return result.divide(new BigDecimal(count), scale,
                BigDecimal.ROUND_HALF_EVEN).toString();
    }
    
    public String concat(String s1, String s2) {
        return s1 + s2;
    }

    public String concat(String s1, String s2, String s3, String s4) {
        return s1 + s2 + s3 + s4;
    }

    public String methodUrl(String name) {
        if (name == null) {
            return null;
        }
        return name.substring(0, name.lastIndexOf('.')).replace('.', '/') + ".html";
    }
    
    public Set<Object> getGroupSet(Collection values, String name) {
        
        if (values == null) {
            return null;
        }
        Set<Object> results = new HashSet<Object>();
        for (Object value : values) {
            results.add(((Map)value).get(name));
        }
        return results;
    }

    public Map<String, Collection<Object>> getGroupMap(Collection<Map<String, Object>> values,
            String targetName) {
        
        if (values == null) {
            return null;
        }
        Map<String, Collection<Object>> results = new LinkedHashMap<String, Collection<Object>>();
        for (Map<String, Object> value : values) {
            
            Collection<Object> resultValues = results.get((String)value.get(targetName));
            if (resultValues == null) {
                resultValues = new ArrayList<Object>();
            }
            resultValues.add(value);
            
            results.put((String)value.get(targetName), resultValues);
        }
        return results;
    }

    public Object searchParent(Map<String, Object> root, Object target) {
        for (Object item : root.values()) {
            if (item.equals(target)) {
                return item;
            }
            
            // TODO このブロックは以前コメントアウトされていたが、使うのでコメントインした。
            // それによる影響は未調査
            if (item instanceof Map) {
                return privatesearchParentM((Map<String, Object>)item, target);
            }
            
            if (item instanceof Collection) {
                Collection list = (Collection)item;
                Object result = privatesearchParentC(root, list, target);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private Object privatesearchParentM(Map<String, Object> root, Object target) {
        for (Object item : root.values()) {
            if (item.equals(target)) {
                return root;
            }
            if (item instanceof Map) {
                Map map = (Map)item;
                Object result = privatesearchParentM(map, target);
                if (result != null) {
                    return result;
                }
            }
            if (item instanceof Collection) {
                Collection list = (Collection)item;
                Object result = privatesearchParentC(root, list, target);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private Object privatesearchParentC(Object parent, Collection<Object> root, Object target) {
        for (Object item : root) {
            if (item.equals(target)) {
                return parent;
            }
            if (item instanceof Map) {
                Map map = (Map)item;
                Object result = privatesearchParentM(map, target);
                if (result != null) {
                    return result;
                }
            }
            if (item instanceof Collection) {
                Collection list = (Collection)item;
                Object result = privatesearchParentC(root, list, target);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public Object[] sortAsc(Collection values) {
        if (values == null) {
            return new Object[0];
        }
        Object[] array = values.toArray(new Object[values.size()]);
        Arrays.sort(array);
        return array;
    }

    public Object[] sortKeyAsc(List values, final String key) {
        if (values == null) {
            return new Object[0];
        }
        Collections.sort(values, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                Comparable key1 = (Comparable)((Map)o1).get(key);
                Comparable key2 = (Comparable)((Map)o2).get(key);
//                System.out.println(key1 + " <=> " + key2);
                return key1.compareTo(key2);
            }
        });
        return values.toArray(new Object[values.size()]);
    }

    public List sortDesc(Collection values) {
        if (values == null) {
            return null;
        }
        Object[] array = values.toArray(new Object[values.size()]);
        Arrays.sort(array);
        List<Object> result = Arrays.asList(array);
        Collections.reverse(result);
        return result;
    }

    public Map createMap() {
        return new HashMap();
    }
    
//    /**
//     * 外部ファイルに内容を書き出します。
//     * @param vmTemplateDir VMテンプレートファイルの基準ディレクトリ
//     * @param baseOutDir 出力先ディレクトリ
//     * @param params パラメータ一覧
//     * @param outFileName 出力先ファイル名
//     * @param context マップ形式のパラメータ一覧
//     * @param vmTemplateName VMテンプレートファイル名
//     * @throws IOException I/O例外
//     */
//    public void write(File vmTemplateDir, File baseOutDir,
//            List<VmParam> params,
//            String outFileName, Object context, String vmTemplateName) throws IOException {
//        
//        VelocityContext velocityContext = new VelocityContext();
//        if (context instanceof Map) {
//            for (Entry entry : ((Map<Object, Object>)context).entrySet()) {
//                velocityContext.put(entry.getKey().toString(), entry.getValue());
//            }
//        }
//        xmlWriter.createHtml(
//                velocityContext,
//                new File(vmTemplateDir, vmTemplateName),
//                new File(baseOutDir, outFileName),
//                params);
//    }

    /**
     * 外部ファイルに内容を書き出します。
     * @param vmTemplatePath VMテンプレートファイルパス
     * @param outFilePath 出力ファイルパス
     * @param innerValues パラメータ一覧
     * @param additionalValues 追加パラメータ一覧
     * @throws IOException I/O例外
     */
    public void write(
            String vmTemplatePath,
            String outFilePath, Map<String, Object> innerValues,
            Map<String, Object> additionalValues) throws IOException {
        
        ((XmlToHtmlImpl)xmlWriter).createHtml(
                vmTemplatePath,
                new File(outFilePath),
                innerValues, additionalValues);
    }

    public int toInt(Object obj) {
        if (obj == null) {
            return 0;
        }
        return Integer.parseInt(obj.toString());
    }
    
    public int count(Map<String, Object> values, String name) {
        
        if (values == null) {
            return 0;
        }
        List<Map<String, Object>> allElements = new ArrayList<Map<String, Object>>();
        appendAll(allElements, values, name);
        return allElements.size();
    }

    public int count(Map<String, Object> values, String name,
            String attributeName, String attributeValue) {
        
        if (values == null) {
            return 0;
        }
        List<Map<String, Object>> allElements = new ArrayList<Map<String, Object>>();
        appendAll(allElements, values, name);
        
        int count = 0;
        for (Map<String, Object> element : allElements) {
            if (attributeValue.equals(element.get(attributeName))) {
                ++count;
            }
        }
        return count;
    }

    /**
     * 値マップから要素を取得します（複数要素対応）。
     * @param values 値マップ
     * @param name 取得する要素名
     * @return 取得した要素一覧
     */
    public Collection<Map<String, Object>> gets(Map<String, Object> values, String name) {
        
        List<Map<String, Object>> allElements = new ArrayList<Map<String, Object>>();
        appendAll(allElements, values, name);
        return allElements;
    }

    public String removePrefix(String allText, String prefix) {
        int textLen = allText.length();
        int prefixLen = prefix.length();
        if (textLen < prefixLen) {
            return allText;
        }
        return allText.substring(prefixLen);
    }
    
    // ------------------------ Private Methods

    /**
     * 値マップから要素を取得します（複数要素対応）。
     * @param values 値マップ
     * @param name 取得する要素名
     * @return 取得した要素一覧
     */
    private Collection<Map<String, Object>> privateGets(Map<String, Object> values, String name) {
        Object singleElement = values.get(name);
        if (singleElement != null) {
            Collection<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
            if (singleElement instanceof String) {
                String str = (String)singleElement;
                Map<String, Object> anonMap = new HashMap<String, Object>();
                anonMap.put("anonText", str);
                results.add(anonMap);
            } else {
                results.add((Map<String, Object>)singleElement);
            }
            return results;
        }
        Object multiElements = values.get(name + "s");
        if (multiElements instanceof Collection) {
            return (Collection<Map<String, Object>>)multiElements;
        }
        return new ArrayList<Map<String, Object>>();
    }

    /**
     * 値マップから要素を取得します（複数要素対応）。
     * @param results 結果格納先
     * @param values 値マップ
     * @param name 取得する要素名
     * @return 取得した要素一覧
     */
    private void appendAll(Collection<Map<String, Object>> results,
            Map<String, Object> values, String name) {
        
        int index = name.indexOf('.');
        if (index >= 0) {
            Collection<Map<String, Object>> firstElements = privateGets(
                    values, name.substring(0, index));
            for (Map<String, Object> firstElement : firstElements) {
                appendAll(results, firstElement, name.substring(index + 1));
            }
        } else {
            results.addAll(privateGets(values, name));
        }
        
    }

}
