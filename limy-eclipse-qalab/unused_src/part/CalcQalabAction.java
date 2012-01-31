/*
 * Created 2007/06/24
 * Copyright (C) 2003-2007  Naoki Iwami (naoki@limy.org)
 *
 * This file is part of Limy Eclipse Plugin.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.limy.eclipse.qalab.action.part;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import net.objectlab.qalab.ant.BuildStatChartTask;
import net.objectlab.qalab.ant.BuildStatMergeTask;

import org.apache.tools.ant.Project;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.common.LimyEclipseUtils;
import org.limy.eclipse.qalab.AntCreator;
import org.limy.eclipse.qalab.LimyQalabConstants;
import org.limy.eclipse.qalab.ProcessUtils;
import org.limy.eclipse.qalab.ant.LimyCreatorUtils;
import org.limy.xml.VmParam;

/**
 * @author Naoki Iwami
 */
public class CalcQalabAction extends AbstractPartAction {

    @Override
    protected String[] getTargetNames() {
        try {
            exec("clean");
            exec(new CalcCheckstyleAction());
            exec(new CalcPmdAction());
            exec(new CalcFindbugsAction());
            exec(new CalcJavancssAction());
            exec(new CalcCoberturaAction());
            exec(new CalcJDependAction());
            exec(new CalcUmlGraphAction());
            exec(new Java2HtmlAction());
            exec(new CalcTodoAction());
            exec(new MakeJavadocAction());
            exec("qalab");
            exec("qalab-chart");
            exec("qalab-chart-coverage");
            exec("qalab-report-only");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String[] { "finish" };
//        Collection<String> names = new ArrayList<String>();
//        names.add("clean");
//        addNames(names, new CalcCheckstyleAction());
//        addNames(names, new CalcPmdAction());
//        addNames(names, new CalcFindbugsAction());
//        addNames(names, new CalcJavancssAction());
//        addNames(names, new CalcCoberturaAction());
//        addNames(names, new CalcJDependAction());
//        addNames(names, new Java2HtmlAction());
//        addNames(names, new CalcTodoAction());
//        addNames(names, new MakeJavadocAction());
//        names.add("qalab");
//        names.add("qalab-chart");
//        names.add("qalab-chart-coverage");
//        names.add("qalab-report-only");
//        names.add("finish");
//        return names.toArray(new String[names.size()]);
    }

    private void exec(AbstractPartAction action) throws IOException {
        String[] names = action.getTargetNames();
        for (String name : names) {
            exec(name);
        }
    }

    private void exec(String target) throws IOException {
        ProcessUtils.execProgram(getBaseDir(), getWriter(),
                "ant.bat", "-f", "build.xml", target);
    }

    @Override
    protected void makeReport() throws CoreException {

        try {
            execAction(new CalcCheckstyleAction());
            execAction(new CalcPmdAction());
            execAction(new CalcPmdCpdAction());
            execAction(new CalcFindbugsAction());
            execAction(new CalcJavancssAction());
            execAction(new CalcCoberturaAction());
            execAction(new CalcJDependAction());
            execAction(new Java2HtmlAction());
            execAction(new CalcTodoAction());
            execAction(new MakeJavadocAction());
            execAction(new CalcQalabMoverAction());
        } catch (IOException e) {
            LimyEclipseUtils.log(e);
        }
        
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        AntCreator[] creators = LimyCreatorUtils.decideCreators(getEnv());
        for (AntCreator creator : creators) {
            String[] mergeClasses = creator.getQalabClassNames();
            String[] reportNames = creator.getReportXmlNames();
            for (int i = 0; i < mergeClasses.length; i++) {
                BuildStatMergeTask task = new BuildStatMergeTask();
                task.setProject(new Project());
                task.setInputFile(getDestFile(reportNames[i]));
                task.setOutputFile(getQalabFile());
                task.setSrcDir(getAllSrcDir().getAbsolutePath());
                task.setHandler(mergeClasses[i]);
                task.setMergerTimeStamp(time);
                task.execute();
            }
        }

        // 通常
        makeQalabReport(0, "qalab", false, 480, 340); // 個別イメージは小さい画像で
        makeQalabReport(0, "qalab", true, 680, 453); // サマリーイメージは大きい画像で
        
        // カバレッジ
        makeQalabReport(1, "qalab/coverage", false, 480, 340); // 個別イメージは小さい画像で
        makeQalabReport(1, "qalab/coverage", true, 680, 453); // サマリーイメージは大きい画像で
    }

    @Override
    protected File getReportHtml() {
        return getDestFile("qalab/index.html");
    }

    // ------------------------ Private Methods

    private boolean isEnableQalab(AntCreator[] creators) {
        String target = LimyCreatorUtils.createTargetString(creators, null, 0, 1);
        return target.length() > 0;
    }

    private boolean isEnableIndivisual() {
        IPreferenceStore store = getEnv().getStore();
        return store.getBoolean(LimyQalabConstants.ENABLE_INDIVISUAL);
    }
    
    private void addParam(Collection<VmParam> params, String name, String value) {
        params.add(new VmParam(name, value));
    }

    private VmParam[] createParams(Collection<VmParam> params) {
        return params.toArray(new VmParam[params.size()]);
    }

    private void execAction(final AbstractPartAction action)
            throws IOException, CoreException {
        
        action.init(getWindow());
        action.selectionChanged(null, getSelection());
        action.innerExecute(false);
    }
    
    private void makeQalabReport(int targetGroup, String destDir,
            boolean summaryOnly, int width, int height) {
        
        File qalabFile = getQalabFile();

        AntCreator[] creators = LimyCreatorUtils.decideCreators(getEnv());

        BuildStatChartTask chartTask = new BuildStatChartTask();
        chartTask.setInputFile(qalabFile);
        chartTask.setToDir(getDestFile(destDir));
        String type = LimyCreatorUtils.createTypeString(creators, targetGroup);
        chartTask.setType(type);
        chartTask.setSummaryType(type);
        chartTask.setSummaryOnly(summaryOnly);
        chartTask.setWidth(width);
        chartTask.setHeight(height);
        chartTask.execute();

        List<VmParam> params = new ArrayList<VmParam>();
        addParam(params, "projectName", getJavaProject().getProject().getName());
        getDestFile(destDir).mkdirs();

        for (AntCreator creator : creators) {
            int group = creator.getSummaryGroup();
            if (group >= 0) {
                addParam(params, creator.getTargetName(), "on");
            }
            if (group == 0) {
                addParam(params, "enable_qalab", "on");
            }
            if (group == 0 || group == 1) {
                addParam(params, "qalab", "on");
            }
        }

        outputReport("qalab/index.vm", "qalab/index.html", createParams(params));
        outputReport("qalab/menu.vm", "qalab/menu.html", createParams(params));

        if (isEnableQalab(creators)) {
            for (ListIterator<VmParam> it = params.listIterator(); it.hasNext();) {
                VmParam param = it.next();
                if ("qalab".equals(param.getName())) {
                    it.remove();
                }
            }
            outputReport("qalab/qalab.vm", qalabFile,
                    "qalab/qalab.html", createParams(params));
            if (isEnableIndivisual()) {
                addParam(params, "enable_indivisual", "on");
                outputReport("qalab/all_packages.vm", "qalab/all-packages.html",
                        createParams(params));
            }
        }
    }

    private void addNames(Collection<String> names, AbstractPartAction action) {
        names.addAll(Arrays.asList(action.getTargetNames()));
    }
    
}
