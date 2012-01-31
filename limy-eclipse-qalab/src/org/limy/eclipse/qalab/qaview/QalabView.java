/*
 * Created 2007/07/01
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
package org.limy.eclipse.qalab.qaview;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jface.LimyContentProvider;
import org.limy.eclipse.common.jface.TableLabelProvider;
import org.limy.eclipse.common.resource.LimyResourceUtils;
import org.limy.xml.XmlUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * QALab結果表示ビューです。
 * @author Naoki Iwami
 */
public class QalabView extends ViewPart implements ISelectionListener {

    // ------------------------ Constants

    /** アイテム表示名一覧 */
    private static final List<String> ITEM_VIEW_NAMES = Arrays.asList(
            "Checkstyle", "Findbugs", "PMD", "PMD CPD", "Javancss", "TODO",
            "Cover Line", "Cover Branch", "JUnit" 
    );
    
    /** アイテム名一覧 */
    private static final List<String> ITEM_NAMES = Arrays.asList(
            "checkstyle", "findbugs", "pmd", "pmd-cpd", "javancss", "todo",
            "cobertura-line", "cobertura-branch", "junit" 
    );

    // ------------------------ Fields

    /** 現在表示中のプロジェクト */
    private IProject targetProject;
    
    /** メインテーブル */
    private Table table;

//    /** テーブルカラム一覧 */
//    private Collection<TableColumn> columns = new ArrayList<TableColumn>();

//    private List<QalabItem> tableItems;

    /** テーブルビューア */
    private TableViewer tableViewer;
    
    // ------------------------ Override Methods

    @Override
    public void setFocus() {
        // do nothing
    }
    
    @Override
    public void createPartControl(Composite parent) {
        
        parent.setLayout(new FillLayout());
        Composite mainComp = new Composite(parent, SWT.NONE);
        mainComp.setLayout(new FillLayout());
//        mainComp.setLayout(new FormLayout());
        
        table = new Table(mainComp, SWT.FULL_SELECTION);
        createTableHeader(table, "Date", 140);
        for (String name : ITEM_VIEW_NAMES) {
            createTableHeader(table, name, 80);
        }

        table.setHeaderVisible(true);
        
        tableViewer = new TableViewer(table);
        tableViewer.setLabelProvider(new TableLabelProvider());
        tableViewer.setContentProvider(new LimyContentProvider());
                
//        table.addListener(SWT.SetData, new Listener() {
//            public void handleEvent(Event e) {
//                TableItem item = (TableItem)e.item;
//                int index = table.indexOf(item);
//                String[] strings = new String[columns.size()];
//                QalabItem qalabItem = tableItems.get(index);
//                strings[0] = qalabItem.getDate().toString();
//                item.setText(strings);
//            }
//        });

    }

    // ------------------------ Private Methods

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        getSite().getPage().addSelectionListener(this);
    }

    private void createTableHeader(Table table, String name, int width) {
        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText(name);
        column.setWidth(width);
//        columns.add(column);
    }

    // ------------------------ Implement Methods

    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        Object element = ((IStructuredSelection)selection).getFirstElement();
        if (!(element instanceof IJavaElement)) {
            return;
        }
        IResource resource = ((IJavaElement)element).getResource();
        IProject project = resource.getProject();
        
        if (!project.equals(targetProject)) {
            try {
                showDatas(project);
            } catch (IOException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
    }
    
    // ------------------------ Private Methods

    private void showDatas(IProject project) throws IOException {
        
        targetProject = project;
        IResource resource = LimyResourceUtils.newFile(project.getFullPath().append("qalab.xml"));
        if (!resource.exists()) {
            return;
        }
        Element root = XmlUtils.parse(resource.getLocation().toFile());
        
        NodeList summaries = root.getElementsByTagName("summary");
        Element summaryEl = (Element)summaries.item(0);
        NodeList summaryResults = summaryEl.getElementsByTagName("summaryresult");
        
        Map<Date, QalabItem> items = new HashMap<Date, QalabItem>();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < summaryResults.getLength(); i++) {
            Element summaryResult = (Element)summaryResults.item(i);
            try {
                Date date = format.parse(summaryResult.getAttribute("date"));
                String value = summaryResult.getAttribute("statvalue");
                String type = summaryResult.getAttribute("type");
                
                QalabItem targetItem = items.get(date);
                if (targetItem == null) {
                    targetItem = new QalabItem(date, ITEM_NAMES);
                    items.put(date, targetItem);
                }
                targetItem.setItem(type, value);
                
            } catch (ParseException e) {
                LimyEclipsePluginUtils.log(e);
                break;
            }
        }
        
        List<QalabItem> tableItems = new ArrayList<QalabItem>();
        List<Date> dates = new ArrayList<Date>();
        dates.addAll(items.keySet());
        Collections.sort(dates);
        Collections.reverse(dates);
        for (Date date : dates) {
            tableItems.add(items.get(date));
        }
        
        tableViewer.setInput(tableItems);
        
//        table.getDisplay().syncExec(new Runnable() {
//            public void run() {
//                if (!table.isDisposed()) {
//                    table.setItemCount(tableItems.size());
//                }
//            }
//        });

    }

}
