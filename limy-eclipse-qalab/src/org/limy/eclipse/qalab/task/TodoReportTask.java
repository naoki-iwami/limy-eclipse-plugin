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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * TODOを検出して出力するAntタスクです。
 * @depend - - - TodoReport
 * @author Naoki Iwami
 */
public class TodoReportTask extends Task {

    // ------------------------ Fields

    /**
     * ソースディレクトリ
     */
    private File srcDir;

    /**
     * 出力先ファイル
     */
    private File outputFile;
    
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
        TodoReport cmd = new TodoReport();
        
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


    // ------------------------ Ant Setter Methods
    
    public void addFileset(FileSet fileSet) {
        fileSets.add(fileSet);
    }

    /**
     * ソースディレクトリを設定します。
     * @param srcDir ソースディレクトリ
     */
    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    /**
     * 出力先ファイルを設定します。
     * @param outputFile 出力先ファイル
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * ソースファイルの文字セットを設定します。
     * @param inputCharset ソースファイルの文字セット
     */
    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    // ------------------------ Private Methods

    /**
     * srcdir指定でコマンドを実行します。
     * @param cmd TODO検出コマンド
     * @throws IOException I/O例外
     */
    private void execWithSrcDir(TodoReport cmd) throws IOException {
        if (srcDir == null) {
            throw new BuildException("srcDir または fileset 要素は必須です。");
        }
        Iterator<File> fileIt = FileUtils.iterateFiles(srcDir, new String[] { "java" }, true);
        while (fileIt.hasNext()) {
            parseFile(cmd, fileIt.next(), srcDir);
        }
        cmd.writeXml(outputFile, "UTF-8");
    }

    /**
     * fileset指定でコマンドを実行します。
     * @param cmd TODO検出コマンド
     * @throws IOException I/O例外
     */
    private void execWithFileset(TodoReport cmd) throws IOException {
        for (FileSet fileSet : fileSets) {
            DirectoryScanner scanner = fileSet.getDirectoryScanner(getProject());
            for (String fileStr : scanner.getIncludedFiles()) {
                File targetBaseDir = fileSet.getDir(getProject());
                File file = new File(targetBaseDir, fileStr);
                parseFile(cmd, file, targetBaseDir);
            }
        }
        cmd.writeXml(outputFile, "UTF-8");
    }

    /**
     * @param cmd TODO検出コマンド
     * @param file 対象ファイル
     * @param baseDir 基準ディレクトリ
     * @throws IOException I/O例外
     */
    private void parseFile(TodoReport cmd, File file, File baseDir) throws IOException {

        log("parse " + file);
        String contents = FileUtils.readFileToString(file, inputCharset);
        
        String relativePath = file.getAbsolutePath().substring(
                baseDir.getAbsolutePath().length() + 1);
        cmd.parseJavaSource(relativePath, contents);
    }
}
