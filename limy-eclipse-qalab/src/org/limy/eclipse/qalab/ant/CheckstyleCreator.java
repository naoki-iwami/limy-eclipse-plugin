/*
 * Created 2006/08/19
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
package org.limy.eclipse.qalab.ant;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.tool.CheckstyleTool;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;


/**
 * Checkstyle用のAnt要素を生成するクラスです。
 * @author Naoki Iwami
 */
public class CheckstyleCreator extends AbstractAntCreator {

    // ------------------------ Implement Methods

    public void exec(XmlElement root, LimyQalabEnvironment env)
            throws IOException, JavaModelException {
        
        appendCheckstyleElement(root);
    }
    
    public String[] getQalabClassNames() {
        return new String[] { "net.objectlab.qalab.parser.CheckstyleStatMerge" };
    }

    public String[] getReportXmlNames() {
        return new String[] { "checkstyle_report.xml" };
    }

    public int getSummaryGroup() {
        return 0;
    }

    public String[] getSummaryTypes() {
        return new String[] { "checkstyle" };
    }

    public String getTargetName() {
        return "checkstyle";
    }

    public boolean isEnable(IPreferenceStore store) {
        return store.getBoolean(LimyQalabConstants.ENABLE_CHECKSTYLE);
    }

    public AntCreator newInstance() {
        return new CheckstyleCreator();
    }

    // ------------------------ Private Methods
    
    private void appendCheckstyleElement(XmlElement root)
            throws FileNotFoundException, JavaModelException {
        
        createTaskdef(root);
        createCheckstyleTaskdef(root);
        createMainTarget(root);
        createReportTarget(root);
        
    }

    /**
     * taskdef要素を作成します。
     * @param root ルート要素
     * @throws FileNotFoundException 必要なファイルが存在しない場合
     */
    private void createTaskdef(XmlElement root) throws FileNotFoundException {
        XmlElement taskEl = XmlUtils.createElement(root, "taskdef");
        taskEl.setAttribute("resource", "checkstyletask.properties");
        taskEl.setAttribute("classpath", getPrefixFileLocation("checkstyle-all"));
    }

    /**
     * @param root
     * @throws FileNotFoundException 
     */
    private void createCheckstyleTaskdef(XmlElement root) throws FileNotFoundException {
        
        XmlElement taskEl = createTaskdefElement(root, "checkstyle-support",
                "org.limy.eclipse.qalab.task.CheckStyleSupportTask", null);
        
        XmlElement classEl = XmlUtils.createElement(taskEl, "classpath");
        addPathElementMyPackage(classEl);
        addPathElementCorePackage(classEl);
        addPathElementPrefix(classEl, "commons-logging");
        
    }

    /**
     * Checkstyle計測ターゲットを作成します。
     * @param root ルート要素
     */
    private void createMainTarget(XmlElement root) {
        XmlElement targetEl = createTargetElement(root, "checkstyle", "init");

        XmlElement initEl = XmlUtils.createElement(targetEl, "checkstyle-support");
        initEl.setAttribute("configFile", "${checkstyle.config}");
        initEl.setAttribute("destDir", "${dest.dir}");
        initEl.setAttribute("encoding", "${src.encoding}");

        XmlElement mainEl = XmlUtils.createElement(targetEl, "checkstyle");
//        mainEl.addAttribute("config", "${checkstyle.config}");
        mainEl.setAttribute("config", "${dest.dir}/checkstyle.xml");  
        mainEl.setAttribute("failureProperty", "checkstyle.failure");
        mainEl.setAttribute("failOnViolation", "false");
        mainEl.setAttribute("classpathref", "all.classpath");
        mainEl.setAttribute("properties", "${dest.dir}/checkstyle.properties");

        XmlElement formatterEl = XmlUtils.createElement(mainEl, "formatter");
        formatterEl.setAttribute("type", "xml");
        formatterEl.setAttribute("tofile", "${dest.dir}/checkstyle_report.xml");

        createFilesetAllSrc(mainEl);
    }

    /**
     * Checkstyleレポート出力ターゲットを作成します。
     * @param root ルート要素
     */
    private void createReportTarget(XmlElement root) {
        XmlElement targetEl = createTargetElement(root, "checkstyle-report-only", "init");
        
        createVmstyleElement(targetEl,
                new String[] { "${dest.dir}/checkstyle_report.xml" },
                "${dest.dir}/checkstyle_report.html", "checkstyle/index.vm",
                CheckstyleTool.class.getName());
        
    }

}
