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
package org.limy.eclipse.code.preference;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.limy.eclipse.common.jface.LimyContentProvider;
import org.limy.eclipse.common.jface.TableLabelProvider;
import org.limy.eclipse.common.swt.FormDataCreater;

/**
 * Javadoc生成支援設定ファイルダイアログのUIを支援するクラスです。
 * @depend - - - LimyContentProvider
 * @depend - - - JavadocCellEditor
 * @author Naoki Iwami
 */
public class JavadocPropUI {

    /**
     * ダイアログのコンポーネントを初期化します。
     * @param dialog 親ダイアログ
     * @param parent 親コンポーネント
     * @param javadocBean Javadocカスタマイズ情報
     */
    public void createAllComps(final JavadocPropDialog dialog,
            Composite parent,
            LimyJavadocBean javadocBean) {
        
        Table table = new Table(parent, SWT.BORDER | SWT.MULTI
                | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
        table.setLayoutData(FormDataCreater.maxWidth(0, 0, 100, -24));
        table.setHeaderVisible(true);

        createTableColumns(table);
        
        TableViewer viewer = new TableViewer(table);
        viewer.setContentProvider(new LimyContentProvider());
        viewer.setLabelProvider(new TableLabelProvider());
        
        viewer.setInput(javadocBean.getNormalValues());
        
        CellEditor[] editors = new CellEditor[4];
        TextCellEditor textEditor = new TextCellEditor(table);
        editors[0] = new ComboBoxCellEditor(table, Type.getStrings(), SWT.READ_ONLY);
        editors[1] = textEditor;
        editors[2] = textEditor;
        editors[3] = textEditor;
        viewer.setCellEditors(editors);

        dialog.setNormalViewer(viewer);
        
        createAddButton(dialog, parent, table);
        createDelButton(dialog, parent, table);

        viewer.setCellModifier(new LimyCellModifier(new LimyModifierTarget() {
            public void update(Object element, String[] properties) {
                dialog.getNormalViewer().update(element, properties);
            }
            public void setModified(boolean b) {
                dialog.setModified(b);
            }
        }));
        viewer.setColumnProperties(new String[] { "0", "1", "2", "3" });
    }

    // ------------------------ Private Methods

    /**
     * Delボタンを作成します。
     * @param dialog 親ダイアログ
     * @param parent 親コンポーネント
     * @param table ターゲットテーブル
     */
    private void createDelButton(final JavadocPropDialog dialog,
            Composite parent, Table table) {
        
        Button delButton = new Button(parent, SWT.NONE);
        delButton.setText("Delete");
        delButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int[] indices = dialog.getNormalViewer().getTable().getSelectionIndices();
                List<Object> list = (List<Object>)dialog.getNormalViewer().getInput();
                for (int i = indices.length - 1; i >= 0; i--) {
                    list.remove(dialog.getNormalViewer().getElementAt(indices[i]));
                }
                dialog.getNormalViewer().setInput(list);
                dialog.setModified(true);
            }
        });
        delButton.setLayoutData(FormDataCreater.controlBottom(70, 100, table, 2, 100, -2));
    }

    /**
     * Addボタンを作成します。
     * @param dialog 親ダイアログ
     * @param parent 親コンポーネント
     * @param table ターゲットテーブル
     */
    private void createAddButton(final JavadocPropDialog dialog,
            Composite parent, Table table) {
        
        Button addButton = new Button(parent, SWT.NONE);
        addButton.setText("New Addition");
        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                JavadocTableObject obj = new JavadocTableObject(Type.CLAZZ,
                        "**CLASS_NAME**", "", "**COMMENT**");
                List<Object> list = (List<Object>)dialog.getNormalViewer().getInput();
                list.add(obj);
                dialog.getNormalViewer().setInput(list);
                dialog.setModified(true);
            }
        });
        addButton.setLayoutData(FormDataCreater.controlBottom(0, 30, table, 2, 100, -2));
    }

    /**
     * テーブルカラムを生成します。
     * @param table SWTテーブル
     */
    private void createTableColumns(Table table) {
        TableColumn colType = new TableColumn(table, SWT.LEFT);
        colType.setText("Type");
        colType.setWidth(100);

        TableColumn colClass = new TableColumn(table, SWT.LEFT);
        colClass.setText("Class Name");
        colClass.setWidth(200);
        
        TableColumn calName = new TableColumn(table, SWT.LEFT);
        calName.setText("Field Name");
        calName.setWidth(100);
        
        TableColumn colComment = new TableColumn(table, SWT.LEFT);
        colComment.setText("Javadoc Comment");
        colComment.setWidth(200);
    }


}
