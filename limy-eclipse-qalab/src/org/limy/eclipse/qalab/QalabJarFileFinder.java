/*
 * Created 2006/08/11
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
package org.limy.eclipse.qalab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * jarファイル検索用クラスです。
 * @author Naoki Iwami
 */
public class QalabJarFileFinder {

    /**
     * JARファイル格納ディレクトリ一覧
     */
    private final File[] libDirs;
    
    public QalabJarFileFinder() {
        libDirs = new File[] {
                new File(LimyQalabPluginUtils.getPath("resource/lib")),
                new File(LimyQalabPluginUtils.getPath("resource/external-lib")),
                new File(LimyQalabPluginUtils.getPath("resource/external-lib/findbugs/lib")),
                new File(LimyQalabPluginUtils.getPath("resource/external-lib/findbugs/plugin")),
        };
    }
    
    public String getPrefixFileLocation(final String prefix) throws FileNotFoundException {
        for (File libDir : libDirs) {
            File[] files = libDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return Pattern.compile(prefix + "-\\d.*\\.jar$").matcher(name).matches();
                }
            });
            if (files != null && files.length > 0) {
                return files[0].getAbsolutePath();
            }
        }
        throw new FileNotFoundException(prefix + "-XXX.jar が見つかりません。");
    }

    public String getFileLocation(final String fileName) throws FileNotFoundException {
        for (File libDir : libDirs) {
            File[] files = libDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.equals(fileName);
                }
            });
            if (files != null && files.length > 0) {
                return files[0].getAbsolutePath();
            }
        }
        throw new FileNotFoundException(fileName + " が見つかりません。");
    }

}
