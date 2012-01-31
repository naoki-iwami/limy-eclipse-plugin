/*
 * Created 2007/02/27
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
package org.limy.eclipse.qalab.action;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Shell;
import org.limy.eclipse.qalab.outline.DialogSupport;
import org.limy.eclipse.qalab.outline.GraphPopupDialog;
import org.limy.eclipse.qalab.outline.ImageCreator;
import org.limy.eclipse.qalab.outline.ListenerCreator;
import org.limy.eclipse.qalab.outline.PopupImage;

/**
 * ポップアップグラフ表示用のヘルパークラスです。
 * @author Naoki Iwami
 */
public final class PopupGraphHelper {
    
    /**
     * private constructor
     */
    private PopupGraphHelper() { }

    public static PopupImage createImage(ImageCreator imageCreator)
            throws CoreException, IOException {
        return imageCreator.create();
    }
    
    /**
     * @param support 
     * @param listenerCreator 
     * @param imageCreator 
     * @param image 
     */
    public static void openDialog(DialogSupport support,
            ListenerCreator listenerCreator, PopupImage image) {
        
        if (image == null) {
            return;
        }
        GraphPopupDialog dialog = new GraphPopupDialog(new Shell(),
                " " + support.getDialogTitle(),
                null, image.getImageFile(),
                support.getTargetElement());

        dialog.open();

        MouseListener mouseListener = listenerCreator.createMouseListener(dialog);
        if (mouseListener != null) {
            dialog.setMouseListener(mouseListener);
        }
        
        MouseMoveListener mouseMoveListener = listenerCreator.createMouseMoveListener(dialog);
        if (mouseMoveListener != null) {
            dialog.setMouseMoveListener(mouseMoveListener);
        }
        
        KeyListener keyListener = listenerCreator.createKeyListener(dialog);
        if (keyListener != null) {
            dialog.getCanvas().addKeyListener(keyListener);
        }
    }

//    /**
//     * @param imageCreator 
//     */
//    public static void openDialog(ImageCreator imageCreator) {
//        
//        try {
//            PopupImage image = imageCreator.init();
//            if (image == null) {
//                return;
//            }
//            GraphPopupDialog dialog = new GraphPopupDialog(new Shell(),
//                    imageCreator,
//                    " " + imageCreator.getDialogTitle(), null,
//                    image.getImageFile(), imageCreator.getTargetElement());
//
//            dialog.open();
//
//            MouseListener mouseListener = imageCreator.createMouseListener(dialog);
//            if (mouseListener != null) {
//                dialog.setMouseListener(mouseListener);
//            }
//            
//            MouseMoveListener mouseMoveListener = imageCreator.createMouseMoveListener(dialog);
//            if (mouseMoveListener != null) {
//                dialog.setMouseMoveListener(mouseMoveListener);
//            }
//            
//            KeyListener keyListener = imageCreator.createKeyListener(dialog);
//            if (keyListener != null) {
//                dialog.getCanvas().addKeyListener(keyListener);
//            }
//            
//        } catch (CoreException e) {
//            LimyEclipsePluginUtils.log(e);
//        } catch (IOException e) {
//            LimyEclipsePluginUtils.log(e);
//        }
//    }
    
    public static void setMouseMoveListener(GraphPopupDialog dialog,
            MouseMoveListener mouseMoveListener) {
        
        dialog.getCanvas().addMouseMoveListener(mouseMoveListener);
    }

}
