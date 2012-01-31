/*
 * Created 2007/08/29
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
package org.limy.eclipse.qalab.outline.jdepend;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import jdepend.framework.PackageFilter;
import jdepend.xmlui.JDepend;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.limy.eclipse.common.io.LimyIOUtils;
import org.limy.eclipse.common.ui.LimyUIUtils;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.ClickableXmlParser;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabJavaUtils;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.common.QalabPackageFilter;
import org.limy.eclipse.qalab.outline.BasePopupImage;
import org.limy.eclipse.qalab.outline.GraphvizUtils;
import org.limy.eclipse.qalab.outline.ImageCreator;
import org.limy.eclipse.qalab.outline.JavaElementClickPoint;
import org.limy.eclipse.qalab.outline.PopupImage;
import org.limy.eclipse.qalab.outline.SvgParser;
import org.limy.eclipse.qalab.task.TaskSupportUtils;
import org.limy.velocity.task.VmStyleTask;

/**
 * パッケージ依存関係図作成を担当するクラスです。
 * @author Naoki Iwami
 */
public class JdependImageCreator implements ImageCreator {

    // ------------------------ Fields

    /** 環境設定 */
    private final LimyQalabEnvironment env;
    
    /** 対象要素 */
    private final IJavaElement targetElement;
    
    /** ポップアップイメージ */
    private final BasePopupImage image;

    /** SVG文字列パーサ */
    private ClickableXmlParser parser;

    /**
     * JdependImageCreatorインスタンスを構築します。
     * @param env
     * @param targetElement 
     */
    public JdependImageCreator(LimyQalabEnvironment env, IJavaElement targetElement) {
        super();
        this.env = env;
        this.targetElement = targetElement;
        image = new BasePopupImage();
    }

    // ------------------------ Implement Methods


    public PopupImage create() throws CoreException, IOException {
        
        IPreferenceStore store = env.getStore();
        String dotExe = store.getString(LimyQalabConstants.KEY_DOT_EXE);
        if (dotExe.length() == 0) {
            LimyUIUtils.showConfirmDialog("dot.exeの場所が指定されていません。\n"
                    + "プロジェクトのプロパティページから QALab -> Report のタブを選択して"
                    + "'dotバイナリ'の欄から設定できます。");
            return null;
        }

        File dotDir = LimyQalabUtils.createTempFile(env.getProject(), "dest/jdepend");
        dotDir.mkdirs();

        StringWriter buff = new StringWriter();
        PrintWriter writer = new PrintWriter(buff);
        
        JDepend jdepend = new JDepend(writer);
        String directory = LimyQalabJavaUtils.getBinDirPath(targetElement);
        if (directory == null) {
            return null;
        }
        jdepend.addDirectory(directory);
        
        PackageFilter packageFilter = new QalabPackageFilter(env);
        String resources = store.getString(LimyQalabConstants.EXCLUDE_JDEPENDS);
        String[] excludes = resources.split("\n");
        for (String exclude : excludes) {
            packageFilter.addPackage(exclude);
        }
        jdepend.setFilter(packageFilter);

        jdepend.analyze();
        LimyIOUtils.saveFile(new File(dotDir, "popup.xml"), buff.toString().getBytes());
        writer.close();
        
        VmStyleTask task = new VmStyleTask();
        task.setOut(new File(dotDir, "popup.dot"));
        task.setStyle(new File(LimyQalabPluginUtils.getResourcePath("jdepend_graph/index.vm")));
        task.setIn(new File(dotDir, "popup.xml"));
        
        TaskSupportUtils.addParam(task, "packagePrefix",
                env.getStore().getString(LimyQalabConstants.KEY_JDEPEND_BASE));

        TaskSupportUtils.addParam(task, "horizontal",
                Boolean.valueOf(image.isHorizontal()));

        task.execute();
        
        GraphvizUtils.creteImageFile(
                env, new File(dotDir, "popup.dot"), dotDir, image);
        
        parser = new JDependSvgParser();
        image.setElements(SvgParser.makePointElementInfos(
                image.getSvgImageFile(),
                new JavaElementClickPoint.Creator(env.getJavaProject(), parser)
        ));
        
        return image;
    }

    public PopupImage changeLocation() throws CoreException, IOException {
        image.setHorizontal(!image.isHorizontal());
        create();
        return image;
    }

}
