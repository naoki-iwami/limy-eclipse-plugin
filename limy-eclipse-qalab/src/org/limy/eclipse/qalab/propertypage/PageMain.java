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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jface.CheckedTableObject;
import org.limy.eclipse.common.jface.ITableObject;
import org.limy.eclipse.common.jface.LimyContentProvider;
import org.limy.eclipse.common.jface.TableLabelProvider;
import org.limy.eclipse.common.swt.FormDataCreater;
import org.limy.eclipse.common.swt.GridDataCreator;
import org.limy.eclipse.qalab.LimyQalabNature;
import org.limy.eclipse.qalab.Messages;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * Mainタブページ
 * @depend - - - LimyQalabNatureConfigureJob
 * @author Naoki Iwami
 */
/* protected */ class PageMain extends StoredComposite implements PropertyChangeListener {

    // ------------------------ Fields

    /** プロジェクト環境 */
    private LimyQalabEnvironment env;

    /** プロジェクト */
    private IProject project;

    /** Nature用チェックボックス */
    private Button enableNature;

    /** リソース用SWTリスト */
    private List resourceList;

    /** 現在natureが有効かどうか */
    private boolean hasNature;

    /** 参照プロジェクトリストのTableViewer */
    private CheckboxTableViewer viewer;
    
    // ------------------------ Constructors

    /**
     * PageMainインスタンスを構築します。
     * @param parent
     * @param style
     * @param env 
     */
    public PageMain(Composite parent, int style, LimyQalabEnvironment env) {
        super(parent, style, env.getStore());
        
        this.env = env;
        project = env.getProject();
        
        try {
            createContents(this);
            hasNature = project.hasNature(LimyQalabNature.NATURE_ID);
        } catch (CoreException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }

    // ------------------------ Implement Methods

    public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();
        if (source instanceof Text) {
            Text text = (Text)source;
            Object value = evt.getNewValue();
            if (value instanceof IPath) {
                IPath path = (IPath)value;
                text.setText(path.removeFirstSegments(1).toString());
            }
        }
//        if (source instanceof List) {
//            String value = ((IPath)evt.getNewValue()).removeFirstSegments(1).toString();
//            if (value.length() > 0) {
//                resourceList.add(value);
//            }
//        }
    }

    // ------------------------ Override Methods

    @Override
    public boolean performOk() {
        
        if (hasNature != enableNature.getSelection()) {
            // チェックボックスが変更されたらJobを起動
            LimyQalabNatureConfigureJob job = new LimyQalabNatureConfigureJob(project,
                    Messages.TITLE_JOB_CHANGE);
            job.setRule(ResourcesPlugin.getWorkspace().getRoot());
            job.schedule();
        }

        Collection<CheckedTableObject> values = (Collection<CheckedTableObject>)viewer.getInput();
        StringBuilder buff = new StringBuilder();
        for (CheckedTableObject value : values) {
            if (value.isChecked()) {
                buff.append(value.getValue(0)).append("\n");
            }
        }
        getStore().setValue(LimyQalabConstants.SUB_PROJECT_NAMES, buff.toString());
        
        return super.performOk();
    }

    // ------------------------ Private Methods

    /**
     * @param comp
     * @throws CoreException 
     */
    private void createContents(Composite comp) throws CoreException {
        
        FormLayout layout = new FormLayout();
        layout.marginWidth = 4;
        layout.marginHeight = 4;
        comp.setLayout(layout);
        
        enableNature = new Button(comp, SWT.CHECK);
        enableNature.setText(Messages.LABEL_MARKER);
        enableNature.setSelection(project.hasNature(LimyQalabNature.NATURE_ID));
        enableNature.setLayoutData(FormDataCreater.maxWidthControlDown(null, 0));

        Label label = new Label(comp, SWT.NONE);
        label.setText("Sub projects");
        label.setLayoutData(FormDataCreater.maxWidthControlDown(enableNature, 4));
        
        Table table = new Table(comp, SWT.CHECK);
        viewer = new CheckboxTableViewer(table);
        viewer.setLabelProvider(new TableLabelProvider());
        viewer.setContentProvider(new LimyContentProvider());
        Collection<ITableObject> items = new ArrayList<ITableObject>();
        
        Collection<Object> checkedElements = new ArrayList<Object>();
        String storeNames = getStore().getString(LimyQalabConstants.SUB_PROJECT_NAMES);
        for (IProject refProject : env.getProject().getReferencedProjects()) {
            CheckedTableObject element = new CheckedTableObject(refProject.getName(), refProject);
            items.add(element);
            if (storeNames.indexOf(element.getValue(0) + "\n") >= 0) {
                checkedElements.add(element);
            }
        }
        viewer.setInput(items);
        viewer.setCheckedElements(checkedElements.toArray(new Object[checkedElements.size()]));
        viewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                ((CheckedTableObject)event.getElement()).setChecked(event.getChecked());
            }
        });
        table.setLayoutData(FormDataCreater.maxWidthControlBottom(label, 4, 0, 100));
        
        Composite gridComp = new Composite(comp, SWT.NONE);
        gridComp.setLayout(new GridLayout(3, false));
        gridComp.setLayoutData(FormDataCreater.maxWidthNoHeightControlBottom(table, 8, 0));
        
        createIgnoreResComp(comp, gridComp);
        
    }

    /**
     * @param comp
     * @param targetComp 
     * @throws CoreException 
     */
    private void createIgnoreResComp(Composite comp, Control targetComp) throws CoreException {
        
        Label label = new Label(comp, SWT.NONE);
        label.setText(Messages.LABEL_IGNORE_RESOURCE);
        label.setLayoutData(FormDataCreater.maxWidthControlDown(targetComp, 8));
        
        Composite gridComp = new Composite(comp, SWT.NONE);
        gridComp.setLayout(new GridLayout(2, false));
        gridComp.setLayoutData(FormDataCreater.maxWidthControlBottom(label, 0, 100, 0));

        resourceList = new List(gridComp, SWT.MULTI);
        resourceList.setLayoutData(GridDataCreator.createFillBoth());
        addField(LimyQalabConstants.IGNORE_PACKAGES, resourceList);

        Composite buttonComp = new Composite(gridComp, SWT.NONE);
        buttonComp.setLayoutData(GridDataCreator.createFillVertical());
        
        createButtonComp(buttonComp);

    }

    /**
     * @param comp
     * @throws CoreException 
     */
    private void createButtonComp(Composite comp) throws CoreException {
        
        FormLayout buttonLayout = new FormLayout();
        comp.setLayout(buttonLayout);
        
        Collection<IProject> targetProjects = new ArrayList<IProject>();
        targetProjects.add(project);
        for (IProject refProject : project.getReferencedProjects()) {
            targetProjects.add(refProject);
        }
        
        Button addButton = new Button(comp, SWT.PUSH);
        addButton.setText(Messages.LABEL_ADD);
        addButton.setLayoutData(FormDataCreater.controlDown(null, 0, 140));
        addButton.addSelectionListener(
                new PackageSelector(resourceList, targetProjects, this));

        Button removeButton = new Button(comp, SWT.PUSH);
        removeButton.setText(Messages.LABEL_REMOVE);
        removeButton.setLayoutData(FormDataCreater.controlDown(addButton, 10, 140));
        removeButton.addSelectionListener(new RemoveListSelector(resourceList));

    }


}
