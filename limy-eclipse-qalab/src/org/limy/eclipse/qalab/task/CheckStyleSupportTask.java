/*
 * Created 2007/01/30
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.Task;
import org.limy.eclipse.common.io.LimyIOUtils;

/**
 * Checkstyleの設定ファイルにbasedir情報を付加するためのサポートタスクです。
 * @author Naoki Iwami
 */
public class CheckStyleSupportTask extends Task {

    // ------------------------ Fields

    /** Checkstyle設定ファイル */
    private File configFile;

    /** 出力先 */
    private File destDir;
    
    /** ソースファイルの文字エンコーディング */
    private String encoding;

    // ------------------------ Override Methods

    @Override
    public void execute() {
        try {
            copyCheckstyleFiles();
        } catch (IOException e) {
            log(e.getMessage());
        }
    }
    
    // ------------------------ Getter/Setter Methods

    /**
     * Checkstyle設定ファイルを取得します。
     * @return Checkstyle設定ファイル
     */
    public File getConfigFile() {
        return configFile;
    }

    /**
     * Checkstyle設定ファイルを設定します。
     * @param configFile Checkstyle設定ファイル
     */
    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    /**
     * 出力先を取得します。
     * @return 出力先
     */
    public File getDestDir() {
        return destDir;
    }

    /**
     * 出力先を設定します。
     * @param destDir 出力先
     */
    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }
    
    /**
     * ソースファイルの文字エンコーディングを設定します。
     * @param encoding ソースファイルの文字エンコーディング
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    // ------------------------ Private Methods

    private void copyCheckstyleFiles() throws IOException {
        
        String lines = LimyIOUtils.getContent(configFile);
        
        Matcher matcher = Pattern.compile(".*<module name=\"Checker\">(.*)",
                Pattern.MULTILINE | Pattern.DOTALL).matcher(lines);
        
        if (matcher.matches()) {
            int pos = matcher.start(1);
            StringBuilder buff = new StringBuilder(lines);
            buff.insert(pos, "<property name=\"basedir\" value=\"${src.basedir}\"/>");
            lines = buff.toString();
        }

        Matcher matcherTree = Pattern.compile(".*<module name=\"TreeWalker\">(.*)",
                Pattern.MULTILINE | Pattern.DOTALL).matcher(lines);
        if (matcherTree.matches()) {
            int pos = matcherTree.start(1);
            StringBuilder buff = new StringBuilder(lines);
            buff.insert(pos, "<property name=\"charset\" value=\"${src.encoding}\"/>");
            lines = buff.toString();
        }

        destDir.mkdirs();
        LimyIOUtils.saveFile(new File(destDir, "checkstyle.xml"), lines.getBytes());

        String propertyContents =
                "src.encoding = " + encoding + "\n"
                + "src.basedir = "
                + new File(destDir, "src").getAbsolutePath().replaceAll("\\\\", "\\\\\\\\")
                + "\n";
        LimyIOUtils.saveFile(new File(destDir, "checkstyle.properties"),
                propertyContents.getBytes());

    }

}
