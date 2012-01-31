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
package org.limy.eclipse.qalab.graphview;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.limy.eclipse.common.swt.DragScrolledComposite;
import org.limy.eclipse.common.swt.HighlightCanvas;
import org.limy.eclipse.qalab.outline.BasePopupImage;
import org.limy.eclipse.qalab.outline.CanvasMouseMoveListener;
import org.limy.eclipse.qalab.outline.GraphPopupDialog;
import org.limy.eclipse.qalab.outline.IGraphView;
import org.limy.eclipse.qalab.outline.ImageCreator;
import org.limy.eclipse.qalab.outline.JavaElementMouseListener;

/**
 * Qalab Graph ビュークラスです。
 * @author Naoki Iwami
 */
public class QalabGraphView extends ViewPart implements IGraphView {

    /** メインキャンバス */
    private HighlightCanvas canvas;

    /** マウスクリックListener */
    private JavaElementMouseListener mouseListener;

    /** マウス移動Listener */
    private CanvasMouseMoveListener mouseMoveListener;

    // ------------------------ Implement Methods

    @Override
    public void createPartControl(Composite parent) {
        FillLayout layout = new FillLayout();
        parent.setLayout(layout);
        
        DragScrolledComposite comp = new DragScrolledComposite(parent);

        canvas = new HighlightCanvas(comp, SWT.NONE);
        comp.setMainComposite(canvas);
        
        mouseMoveListener = new CanvasMouseMoveListener(canvas, null);
        canvas.addMouseMoveListener(mouseMoveListener);
        
        mouseListener = new JavaElementMouseListener(null);
        canvas.addMouseListener(mouseListener);

    }

    @Override
    public void setFocus() {
        canvas.setFocus();
    }
    
    // ------------------------ Public Methods
    public void setImageData(ImageCreator creator, GraphPopupDialog dialog)
            throws IOException, CoreException {
        BasePopupImage image = (BasePopupImage)creator.create();
        ImageData imageData = dialog.getImageData();
        canvas.setSize(imageData.width, imageData.height);
        canvas.setBackgroundImage(new Image(canvas.getDisplay(), imageData));
        canvas.setTargetRect(null);
        mouseMoveListener.setImage(image);
        mouseListener.setImage(image);
    }
    
}
