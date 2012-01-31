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
package org.limy.eclipse.qalab.task;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;
import org.limy.xml.XmlWriteUtils;

/**
 * ソースファイルからTO-DO文字列を検出して出力するクラスです。
 * @author Naoki Iwami
 */
public class TodoReport {

    // ------------------------ Constants

    /**
     * 改行文字
     */
    private static final String BR = "\n";
    
    /**
     * todoパターン
     */
    private static final Pattern PATTERN_TODO = Pattern.compile(".*(\\s)TODO(\\s)(.*)");
    
    // ------------------------ Fields

    /** パース結果格納先 */
    private List<TodoBean> beans = new ArrayList<TodoBean>();

    // ------------------------ Public Methods
    
    public void parseJavaSource(String fileName, String contents) {
        TodoBean bean = new TodoBean();
        bean.setName(fileName);
        LineIterator iterator = new LineIterator(new StringReader(contents));
        int lineNumber = 1;
        while (iterator.hasNext()) {
            String line = iterator.nextLine();
            Matcher matcher = PATTERN_TODO.matcher(line);
            if (matcher.matches()) {
                String message = matcher.group(3);
                bean.addError(new TodoError(lineNumber, message));
            }
            ++lineNumber;
        }
        beans.add(bean);
    }
    
    /**
     * 検出結果をXML形式でファイルに出力します。
     * @param file 出力先ファイル
     * @param charset 出力ファイルの文字セット
     * @throws IOException I/O例外
     */
    public void writeXml(File file, String charset) throws IOException {
        StringBuilder buff = new StringBuilder();
        buff.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(BR);
        buff.append("<todo>").append(BR);
        for (TodoBean bean : beans) {
            XmlElement fileEl = XmlUtils.createElement("file");
            fileEl.setAttribute("name", bean.getName());
            for (TodoError error : bean.getErrors()) {
                XmlElement errorEl = XmlUtils.createElement(fileEl, "error");
                errorEl.setAttribute("line", Integer.toString(error.getLine()));
                errorEl.setAttribute("message", XmlUtils.escapeAttributeValue(error.getMessage()));
            }
            XmlWriteUtils.writeXml(buff, fileEl);
        }
        buff.append("</todo>").append(BR);
        FileUtils.writeStringToFile(file, buff.toString(), charset);
    }

}
