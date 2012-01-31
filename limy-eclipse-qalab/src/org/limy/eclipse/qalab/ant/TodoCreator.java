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
package org.limy.eclipse.qalab.ant;

import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.AntCreator;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.xml.XmlElement;
import org.limy.xml.XmlUtils;


/**
 * TODO用のAnt要素を生成するクラスです。
 * @author Naoki Iwami
 */
public class TodoCreator extends AbstractAntCreator {

    public void exec(XmlElement root, LimyQalabEnvironment env)
            throws FileNotFoundException, CoreException {
        
        appendAllElement(root, env);
    }
    
    public String[] getQalabClassNames() {
        return new String[] { "org.limy.eclipse.qalab.parser.TodoStatMerge" };
    }

    public String[] getReportXmlNames() {
        return new String[] { "todo_report.xml" };
    }
    
    public int getSummaryGroup() {
        return 0;
    }

    public String[] getSummaryTypes() {
        return new String[] { "todo" };
    }

    public String getTargetName() {
        return "todo";
    }

    public boolean isEnable(IPreferenceStore store) {
        return store.getBoolean(LimyQalabConstants.ENABLE_TODO);
    }

    public AntCreator newInstance() {
        return new TodoCreator();
    }
    
    // ------------------------ Private Methods

    private void appendAllElement(XmlElement root, LimyQalabEnvironment env)
            throws FileNotFoundException, CoreException {

        createTaskdef(root);
        createMainTarget(root, env);
        createReportTarget(root);
    }

    /**
     * taskdef要素を作成します。
     * @param root ルート要素
     * @throws FileNotFoundException 必要なファイルが存在しない場合
     */
    private void createTaskdef(XmlElement root) throws FileNotFoundException {
        
        XmlElement taskEl = createTaskdefElement(root,
                "todo", "org.limy.eclipse.qalab.task.TodoReportTask", null);

        XmlElement classEl = XmlUtils.createElement(taskEl, "classpath");
        
        addPathElementMyPackage(classEl);
        addPathElementCorePackage(classEl);
        addPathElementPrefix(classEl, "commons-io");
//        addPathElement(classEl, "limy-core.jar");

    }
    
    /**
     * todo計測ターゲットを作成します。
     * @param root ルート要素
     * @param env 
     * @throws CoreException コア例外
     */
    private void createMainTarget(XmlElement root, LimyQalabEnvironment env)
            throws CoreException {
        
        XmlElement targetEl = createTargetElement(root, "todo", "init");

        XmlElement mainEl = XmlUtils.createElement(targetEl, "todo");
        mainEl.setAttribute("outputFile", "${dest.dir}/todo_report.xml");
        mainEl.setAttribute("inputCharset", "${src.encoding}");

        createFilesetFullSrc(env, mainEl);
    }
    
    /**
     * todoレポート出力ターゲットを作成します。
     * @param root ルート要素
     */
    private void createReportTarget(XmlElement root) {
        
        XmlElement targetEl = createTargetElement(root, "todo-report-only", "init");

        XmlElement styleEl = XmlUtils.createElement(targetEl, "vmstyle");
        styleEl.setAttribute("in", "${dest.dir}/todo_report.xml");
        styleEl.setAttribute("out", "${dest.dir}/todo_report.html");
        styleEl.setAttribute("style", LimyQalabPluginUtils.getResourcePath("todo/index.vm"));
    }

}
