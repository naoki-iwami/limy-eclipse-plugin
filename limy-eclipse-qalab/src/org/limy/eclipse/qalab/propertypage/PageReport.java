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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.swt.FormDataCreater;
import org.limy.eclipse.common.swt.GridDataCreator;
import org.limy.eclipse.qalab.Messages;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * Reportタブページ
 * @author Naoki Iwami
 */
/* protected */ class PageReport extends StoredComposite {
    
    // ------------------------ Fields
    
//    /** Preferenceページ */
//    private final PreferencePage page;

    /** プロジェクト環境 */
    private LimyQalabEnvironment env;

    /** JDepend対象外リソース用SWTリスト */
    private List excludeResourceList;

    /** dotバイナリを必要とするCreatorに対応するチェックボックス */
    private Collection<Button> dotRequiredChecks = new ArrayList<Button>();

    /** dotバイナリパスが変更されたときのvalidチェックリスナ */
    private DotRequiredListener dotRequiredListener;

    // ------------------------ Constructors

    /**
     * Page4インスタンスを構築します。
     * @param page
     * @param parent
     * @param style
     * @param env 
     */
    public PageReport(PreferencePage page,
            Composite parent, int style, LimyQalabEnvironment env) {
        
        super(parent, style, env.getStore());
        this.env = env;
        dotRequiredListener = new DotRequiredListener(page, dotRequiredChecks);
        
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
        
        comp.setLayout(FormDataCreater.createLayout(4, 4));
        
        Button javancss = new Button(comp, SWT.CHECK);
        javancss.setText(Messages.LABEL_JAVANCSS);
        javancss.setLayoutData(FormDataCreater.maxWidthControlDown(null, 4));
        addField(LimyQalabConstants.ENABLE_NCSS, javancss);

        Button todo = new Button(comp, SWT.CHECK);
        todo.setText(Messages.LABEL_TODO);
        todo.setLayoutData(FormDataCreater.maxWidthControlDown(javancss, 4));
        addField(LimyQalabConstants.ENABLE_TODO, todo);

        Control lastControl = createDotComps(comp, todo);

        Button perFile = new Button(comp, SWT.CHECK);
        perFile.setText(Messages.LABEL_ENABLE_INDIVISUAL);
        perFile.setLayoutData(FormDataCreater.maxWidthControlDown(lastControl, 4));
        addField(LimyQalabConstants.ENABLE_INDIVISUAL, perFile);

        Composite gridComp = new Composite(comp, SWT.NONE);
        gridComp.setLayout(new GridLayout(3, false));
        gridComp.setLayoutData(FormDataCreater.maxWidthNoHeightControlBottom(perFile, 8, 0));

        createQalabxmlComp(gridComp);
        createDestComp(gridComp);
        createAntComp(gridComp);
        createAntPropComp(gridComp);
        createBaseJdependPackageComp(gridComp);
        createExcludeJdependComp(comp, gridComp);

    }

    /**
     * Dot関連のコンポーネントを作成します。
     * @param comp 親コンポーネント
     * @param lastControl 直前のコントロール
     * @return 最後に作成したコントロール
     */
    private Control createDotComps(Composite comp, Control lastControl) {
        Composite subComp = new Composite(comp, SWT.NONE);
        subComp.setLayoutData(FormDataCreater.maxWidthControlDown(lastControl, 4));
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        subComp.setLayout(gridLayout);

        Button jdepend = new Button(subComp, SWT.CHECK);
        jdepend.setText("JDepend");
        jdepend.setLayoutData(GridDataCreator.create());
        addField(LimyQalabConstants.ENABLE_JDEPEND, jdepend);
        jdepend.addSelectionListener(dotRequiredListener);
        dotRequiredChecks.add(jdepend);
        
        Link link = new Link(subComp, SWT.NONE);
        link.setText("<a>Configure dot.exe Settings...</a>");
        link.setLayoutData(GridDataCreator.createFillHorizontal(SWT.RIGHT));
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent evt) {
                String id = "org.limy.eclipse.qalab.LimyQalabPreferencePage";
                PreferencesUtil.createPreferenceDialogOn(
                        getShell(), id, new String[] { id }, null).open();
                dotRequiredListener.checkValid();
            }
        });

        Button umlgraph = new Button(subComp, SWT.CHECK);
        umlgraph.setText("UMLGraph");
        umlgraph.setLayoutData(GridDataCreator.create());
        addField(LimyQalabConstants.ENABLE_UMLGRAPH, umlgraph);
        umlgraph.addSelectionListener(dotRequiredListener);
        dotRequiredChecks.add(umlgraph);
                
        return subComp;
    }
    
    /**
     * @param parent
     */
    private void createQalabxmlComp(Composite parent) {
        createTextAndButton(parent, Messages.LABEL_QALAB_XML, LimyQalabConstants.KEY_QALAB_XML);
    }

    /**
     * @param parent
     */
    private void createDestComp(Composite parent) {
        createTextAndButton(parent, Messages.LABEL_DEST_DIR, LimyQalabConstants.KEY_DEST_DIR);
    }

    /**
     * @param parent
     */
    private void createAntComp(Composite parent) {
        createTextComp(parent, Messages.LABEL_BUILD_XML, LimyQalabConstants.KEY_BUILD_XML);
        new Label(parent, SWT.NONE); // ダミー
    }

    /**
     * @param parent
     */
    private void createAntPropComp(Composite parent) {
        createTextComp(parent, Messages.LABEL_BUILD_PROP, LimyQalabConstants.KEY_BUILD_PROP);
        new Label(parent, SWT.NONE); // ダミー
    }

    /**
     * @param parent
     */
    private void createBaseJdependPackageComp(Composite parent) {
        createTextComp(parent, Messages.LABEL_JDEPEND_BASE, LimyQalabConstants.KEY_JDEPEND_BASE);
        new Label(parent, SWT.NONE); // ダミー
    }

    /**
     * @param parent
     * @param labelKey 
     * @param valueKey 
     * @return 
     */
    private Text createTextComp(Composite parent,
            String labelKey, String valueKey) {
        
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelKey);
        label.setLayoutData(GridDataCreator.create());
        
        Text text = new Text(parent, SWT.BORDER);
        text.setLayoutData(GridDataCreator.createFillHorizontal());
        addField(valueKey, text);
        
        return text;
    }

    /**
     * @param comp
     * @param targetComp 
     * @throws CoreException 
     */
    private void createExcludeJdependComp(Composite comp, Control targetComp) throws CoreException {
        
        Label label = new Label(comp, SWT.NONE);
        label.setText(Messages.LABEL_EXCLUDE_JU_PACKAGES);
        label.setLayoutData(FormDataCreater.maxWidthControlDown(targetComp, 8));
        
        Composite gridComp = new Composite(comp, SWT.NONE);
        gridComp.setLayout(new GridLayout(2, false));
        gridComp.setLayoutData(FormDataCreater.maxWidthControlBottom(label, 0, 100, 0));

        excludeResourceList = new List(gridComp, SWT.NONE);
        excludeResourceList.setLayoutData(GridDataCreator.createFillBoth());
        addField(LimyQalabConstants.EXCLUDE_JDEPENDS, excludeResourceList);

        Composite buttonComp = new Composite(gridComp, SWT.NONE);
        buttonComp.setLayoutData(GridDataCreator.createFillVertical());
        
        createButtonComp(buttonComp);

    }
    
    /**
     * @param comp
     * @throws CoreException 
     */
    private void createButtonComp(Composite comp) throws CoreException {
        
        comp.setLayout(new FormLayout());
        
        Button addButton = new Button(comp, SWT.PUSH);
        addButton.setText(Messages.LABEL_ADD);
        addButton.setLayoutData(FormDataCreater.controlDown(null, 0, 140));
        addButton.addSelectionListener(
                new TextInputSelector(excludeResourceList, null));

        Button removeButton = new Button(comp, SWT.PUSH);
        removeButton.setText(Messages.LABEL_REMOVE);
        removeButton.setLayoutData(FormDataCreater.controlDown(addButton, 10, 140));
        removeButton.addSelectionListener(new RemoveListSelector(excludeResourceList));

    }

    /**
     * テキストおよびフォルダ選択ボタンを作成します。
     * @param parent 
     * @param labelKey 
     * @param valueKey 
     */
    private void createTextAndButton(Composite parent, String labelKey, String valueKey) {
        Text text = createTextComp(parent, labelKey, valueKey);
        Button button = new Button(parent, SWT.PUSH);
        button.setText(Messages.LABEL_SELECT_FOLDER);
        button.setLayoutData(GridDataCreator.create());
        button.addSelectionListener(
                new FolderSelector(text, env.getProject(), null));
    }

}
