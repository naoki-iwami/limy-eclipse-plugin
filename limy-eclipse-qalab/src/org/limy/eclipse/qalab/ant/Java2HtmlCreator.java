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
package org.limy.eclipse.qalab.ant;

import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;


/**
 * Java2html用のAnt要素を生成するクラスです。
 * @author Naoki Iwami
 */
public class Java2HtmlCreator extends AbstractAntCreator {

    // ------------------------ Implement Methods
    
    public void exec(XmlElement root, LimyQalabEnvironment env)
            throws FileNotFoundException, CoreException {
        
        appendJava2HtmlElement(root, env);
    }
    
    public String[] getQalabClassNames() {
        return new String[0];
    }

    public String[] getReportXmlNames() {
        return new String[0];
    }
    
    public int getSummaryGroup() {
        return 3;
    }

    public String[] getSummaryTypes() {
        return new String[0];
    }

    public String getTargetName() {
        return "java2html";
    }

    public boolean isEnable(IPreferenceStore store) {
        return true;
    }

    public AntCreator newInstance() {
        return new Java2HtmlCreator();
    }

    // ------------------------ Private Methods
    
    private void appendJava2HtmlElement(XmlElement root, LimyQalabEnvironment env)
            throws FileNotFoundException, CoreException {
        
        createTaskdef(root);
        createReportTarget(root, env);
        
    }

    /**
     * taskdef要素を作成します。
     * @param root ルート要素
     * @throws FileNotFoundException 必要なファイルが存在しない場合
     */
    private void createTaskdef(XmlElement root) throws FileNotFoundException {
        XmlElement taskEl = XmlUtils.createElement(root, "taskdef");
        taskEl.setAttribute("name", "java2html");
        taskEl.setAttribute("classname", "org.limy.eclipse.qalab.task.Java2HtmlTask");
                
        XmlElement classEl = XmlUtils.createElement(taskEl, "classpath");

//        addPathElement(classEl, "limy-core.jar");
        addPathElementMyPackage(classEl);
        addPathElement(classEl, "cobertura.jar");
        addPathElementPrefix(classEl, "commons-io");

    }
    
    /**
     * java2htmlレポート出力ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @throws CoreException コア例外
     */
    private void createReportTarget(XmlElement root, LimyQalabEnvironment env)
            throws CoreException {
        
        XmlElement targetEl = createTargetElement(root, "java2html", "init");
        
        XmlElement mainEl = XmlUtils.createElement(targetEl, "java2html");
        mainEl.setAttribute("destdir", "${dest.dir}/javasrc");
        mainEl.setAttribute("enableLineAnchor", "true");
        mainEl.setAttribute("inputCharset", "${src.encoding}");
        
        createFilesetFullSrc(env, mainEl);
    }

}
