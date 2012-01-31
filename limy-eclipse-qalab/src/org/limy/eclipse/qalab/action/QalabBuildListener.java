/*
 * Created 2007/01/08
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
package org.limy.eclipse.qalab.action;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * Ant実行時のリスナークラスです。
 * @author Naoki Iwami
 */
public class QalabBuildListener implements BuildListener {

    /** 改行文字 */
    private static final String BR = System.getProperty("line.separator");

    /** 標準出力内容の格納先（メモリ） */
    private StringBuilder buff = new StringBuilder();
    
    /** 標準出力内容の格納先（ファイル） */
    private FileWriter writer;
    
    // ------------------------ Implement Methods

    public void buildFinished(BuildEvent event) {

        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
    }

    public void buildStarted(BuildEvent event) {
        // do nothing
    }

    public void messageLogged(BuildEvent event) {
        if (event.getPriority() > Project.MSG_VERBOSE) {
            return;
        }
        
        if (writer != null) {
            try {
                writer.write(event.getMessage());
                writer.write(BR);
            } catch (IOException e) {
                // do nothing
            }
        } else {
            buff.append(event.getMessage()).append(BR);
        }
    }

    public void targetFinished(BuildEvent event) {
        // do nothing
    }

    public void targetStarted(BuildEvent event) {
        if (writer == null && event.getSource() instanceof Target) {
            String fileName = ((Target)event.getSource()).getLocation().getFileName();
            File file = new File(fileName).getParentFile();
            try {
                writer = new FileWriter(new File(file, "limy.log"));
                writer.write(buff.toString());
            } catch (IOException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
    }

    public void taskFinished(BuildEvent event) {
        // do nothing
    }

    public void taskStarted(BuildEvent event) {
        // do nothing
    }

}
