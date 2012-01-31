/*
 * Created 2007/01/15
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
package org.limy.eclipse.qalab.ui;


import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jface.TableLabelProvider;
import org.limy.eclipse.common.swt.FormDataCreater;
import org.limy.eclipse.common.swt.SwtTableUtils;
import org.limy.eclipse.common.ui.LimyUIUtils;
import org.limy.eclipse.core.LimyEclipsePlugin;
import org.limy.eclipse.qalab.LimyQalabMarker;
import org.limy.eclipse.qalab.LimyQalabPluginUtils;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.tester.FailureItem;
import org.limy.eclipse.qalab.tester.ProjectTestResult;

/**
 * テスト結果表示用ビュークラスです。
 * @depend - - - TestResultListener
 * @author Naoki Iwami
 */
public class TestResultView extends ViewPart {

    /** テーブルビューア */
    private TableViewer viewer;
    
    /** プロジェクト単位のテスト結果 */
    private Collection<ProjectTestResult> initializeDatas;

    /** ターゲット表示用ラベル */
    private Label targetLabel;

    /** カバレッジ表示用ラベル */
    private Label coverageLabel;

    // ------------------------ Override Methods

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        
        initializeDatas = new ArrayList<ProjectTestResult>();
        try {
            IResource[] projects = LimyEclipsePlugin.getWorkspace().getRoot().members();
            for (IResource project : projects) {
                if (!((IProject)project).isOpen()) {
                    continue;
                }
                LimyQalabEnvironment env = LimyQalabPluginUtils.createEnv((IProject)project);
                
                ProjectTestResult result = new ProjectTestResult((IProject)project);
                IMarker[] markers = project.findMarkers(
                        LimyQalabMarker.FAILURE_ID, true, IResource.DEPTH_INFINITE);
                for (IMarker marker : markers) {
                    
                    Integer lineNumber = (Integer)marker.getAttribute(IMarker.LINE_NUMBER);
                    String message = (String)marker.getAttribute(IMarker.MESSAGE);
                    boolean isError = LimyQalabMarker.ERROR_ID.equals(marker.getType());
                    String testName = (String)marker.getAttribute(
                            LimyQalabMarker.TEST_NAME);
                    
                    String className = LimyQalabUtils.getQualifiedClassName(
                            env, marker.getResource());
                    
                    result.addItem(new FailureItem(className, testName,
                            marker.getResource(),
                            isError, lineNumber.intValue(), message));
                }
                initializeDatas.add(result);
            }
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
    }

    @Override
    public void createPartControl(Composite parent) {
        
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new FormLayout());
        
        Composite upperComp = new Composite(comp, SWT.BORDER);
        upperComp.setLayoutData(FormDataCreater.maxWidthNoHeightControlBottom(null, 0, 0));
        
        upperComp.setLayout(new FormLayout());
        
        Label label1 = new Label(upperComp, SWT.NONE);
        label1.setText("Target : ");
        label1.setLayoutData(FormDataCreater.controlRightPercentage(null, 0, 10));

        targetLabel = new Label(upperComp, SWT.NONE);
        targetLabel.setLayoutData(FormDataCreater.controlRightPercentage(null, 10, 30));

        Label label2 = new Label(upperComp, SWT.NONE);
        label2.setText("Coverage : ");
        label2.setLayoutData(FormDataCreater.controlRightPercentage(null, 30, 40));

        coverageLabel = new Label(upperComp, SWT.NONE);
        coverageLabel.setLayoutData(FormDataCreater.controlRightPercentage(null, 40, 100));

        Composite downComp = new Composite(comp, SWT.NONE);
        downComp.setLayoutData(FormDataCreater.maxWidthControlBottom(upperComp, 100));
        downComp.setLayout(new FillLayout());

        createTable(downComp);
        
        getSite().getPage().addSelectionListener(
                new TestResultListener(targetLabel, coverageLabel));

    }
    
    @Override
    public void setFocus() {
        // do nothing
    }
    
    // ------------------------ Public Methods

    /**
     * テーブルビューアを取得します。
     * @return テーブルビューア
     */
    public TableViewer getTableViewer() {
        return viewer;
    }

    // ------------------------ Private Methods

    /**
     * テーブルコンポーネントを作成します。
     * @param parent 親コンポーネント
     */
    private void createTable(Composite parent) {
        Table table = new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        
        SwtTableUtils.addTableHeader(table, SWT.CENTER, "", 16);
        SwtTableUtils.addTableHeader(table, SWT.LEFT, "Description", 450);
        SwtTableUtils.addTableHeader(table, SWT.LEFT, "Resource", 100);
        SwtTableUtils.addTableHeader(table, SWT.LEFT, "Path", 140);
        SwtTableUtils.addTableHeader(table, SWT.LEFT, "Location", 80);

        viewer = new TableViewer(table);
        viewer.setLabelProvider(new TableLabelProvider());
        viewer.setContentProvider(new QalabContentProvider());
        
        for (ProjectTestResult result : initializeDatas) {
            viewer.setInput(result);
        }
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
                FailureItem item = (FailureItem)selection.getFirstElement();
                if (item != null) {
                    LimyUIUtils.openFile((IFile)item.getResource(), item.getLineNumber());
                }
            }
        });
    }


}
