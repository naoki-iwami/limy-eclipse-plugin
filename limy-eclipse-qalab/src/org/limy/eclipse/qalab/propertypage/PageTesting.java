/*
 * Created 2007/02/25
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jface.LimyContentProvider;
import org.limy.eclipse.common.swt.FormDataCreater;
import org.limy.eclipse.common.swt.GridDataCreator;
import org.limy.eclipse.qalab.Messages;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * Testタブページ
 * @author Naoki Iwami
 */
/* package */ class PageTesting extends StoredComposite {
    
    // ------------------------ Fields

    /** プロジェクト */
    private IProject project;
  
    /** リストビューア */
    private ListViewer listViewer;
    
//    /** リソース用SWTリスト */
//    private List resourceList;

    // ------------------------ Constructors

    public PageTesting(Composite parent, int style, LimyQalabEnvironment env) {
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
        
        comp.setLayout(FormDataCreater.createLayout(4, 4));
        
        // テスト用ライブラリ格納ディレクトリ
        Composite gridComp = new Composite(comp, SWT.NONE);
        gridComp.setLayout(new GridLayout(3, false));
        gridComp.setLayoutData(FormDataCreater.maxWidthNoHeightControlBottom(null, 4, 0));
        createLibDirComp(gridComp);
        
//        // テスト時に除外するソースディレクトリ
//        Composite resComp = createIgnoreResComp(comp, gridComp);
        
        // テスト時に設定する環境変数
        Composite envComp = new Composite(comp, SWT.NONE);
        envComp.setLayout(FormDataCreater.createLayout(4, 4));
        envComp.setLayoutData(FormDataCreater.controlBottom(0, 100, gridComp, 0, 100, 0));
//        envComp.setLayoutData(FormDataCreater.controlBottom(0, 100, resComp, 0, 100, 0));
        createEnvComp(envComp, null);

    }
    
    /**
     * @param comp
     * @param targetComp 
     * @throws CoreException 
     */
    private void createEnvComp(Composite comp, Control targetComp) throws CoreException {
        
        Label label = new Label(comp, SWT.NONE);
        label.setText(Messages.LABEL_TESTENV);
        label.setLayoutData(FormDataCreater.maxWidthControlDown(targetComp, 8));
        
        Composite gridComp = new Composite(comp, SWT.NONE);
        gridComp.setLayout(new GridLayout(2, false));
        gridComp.setLayoutData(FormDataCreater.maxWidthControlBottom(label, 0, 100, 0));

        List envList = new List(gridComp, SWT.NONE);
        envList.setLayoutData(GridDataCreator.createFillBoth());

        listViewer = new ListViewer(envList);
        listViewer.setContentProvider(new LimyContentProvider());
        listViewer.setLabelProvider(new LabelProvider());

        addNameValueList(LimyQalabConstants.TEST_ENVS, listViewer);

        Composite buttonComp = new Composite(gridComp, SWT.NONE);
        buttonComp.setLayoutData(GridDataCreator.createFillVertical());
        
        createButtonComp(buttonComp, envList);

    }
    
    /**
     * @param comp
     * @param envList 
     * @throws CoreException 
     */
    private void createButtonComp(Composite comp, List envList) throws CoreException {
        
        comp.setLayout(new FormLayout());
        
        Button addButton = new Button(comp, SWT.PUSH);
        addButton.setText(Messages.LABEL_ADD);
        addButton.setLayoutData(FormDataCreater.controlDown(null, 0, 140));
        
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                NameValue nameValue = (NameValue)evt.getNewValue();
                listViewer.add(nameValue);
            }
        };
        addButton.addSelectionListener(
                new ValueInputSelector(envList, listener));

        Button removeButton = new Button(comp, SWT.PUSH);
        removeButton.setText(Messages.LABEL_REMOVE);
        removeButton.setLayoutData(FormDataCreater.controlDown(addButton, 10, 140));
        removeButton.addSelectionListener(new RemoveListSelector(envList));

    }
    
    /**
     * @param parent
     */
    private void createLibDirComp(Composite parent) {
        
        Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.LABEL_TEST_DIR);
        label.setLayoutData(GridDataCreator.create());
        
        Text text = new Text(parent, SWT.BORDER);
        text.setLayoutData(GridDataCreator.createFillHorizontal());
        addField(LimyQalabConstants.TEST_LIBDIR, text);

        Button button = new Button(parent, SWT.PUSH);
        button.setText(Messages.LABEL_SELECT_FOLDER);
        button.setLayoutData(GridDataCreator.create());
        button.addSelectionListener(
                new FolderSelector(text, project, null));
    }

//    /**
//     * @param comp
//     * @param targetComp 
//     * @return 
//     * @throws CoreException 
//     */
//    private Composite createIgnoreResComp(Composite comp, Control targetComp) throws CoreException {
//        
//        Label label = new Label(comp, SWT.NONE);
//        label.setText(Messages.LABEL_IGNORE_SOURCE_DIRS);
//        label.setLayoutData(FormDataCreater.maxWidthControlDown(targetComp, 8));
//        
//        Composite gridComp = new Composite(comp, SWT.NONE);
//        gridComp.setLayout(new GridLayout(2, false));
//        gridComp.setLayoutData(FormDataCreater.maxWidthControlBottom(label, 0, 50, 0));
//
//        resourceList = new List(gridComp, SWT.MULTI);
//        resourceList.setLayoutData(GridDataCreator.createFillBoth());
//        addField(LimyQalabConstants.IGNORE_SOURCE_DIRS, resourceList);
//
//        Composite buttonComp = new Composite(gridComp, SWT.NONE);
//        buttonComp.setLayoutData(GridDataCreator.createFillVertical());
//        
//        createButtonComp(buttonComp);
//        return gridComp;
//    }

//    /**
//     * @param comp
//     * @throws CoreException 
//     */
//    private void createButtonComp(Composite comp) throws CoreException {
//        
//        FormLayout buttonLayout = new FormLayout();
//        comp.setLayout(buttonLayout);
//        
//        Collection<IProject> targetProjects = new ArrayList<IProject>();
//        targetProjects.add(project);
//        for (IProject refProject : project.getReferencedProjects()) {
//            targetProjects.add(refProject);
//        }
//        
//        Button addButton = new Button(comp, SWT.PUSH);
//        addButton.setText(Messages.LABEL_ADD);
//        addButton.setLayoutData(FormDataCreater.controlDown(null, 0, 140));
//        addButton.addSelectionListener(
//                new SourceDirSelector(resourceList, targetProjects, null));
//
//        Button removeButton = new Button(comp, SWT.PUSH);
//        removeButton.setText(Messages.LABEL_REMOVE);
//        removeButton.setLayoutData(FormDataCreater.controlDown(addButton, 10, 140));
//        removeButton.addSelectionListener(new RemoveListSelector(resourceList));
//
//    }

}
