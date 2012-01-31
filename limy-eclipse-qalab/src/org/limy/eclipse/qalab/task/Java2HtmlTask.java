/*
 * Created 2006/07/05
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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.cobertura.reporting.html.JavaToHtml;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * JavaファイルからHTMLファイルを生成するAntタスクです。
 * <p>
 * 内部でCoberturaのクラスを使用しています。
 * </p>
 * @author Naoki Iwami
 */
public class Java2HtmlTask extends Task {
    
    // ------------------------ Constants

    /**
     * 改行文字
     */
    private static final String BR = "\n";
    
    // ------------------------ Fields

    /**
     * ソースディレクトリ
     */
    private File srcDir;

    /**
     * 出力先ディレクトリ
     */
    private File destDir;
    
    /**
     * 行番号アンカー指定による行ハイライト機能を有効にするかどうか
     */
    private boolean enableLineAnchor;
    
    /**
     * ソースファイルの文字セット
     */
    private String inputCharset;

    /**
     * ファイルセットリスト
     */
    private List<FileSet> fileSets = new LinkedList<FileSet>();

    // ------------------------ Override Methods

    @Override
    public void execute() {
        JavaToHtml cmd = new JavaToHtml();
        
        try {
            
            if (fileSets.isEmpty()) {
                execWithSrcDir(cmd);
                return;
            }
            
            if (srcDir != null) {
                throw new BuildException(
                        "srcDir と fileset 要素はどちらか一つだけ記述する必要があります。");
            }
            
            execWithFileset(cmd);
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    // ------------------------ Private Methods

    /**
     * srcdir指定でコマンドを実行します。
     * @param cmd java->html変換コマンド
     * @throws IOException I/O例外
     */
    private void execWithSrcDir(JavaToHtml cmd) throws IOException {
        if (srcDir == null) {
            throw new BuildException("srcDir または fileset 要素は必須です。");
        }
        
        Collection<String> classNames = new ArrayList<String>();
        
        Iterator<Object> fileIt = FileUtils.iterateFiles(srcDir, new String[] { "java" }, true);
        while (fileIt.hasNext()) {
            File file = (File)fileIt.next();
            writeFile(cmd, file, srcDir);
            classNames.add(file.getAbsolutePath().substring(
                    srcDir.getAbsolutePath().length() + 1));
        }
        createIndexHtml(classNames);
    }
    
    /**
     * fileset指定でコマンドを実行します。
     * @param cmd java->html変換コマンド
     * @throws IOException I/O例外
     */
    private void execWithFileset(JavaToHtml cmd) throws IOException {
        
        Collection<String> classNames = new ArrayList<String>();

        for (FileSet fileSet : fileSets) {
            DirectoryScanner scanner = fileSet.getDirectoryScanner(getProject());
            for (String fileStr : scanner.getIncludedFiles()) {
                File targetBaseDir = fileSet.getDir(getProject());
                File file = new File(targetBaseDir, fileStr);
                writeFile(cmd, file, targetBaseDir);
                classNames.add(file.getAbsolutePath().substring(
                        targetBaseDir.getAbsolutePath().length() + 1));
            }
        }
        createIndexHtml(classNames);
    }

    /**
     * JavaファイルをHTMLに変換して出力します。
     * @param cmd java->html変換コマンド
     * @param targetFile Javaファイル
     * @param targetBaseDir Javaファイルの基準ディレクトリ
     * @throws IOException I/O例外
     */
    private void writeFile(JavaToHtml cmd, File targetFile,
            File targetBaseDir) throws IOException {
        if (!"java".equals(FilenameUtils.getExtension(targetFile.getName()))) {
            return;
        }
        String lines = FileUtils.readFileToString(targetFile, inputCharset);
        
        String relativePath = targetFile.getAbsolutePath().substring(
                targetBaseDir.getAbsolutePath().length() + 1);
        File outputFile = new File(destDir,
                FilenameUtils.getPath(relativePath)
                + FilenameUtils.getBaseName(relativePath) + ".html");

        String fileName = FilenameUtils.getPath(relativePath)
                + FilenameUtils.getBaseName(relativePath);
        fileName = fileName.replace('/', '.');
        fileName = fileName.replace('\\', '.');
        String result = convertHtml(cmd, lines, fileName);
        outputFile.getParentFile().mkdirs();
        FileUtils.writeStringToFile(outputFile, result, "UTF-8");
    }
    
    /**
     * java文をhtmlに変換します。
     * @param cmd java->html変換コマンド
     * @param lines Java文
     * @param fileName ファイル名
     * @return HTML文
     */
    private String convertHtml(JavaToHtml cmd, String lines, String fileName) {
        LineIterator iterator = new LineIterator(new StringReader(lines));
        
        StringBuilder buff = new StringBuilder();
        appendLine(buff, "<head>");
        appendLine(buff, "<meta http-equiv=\"Content-Type\" content=\"text/html;"
                + " charset=UTF-8\"/>");
        buff.append("<title>").append(fileName).append("</title>");
        appendLine(buff, "<style type=\"text/css\">");
        appendLine(buff, "<!-- pre.src { margin-top: 0px; margin-bottom: 0px; } -->");
        appendLine(buff, "<!-- td.numLine { background: #f0f0f0; border-right: #dcdcdc 1px solid;"
                + " padding-right: 3px; text-align: right; } -->");
        appendLine(buff, "<!-- table.src {  border: #dcdcdc 1px solid; font-size: 16px; } -->");
        appendLine(buff, "<!-- td.src { width: 100%; } -->");
        appendLine(buff, "<!-- span.comment { color: #b22222; font-style: italic; } -->");
        appendLine(buff, "<!-- span.keyword { color: #2020bf; font-weight: bold; } -->");
        appendLine(buff, "<!-- span.string { color: #2a00ff; } -->");
        appendLine(buff, "<!-- span.text_italic { font-size: 12px; font-style: italic; } -->");
        appendLine(buff, "<!-- body { font-family: verdana, arial, helvetica; } -->");
        appendLine(buff, "<!-- div.separator { height: 10px; } -->");
        appendLine(buff, "<!-- table tr td, table tr th { font-size: 75%; } -->");
        appendLine(buff, "<!-- h1, h2, h3, h4, h5, h6 { margin-bottom: 0.5em; } -->");
        appendLine(buff, "<!-- h5 { margin-top: 0.5em; } -->");
        appendLine(buff, "</style>");
//        appendLine(buff, "<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>");
        appendLine(buff, "</head>");
        
        if (enableLineAnchor) {
            appendLine(buff, "<script>");
            appendLine(buff, "function funcInit() { "
                    + "var pos = window.location.href.lastIndexOf(\"#\"); "
                    + "if (pos >= 0) { var number = window.location.href.substring(pos + 1);"
                    + " document.getElementById(number).style.backgroundColor = \"yellow\"; }}");
            appendLine(buff, "</script>");
            appendLine(buff, "<body onload=\"funcInit()\">");
        } else {
            appendLine(buff, "<body>");
        }
        buff.append("<h5>").append(fileName).append("</h5>");
        appendLine(buff, "<div class=\"separator\">&nbsp;</div>");
        appendLine(buff, "<table cellspacing=\"0\" cellpadding=\"0\" class=\"src\">");
        
        int number = 1;
        while (iterator.hasNext()) {
            String line = cmd.process(iterator.nextLine());
            buff.append("<tr id=\"").append(number).append("\">");
            buff.append("<td class=\"numLine\">&nbsp;").append(number).append("</td>");
            buff.append("<td class=\"src\"><pre class=\"src\">&nbsp;").append(line);
            buff.append("</pre></td>");
            appendLine(buff, "</tr>");
            
            ++number;
        }
        
        appendLine(buff, "</table>");
        appendLine(buff, "</body>");
        
        return buff.toString();
    }

    private void appendLine(StringBuilder buff, String string) {
        buff.append(string).append(BR);
    }

    /**
     * ファイル一覧表示用のindex.htmlファイルを生成します。
     * @param classNames Java完全限定クラス名一覧
     * @throws IOException 
     */
    private void createIndexHtml(Collection<String> classNames) throws IOException {
        
        StringBuilder buff = new StringBuilder();
        appendLine(buff, "<html><body>");
        appendLine(buff, "<h1>Java Source List</h1>");
        
        for (String name : classNames) {
            name = name.substring(0, name.length() - 5);
            String className = name.replaceAll("\\\\", ".");
            className = className.replaceAll("/", ".");
            appendLine(buff, "<a href=\""
                    + name + ".html\">" + className + "</a><br>");
        }
        
        appendLine(buff, "</body></html>");
        FileUtils.writeByteArrayToFile(new File(destDir, "index.html"),
                buff.toString().getBytes());

    }

    // ------------------------ Ant Setter Methods
    
    public void addFileset(FileSet fileSet) {
        fileSets.add(fileSet);
    }

    // ------------------------ Getter/Setter Methods

    /**
     * ソースディレクトリを設定します。
     * @param srcDir ソースディレクトリ
     */
    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    /**
     * 出力先ディレクトリを設定します。
     * @param destDir 出力先ディレクトリ
     */
    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    /**
     * 行番号アンカー指定による行ハイライト機能を有効にするかどうかを設定します。
     * @param enableLineAnchor 行番号アンカー指定による行ハイライト機能を有効にするかどうか
     */
    public void setEnableLineAnchor(boolean enableLineAnchor) {
        this.enableLineAnchor = enableLineAnchor;
    }

    /**
     * ソースファイルの文字セットを設定します。
     * @param inputCharset ソースファイルの文字セット
     */
    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }
    
}
