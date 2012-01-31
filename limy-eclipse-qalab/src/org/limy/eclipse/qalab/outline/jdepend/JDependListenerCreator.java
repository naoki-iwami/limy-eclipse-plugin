/*
 * Created 2007/08/31
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
package org.limy.eclipse.qalab.outline.jdepend;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.limy.eclipse.qalab.outline.CanvasMouseMoveListener;
import org.limy.eclipse.qalab.outline.CommonKeyExecutor;
import org.limy.eclipse.qalab.outline.DialogSupport;
import org.limy.eclipse.qalab.outline.GraphPopupDialog;
import org.limy.eclipse.qalab.outline.ImageCreator;
import org.limy.eclipse.qalab.outline.JavaElementMouseListener;
import org.limy.eclipse.qalab.outline.ListenerCreator;
import org.limy.eclipse.qalab.outline.PopupImage;
import org.limy.eclipse.qalab.outline.QalabKeyListener;

/**
 * @author Naoki Iwami
 */
public class JDependListenerCreator implements ListenerCreator {

    /** 表示イメージ */
    private final PopupImage image;
        
    /** ダイアログサポート */
    private final DialogSupport support;

    /** イメージ作成担当 */
    private ImageCreator creator;

    // ------------------------ Constructors

    /**
     * JDependListenerCreator インスタンスを構築します。
     * @param creator 
     * @param image 表示イメージ
     * @param support ダイアログサポート
     */
    public JDependListenerCreator(ImageCreator creator, PopupImage image, DialogSupport support) {
        super();
        this.creator = creator;
        this.image = image;
        this.support = support;
    }


    // ------------------------ Implement Methods

    public KeyListener createKeyListener(GraphPopupDialog dialog) {
        return new QalabKeyListener(new CommonKeyExecutor(creator, support, dialog));
    }

    public MouseListener createMouseListener(GraphPopupDialog dialog) {
        return new JavaElementMouseListener(image);
    }

    public MouseMoveListener createMouseMoveListener(GraphPopupDialog dialog) {
        return new CanvasMouseMoveListener(dialog.getCanvas(), image);
    }
    
}
