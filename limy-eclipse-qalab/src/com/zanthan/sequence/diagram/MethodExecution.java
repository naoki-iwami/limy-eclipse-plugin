/*
 * Created 2009/02/08
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
package com.zanthan.sequence.diagram;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.layout.Painter;

public class MethodExecution extends Item {

    /**
     * The name of the method being executed.
     */
    private String name = null;

    /**
     * List of calls made from this method.
     */
    private List calls = new ArrayList();
    /**
     * The object the method is executing on.
     */
    private ObjectLifeLine objectLifeLine = null;

    private int indent = 0;
    private int halfWidth = 0;
    private int startY = 0;
    private int endY = 0;

    public MethodExecution(ObjectLifeLine objectLifeLine, String name, Object userData) {
        this.objectLifeLine = objectLifeLine;
        this.name = name;
        this.userData = userData;
    }

    public String getName() {
        return name;
    }

    public void addCall(Call call) {
        calls.add(call);
    }

    public Iterator getCalls() {
        return calls.iterator();
    }

    public ObjectLifeLine getObjectLifeLine() {
        return objectLifeLine;
    }

    public ItemIdentifier getIdentifier() {
        return objectLifeLine.getMethodExecutionIdentifier(this);
    }

    public String toString() {
        return "<MethodExecution-" + hashCode() + " on " + objectLifeLine + ">";
    }

    int getCallCount() {
        return calls.size();
    }


    public void layout(LayoutData layoutData) {

        halfWidth = layoutData.getMethodExecutionHalfWidth();

        layoutData.firstVisit(this);

        ObjectLifeLine objectLifeLine = getObjectLifeLine();
        if (layoutData.firstVisit(objectLifeLine)) {
            objectLifeLine.setInitialX(layoutData);
        }

        indent = objectLifeLine.incrementMethodExecutionIndent();

        for (Iterator it = getCalls(); it.hasNext();) {
            Call call = (Call) it.next();
            if (layoutData.firstVisit(call)) {
                call.layout(layoutData);
            }
        }

        objectLifeLine.decrementMethodExecutionIndent();
    }

    public void paint(Painter painter) {
        int minX = getMinX();
        int startY = getStartY();
        painter.paintMethodExecution(new Rectangle(minX, startY, getMaxX() - minX, getEndY() - startY), selected);
    }

    public int getMaxX() {
        return getObjectLifeLine().getCenterX() + halfWidth + (indent * (halfWidth / 2));
    }

    public int getMinX() {
        return getObjectLifeLine().getCenterX() - halfWidth + (indent * (halfWidth / 2));
    }

    boolean toTheRightOf(MethodExecution otherMethodExecution) {
        return getObjectLifeLine().getSeq() > otherMethodExecution.getObjectLifeLine().getSeq();
    }

    void setStartY(int startY) {
        this.startY = startY;
    }

    void setEndY(int endY) {
        this.endY = endY;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndY() {
        return endY;
    }

    public boolean encloses(Point p) {
        if (p.y > getEndY() || p.y < getStartY()) {
            return false;
        }
        if (p.x > getMaxX() || p.x < getMinX()) {
            return false;
        }
        return true;
    }
}
