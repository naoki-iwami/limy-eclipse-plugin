/*
 * Created 2005/07/21
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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * Javadoc生成支援設定ファイルを表示するダイアログクラスです。
 * @depend - - - JavadocPropUI
 * @author Naoki Iwami
 */
public class JavadocPropDialog extends ApplicationWindow {
    
    // ------------------------ Fields
    
    /**
     * 格納先プロパティファイル
     */
    private File propFile;
    
    /**
     * 上段テーブルビューア
     */
    private TableViewer normalViewer;
    
    /**
     * Javadocカスタマイズ情報Bean
     */
    private LimyJavadocBean javadocBean;

    /**
     * 更新フラグ
     */
    private boolean modified;
    
    // ------------------------ Constructors
    
    /**
     * @param parentShell
     * @param propFile
     */
    public JavadocPropDialog(Shell parentShell, File propFile) {
        super(parentShell);
        this.propFile = propFile;
        modified = false;
    }
    
    // ------------------------ Implement Methods
    
    protected Control createContents(Composite parent) {
        
        getShell().setText("Limy Javadoc Comments Editor");

        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new FormLayout());
        
        try {
            javadocBean = new LimyJavadocBean(getPropFile());
        } catch (IOException e) {
            javadocBean = new LimyJavadocBean();
            LimyEclipsePluginUtils.log(e);
        }
        
        createTable(comp);
        parent.setSize(700, 500);

        return comp;
    }
    
    // ------------------------ Override Methods
    
    @Override
    public boolean close() {
        
        if (modified) {
            String[] buttons = new String[] {
                    IDialogConstants.YES_LABEL,
                    IDialogConstants.NO_LABEL,
                    IDialogConstants.CANCEL_LABEL,
            };
            MessageDialog d = new MessageDialog(
                getShell(), "Save Resourse",
                null,
                MessageFormat.format(
                        "内容が更新されています。''{0}'' ファイルを更新しますか？",
                        getPropFile().getAbsolutePath()),
                MessageDialog.QUESTION, buttons, 0);
            int ret = d.open();
            
            if (ret == 0/*YES*/) {
                try {
                    javadocBean.save(getPropFile());
                } catch (IOException e) {
                    LimyEclipsePluginUtils.log(e);
                }
            }
            if (ret == 2/*CANCEL*/) {
                return false;
            }
        }
        return super.close();
    }
    
    // ------------------------ Getter/Setter Methods

    /**
     * 格納先プロパティファイルを取得します。
     * @return 格納先プロパティファイル
     */
    public File getPropFile() {
        return propFile;
    }

    /**
     * 格納先プロパティファイルを設定します。
     * @param propFile 格納先プロパティファイル
     */
    public void setPropFile(File propFile) {
        this.propFile = propFile;
    }

    /**
     * 上段テーブルビューアを取得します。
     * @return 上段テーブルビューア
     */
    public TableViewer getNormalViewer() {
        return normalViewer;
    }

    /**
     * 上段テーブルビューアを設定します。
     * @param normalViewer 上段テーブルビューア
     */
    public void setNormalViewer(TableViewer normalViewer) {
        this.normalViewer = normalViewer;
    }

    /**
     * Javadocカスタマイズ情報Beanを取得します。
     * @return Javadocカスタマイズ情報Bean
     */
    public LimyJavadocBean getJavadocBean() {
        return javadocBean;
    }

    /**
     * Javadocカスタマイズ情報Beanを設定します。
     * @param javadocBean Javadocカスタマイズ情報Bean
     */
    public void setJavadocBean(LimyJavadocBean javadocBean) {
        this.javadocBean = javadocBean;
    }

    /**
     * 更新フラグを取得します。
     * @return 更新フラグ
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * 更新フラグを設定します。
     * @param modified 更新フラグ
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    // ------------------------ Private Methods

    /**
     * SWTテーブルを生成します。
     * @param comp 親コンポーネント
     */
    private void createTable(Composite comp) {
        
        new JavadocPropUI().createAllComps(this, comp, javadocBean);
    }

}
