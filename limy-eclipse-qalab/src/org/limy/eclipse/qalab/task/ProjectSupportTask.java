/*
 * Created 2007/02/19
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Task;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;
import org.limy.xml.XmlWriteUtils;

/**
 * プロジェクト一覧をXML出力する簡易タスクです。
 * @author Naoki Iwami
 */
public class ProjectSupportTask extends Task {

    // ------------------------ Fields

    /** 出力先 */
    private File out;

    /**
     * プロジェクト一覧
     */
    private List<SimpleParam> projects = new ArrayList<SimpleParam>();

    // ------------------------ Override Methods

    @Override
    public void execute() {
        try {
            createProjectInfoXml();
        } catch (IOException e) {
            log(e.getMessage());
        }
    }

    // ------------------------ Public Methods

    public SimpleParam createProject() {
        SimpleParam p = new SimpleParam();
        projects.add(p);
        return p;
    }
    
    /**
     * 出力先を設定します。
     * @param out 出力先
     */
    public void setOut(File out) {
        this.out = out;
    }
    
    // ------------------------ Private Methods
    
    /**
     * @throws IOException 
     * 
     */
    private void createProjectInfoXml() throws IOException {
        
        XmlElement root = XmlUtils.createElement("root");
        for (SimpleParam project : projects) {
            XmlElement projectEl = XmlUtils.createElement(root, "project");
            projectEl.setAttribute("name", project.getValue());
        }
        
        FileWriter writer = new FileWriter(out);
        try {
            XmlWriteUtils.writeXml(writer, root);
        } finally {
            writer.close();
        }
        
    }
    
}
