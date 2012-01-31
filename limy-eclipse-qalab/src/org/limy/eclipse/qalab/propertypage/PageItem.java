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
package org.limy.eclipse.qalab.propertypage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.swt.FormDataCreater;
import org.limy.eclipse.common.swt.GridDataCreator;
import org.limy.eclipse.qalab.Messages;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * Itemsタブページ
 * @author Naoki Iwami
 */
/* protected */ class PageItem extends StoredComposite {
    
    // ------------------------ Fields

    /** プロジェクト */
    private IProject project;

    // ------------------------ Constructors

    /**
     * Page3インスタンスを構築します。
     * @param parent
     * @param style
     * @param env 
     */
    public PageItem(TabFolder parent, int style, LimyQalabEnvironment env) {
        super(parent, style, env.getStore());
        this.project = env.getProject();
        try {
            createContents(this);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }
    
    // ------------------------ Private Methods

    /**
     * @param comp
     * @throws CoreException 
     */
    private void createContents(Composite comp) throws CoreException {
        
        FormLayout layout = FormDataCreater.createLayout(4, 4);
        comp.setLayout(layout);
        
        Button checkstyle = new Button(comp, SWT.CHECK);
        checkstyle.setText(Messages.LABEL_CHECKSTYLE);
        checkstyle.setLayoutData(FormDataCreater.maxWidthControlDown(null, 0));
        addField(LimyQalabConstants.ENABLE_CHECKSTYLE, checkstyle);
        Control targerControl = createSettingFile(
                comp, checkstyle,
                LimyQalabConstants.KEY_CHK_TYPE,
                LimyQalabConstants.KEY_CHK_CFG);

        Button pmd = new Button(comp, SWT.CHECK);
        pmd.setText(Messages.LABEL_PMD);
        pmd.setLayoutData(FormDataCreater.maxWidthControlDown(targerControl, 4));
        addField(LimyQalabConstants.ENABLE_PMD, pmd);
        targerControl = createSettingFile(comp, pmd,
                LimyQalabConstants.KEY_PMD_TYPE,
                LimyQalabConstants.KEY_PMD_CFG);

        Button findbugs = new Button(comp, SWT.CHECK);
        findbugs.setText(Messages.LABEL_FINDBUGS);
        findbugs.setLayoutData(FormDataCreater.maxWidthControlDown(targerControl, 4));
        addField(LimyQalabConstants.ENABLE_FINDBUGS, findbugs);

        Button cobertura = new Button(comp, SWT.CHECK);
        cobertura.setText(Messages.LABEL_COBERTURA);
        cobertura.setLayoutData(FormDataCreater.maxWidthControlDown(findbugs, 4));
        addField(LimyQalabConstants.ENABLE_JUNIT, cobertura);
        
        Composite gridComp = new Composite(comp, SWT.NONE);
        gridComp.setLayout(new GridLayout(3, false));
        gridComp.setLayoutData(FormDataCreater.maxWidthNoHeightControlBottom(cobertura, 4, 0));

//        createLibDirComp(gridComp);

    }

    /**
     * 設定ファイル選択用コンポーネントを作成します。
     * @param parent 親コンポーネント
     * @param lastControl 直前のコントロール
     * @param storeKeyType 種別情報を格納するストアキー
     * @param storeKeyFile ファイル情報を格納するストアキー
     * @return 作成したコンポーネント
     */
    private Control createSettingFile(Composite parent, Control lastControl,
            String storeKeyType, String storeKeyFile) {
        
        Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.LABEL_SETTING_FILE);
        label.setLayoutData(FormDataCreater.controlDownWithWidth(lastControl, 4, 32, 100));
        
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayoutData(FormDataCreater.maxWidthControlDown(
                lastControl, 4, 140));
        FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        fillLayout.spacing = 4;
        comp.setLayout(fillLayout);
        
        Button defaultChk = new Button(comp, SWT.RADIO);
        defaultChk.setText(Messages.LABEL_DEFAULT_FILE);

        Button workspaceChk = new Button(comp, SWT.RADIO);
        workspaceChk.setText(Messages.LABEL_INNER_FILE);

        Button externalChk = new Button(comp, SWT.RADIO);
        externalChk.setText(Messages.LABEL_OUTER_FILE);
        
        addField(storeKeyType, defaultChk, workspaceChk, externalChk);

        Composite gridComp = new Composite(parent, SWT.NONE);
        gridComp.setLayout(new GridLayout(2, false));
        gridComp.setLayoutData(FormDataCreater.maxWidthNoHeightControlBottom(comp, 0, 140));

        Text text = new Text(gridComp, SWT.BORDER);
        text.setLayoutData(GridDataCreator.createFillHorizontal());
        addField(storeKeyFile, text);
        
        Button button = new Button(gridComp, SWT.PUSH);
        button.setText(Messages.LABEL_SELECT_FILE);
        button.setLayoutData(GridDataCreator.create());
        button.addSelectionListener(new MultiTypeFileSelector(
                workspaceChk, text, project, Messages.XML, null));

        return gridComp;
    }
     

//    /**
//     * @param parent
//     */
//    private void createLibDirComp(Composite parent) {
//        
//        Label label = new Label(parent, SWT.NONE);
//        label.setText(Messages.LABEL_TEST_DIR);
//        label.setLayoutData(GridDataCreator.create());
//        
//        Text text = new Text(parent, SWT.BORDER);
//        text.setLayoutData(GridDataCreator.createFillHorizontal());
//        addField(LimyQalabConstants.TEST_LIBDIR, text);
//
//        Button button = new Button(parent, SWT.PUSH);
//        button.setText(Messages.LABEL_SELECT_FOLDER);
//        button.setLayoutData(GridDataCreator.create());
//        button.addSelectionListener(
//                new FolderSelector(text, project, null));
//    }

}
