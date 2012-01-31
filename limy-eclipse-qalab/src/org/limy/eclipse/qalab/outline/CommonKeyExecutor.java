/*
 * Created 2007/08/30
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
package org.limy.eclipse.qalab.outline;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.ui.LimyUIUtils;

/**
 * 一般的なキーアクション実行クラスです。
 * @author Naoki Iwami
 */
public class CommonKeyExecutor implements KeyExecutor {
    
    /** ダイアログ処理サポート */
    private DialogSupport support;
    
    /** ポップアップダイアログ */
    private GraphPopupDialog dialog;

    /** イメージ作成担当 */
    private ImageCreator creator;
    
    // ------------------------ Constructors

    /**
     * CommonKeyExecutor インスタンスを構築します。
     * @param creator イメージ作成担当
     * @param support ダイアログ処理サポート
     * @param dialog ポップアップダイアログ
     */
    public CommonKeyExecutor(ImageCreator creator, DialogSupport support, GraphPopupDialog dialog) {
        super();
        this.creator = creator;
        this.support = support;
        this.dialog = dialog;
    }
    
    // ------------------------ Implement Methods

    public void execute(char character) throws CoreException {
        switch (character) {
        case 'v':
            pushImageToView();
            break;
        case 'c':
            changeImageDirection();
            break;
        default:
            break;
        }
    }
    
    // ------------------------ Getter/Setter Methods

    /**
     * ポップアップダイアログを取得します。
     * @return ポップアップダイアログ
     */
    public GraphPopupDialog getDialog() {
        return dialog;
    }

    /**
     * ポップアップダイアログを設定します。
     * @param dialog ポップアップダイアログ
     */
    public void setDialog(GraphPopupDialog dialog) {
        this.dialog = dialog;
    }

    // ------------------------ Private Methods

    /**
     * 現在のイメージをQalab Graphビューに表示します。
     * @throws CoreException 
     */
    private void pushImageToView() throws CoreException {
        dialog.close();
        String viewId = "org.limy.eclipse.qalab.graphview.QalabGraphView";
        IGraphView view = (IGraphView)LimyUIUtils.showView(viewId);
        try {
            view.setImageData(creator, dialog);
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }
    }

    private void changeImageDirection() throws CoreException {
        try {
            PopupImage image = support.changeHorizontal();
            dialog.changeImageFile(image.getImageFile());
        } catch (IOException e) {
            LimyEclipsePluginUtils.log(e);
        }

    }
}
