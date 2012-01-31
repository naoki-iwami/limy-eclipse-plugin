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

import java.awt.geom.Point2D;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.ui.LimyUIUtils;

/**
 * Java要素用のマウスリスナークラスです。
 * @author Naoki Iwami
 */
public class JavaElementMouseListener extends MouseAdapter {

    /** キャンバス内イメージ */
    private PopupImage image;
    
    /**
     * JDependMouseListener インスタンスを構築します。
     * @param image キャンバス内イメージ
     */
    public JavaElementMouseListener(PopupImage image) {
        super();
        this.image = image;
    }

    @Override
    public void mouseDown(MouseEvent evt) {
        super.mouseDown(evt);
        Point2D.Double point = new Point2D.Double(evt.x, evt.y);
        ClickablePointInfo el = image.getElement(point);
        if (el != null) {
            IJavaElement element = ((JavaElementClickPoint)el).getElement();
            if (element != null) {
                try {
                    LimyUIUtils.openInEditor(element);
                } catch (CoreException e) {
                    LimyEclipsePluginUtils.log(e);
                }
            }
        }
    }

    // ------------------------ Getter/Setter Methods

    /**
     * キャンバス内イメージを設定します。
     * @param image キャンバス内イメージ
     */
    public void setImage(PopupImage image) {
        this.image = image;
    }

}
