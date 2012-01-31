/*
 * Created 2007/02/14
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

import java.io.File;
import java.io.IOException;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.io.LimyIOUtils;
import org.limy.eclipse.common.swt.DragScrolledComposite;
import org.limy.eclipse.common.swt.HighlightCanvas;
import org.limy.eclipse.core.LimyEclipsePlugin;

/**
 * イメージを表示するポップアップダイアログです。
 * @author Naoki Iwami
 */
public class GraphPopupDialog extends PopupDialog {

    /**
     * イメージ保存アクション
     * @author Naoki Iwami
     */
    class SaveAction extends Action {

        public SaveAction() {
            super("&Save image...");
        }

        @Override
        public void run() {
            FileDialog dialog = new FileDialog(new Shell(), SWT.SAVE);
            dialog.setFilterExtensions(new String[] { "*.png" });
            String filePath = dialog.open();
            if (filePath != null) {
                try {
                    LimyIOUtils.copyFile(imageFile, new File(filePath));
                } catch (IOException e) {
                    LimyEclipsePluginUtils.log(e);
                }
            }
        }

    }
    
    // ------------------------ Fields

    /** イメージファイル */
    private File imageFile;
    
    /** イメージデータ */
    private ImageData imageData;
    
    /** ハイライト対応キャンバス */
    private HighlightCanvas canvas;

    /** 現在のマウス移動ハンドラ */
    private MouseMoveListener mouseMoveListener;

    /** 現在のマウスクリックハンドラ */
    private MouseListener mouseListener;

    /** 対象Java要素 */
    private IJavaElement targetElement;

    // ------------------------ Constructors

    /**
     * GraphPopupDialogインスタンスを構築します。
     * @param parent シェル
     * @param title タイトル
     * @param info インフォメーション文字列
     * @param imageFile イメージファイル
     * @param targetElement 対象Java要素
     */
    public GraphPopupDialog(Shell parent,
            String title, String info, File imageFile,
            IJavaElement targetElement) {
        
        super(parent, SWT.NONE, true, true, true, false, title, info);
        this.imageFile = imageFile;
        this.targetElement = targetElement;
        
    }

    // ------------------------ Override Methods

    @Override
    protected IDialogSettings getDialogSettings() {
        String sectionName = "limyEclipseQalabGraph";
        IDialogSettings section = LimyEclipsePlugin.getDefault()
                .getDialogSettings().getSection(sectionName);
        if (section == null) {
            section = LimyEclipsePlugin.getDefault()
                    .getDialogSettings().addNewSection(sectionName);
//            section.put(getClass().getName() + "DIALOG_USE_PERSISTED_BOUNDS", true);
        }
        return section;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        
        DragScrolledComposite comp = new DragScrolledComposite(parent);
        canvas = new HighlightCanvas(comp, SWT.NONE);
        comp.setMainComposite(canvas);
        
        imageData = new ImageData(imageFile.getAbsolutePath());
        canvas.setSize(imageData.width, imageData.height);
        canvas.setBackgroundImage(new Image(comp.getDisplay(), imageData));
        
        return comp;
    }
    
    @Override
    protected Point getInitialSize() {
        Point size = new Point(imageData.width + 4, imageData.height + 44 - 14/*info*/);
        if (size.x > 1000) {
            size.x = 1000;
            size.y += 18;
        }
        if (size.y > 800) {
            size.y = 800;
            size.x += 18;
        }
        return size;
    }
    
    @Override
    protected void fillDialogMenu(IMenuManager dialogMenu) {
        super.fillDialogMenu(dialogMenu);
        dialogMenu.add(new SaveAction());
    }
    
    // ------------------------ Public Methods

    /**
     * イメージファイルを変更します。
     * @param newImageFile
     */
    public void changeImageFile(File newImageFile) {
        imageData = new ImageData(imageFile.getAbsolutePath());
        canvas.setSize(imageData.width, imageData.height);
        canvas.setBackgroundImage(new Image(canvas.getDisplay(), imageData));
        initializeBounds();
    }

    /**
     * マウス移動ハンドラを設定します。複数呼び出すと、以前に設定したハンドラは解除されます。
     * @param mouseMoveListener 
     */
    public void setMouseMoveListener(MouseMoveListener mouseMoveListener) {
        if (this.mouseMoveListener != null) {
            canvas.removeMouseMoveListener(this.mouseMoveListener);
        }
        this.mouseMoveListener = mouseMoveListener;
        canvas.addMouseMoveListener(mouseMoveListener);
    }

    /**
     * マウスクリックハンドラを設定します。複数呼び出すと、以前に設定したハンドラは解除されます。
     * @param mouseListener 
     */
    public void setMouseListener(MouseListener mouseListener) {
        if (this.mouseListener != null) {
            canvas.removeMouseListener(this.mouseListener);
        }
        this.mouseListener = mouseListener;
        canvas.addMouseListener(mouseListener);
    }

    // ------------------------ Getter/Setter Methods

    /**
     * イメージデータを取得します。
     * @return イメージデータ
     */
    public ImageData getImageData() {
        return imageData;
    }

    /**
     * ハイライト対応キャンバスを取得します。
     * @return ハイライト対応キャンバス
     */
    public HighlightCanvas getCanvas() {
        return canvas;
    }

    /**
     * 現在のマウスクリックハンドラを取得します。
     * @return 現在のマウスクリックハンドラ
     */
    public MouseListener getMouseListener() {
        return mouseListener;
    }

    /**
     * 対象Java要素を取得します。
     * @return 対象Java要素
     */
    public IJavaElement getTargetElement() {
        return targetElement;
    }

}
