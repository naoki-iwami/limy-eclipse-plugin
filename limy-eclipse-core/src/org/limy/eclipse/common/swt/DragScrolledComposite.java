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
package org.limy.eclipse.common.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * ドラッグ動作でスクロール可能な ScrolledComposite クラスです。
 * @author Naoki Iwami
 */
public class DragScrolledComposite extends ScrolledComposite {

    /** スクロール速度(X) */
    private static final int SCROLL_X = 6;
    
    /** スクロール速度(Y) */
    private static final int SCROLL_Y = 6;
    
    /** ドラッグ基準位置 */
    private Point basePos = new Point(-1, -1);

    public DragScrolledComposite(Composite parent) {
        super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    }
    
    public void setMainComposite(Composite comp) {
        setContent(comp);
        
        MouseListener ls = new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button == 1) {
                    HighlightCanvas targetCanvas = (HighlightCanvas)e.widget;
                    ScrolledComposite baseComp = (ScrolledComposite)targetCanvas.getParent();
                    Point origin = baseComp.getOrigin();
                    basePos.x = e.x - origin.x;
                    basePos.y = e.y - origin.y;
                }
                super.mouseDown(e);
            }

            @Override
            public void mouseUp(MouseEvent e) {
                if (e.button == 1) {
                    basePos.x = -1;
                }
                super.mouseUp(e);
            }
            
        };
        comp.addMouseListener(ls);
        
        MouseMoveListener listener = new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                
                if (basePos.x < 0) {
                    return;
                }
                
                HighlightCanvas targetCanvas = (HighlightCanvas)e.widget;
                ScrolledComposite baseComp = (ScrolledComposite)targetCanvas.getParent();
                Point origin = baseComp.getOrigin();
                Point viewPos = new Point(e.x - origin.x, e.y - origin.y);
                
                Point newOrigin = new Point(origin.x - (viewPos.x - basePos.x) * SCROLL_X,
                        origin.y - (viewPos.y - basePos.y) * SCROLL_Y);
                newOrigin.x = Math.max(newOrigin.x, 0);
                newOrigin.y = Math.max(newOrigin.y, 0);
                newOrigin.x = Math.min(newOrigin.x,
                        targetCanvas.getSize().x - baseComp.getClientArea().width);
                newOrigin.y = Math.min(newOrigin.y,
                        targetCanvas.getSize().y - baseComp.getClientArea().height);
                
                if (!origin.equals(newOrigin)) {
                    baseComp.setOrigin(newOrigin);
                }
                
                basePos.x = viewPos.x;
                basePos.y = viewPos.y;
                
            }
        };
        comp.addMouseMoveListener(listener);

    }

}
