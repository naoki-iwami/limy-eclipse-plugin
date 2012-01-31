/*
 * Created 2009/04/05
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
package org.limy.eclipse.qalab.qaview;


import java.io.File;
import java.io.IOException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.limy.eclipse.common.swt.FormDataCreater;
import org.limy.xml.XmlUtils;
import org.limy.xml.XmlXpathUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class QalabAllView extends ViewPart implements ISelectionListener {

    @Override
    public void createPartControl(Composite parent) {

        Device display = parent.getDisplay();

        parent.setLayout(new FillLayout());
        final TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
        
        TabItem item1 = new TabItem(tabFolder, SWT.NONE);
        item1.setText("Latest");
        Composite latestControl = new Composite(tabFolder, SWT.NONE);
        item1.setControl(latestControl);

        TabItem item2 = new TabItem(tabFolder, SWT.NONE);
        item2.setText("Detail");
        Control detailControl = new Composite(tabFolder, SWT.NONE);
        item2.setControl(detailControl);

        
        latestControl.setLayout(FormDataCreater.createLayout(4, 4));
        Composite comp = latestControl;
        
//        StyledText text1 = new StyledText(comp, SWT.NONE);
        
//        text1.setText("Checkstyle");
//        text1.setFont(new Font(display, "Arias", 16, SWT.NONE));
//        text1.setStyleRange(new StyleRange(0, 10,
//                new Color(display, 0, 0, 255), new Color(display, 255, 255, 255)));
        
        Link link1 = new Link(comp, SWT.NONE);
        link1.setText("<a>Checkstyle</a>");
        link1.setFont(new Font(display, "Arias", 16, SWT.NONE));
        link1.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }
            public void widgetSelected(SelectionEvent e) {
                tabFolder.setSelection(1);
            }
        });
        link1.setLayoutData(FormDataCreater.leftTop(0, 0));
        

//        parent.setBackground(new Color(display, 255, 255, 255));

    }

    @Override
    public void setFocus() {
        // do nothing
    }

    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        // TODO Auto-generated method stub
        
    }

    private void parseXml() throws IOException {
        Element root = XmlUtils.parse(new File("E:\\var\\prog\\eclipse-plugin\\latest\\limy-eclipse-all\\dest\\checkstyle_report.xml"));
        for (Node node : XmlXpathUtils.getNodeList(root, "/checkstyle/file/error")) {
            String name = getAttr(node.getParentNode(), "name");
            String message = getAttr(node, "message");
            int lineNo = Integer.parseInt(getAttr(node, "line"));
            
            
        }
        
    }

    private String getAttr(Node node, String attrName) {
        return node.getAttributes().getNamedItem(attrName).getNodeValue();
    }
    
    public static void main(String[] args) throws IOException {
        new QalabAllView().parseXml();
    }
}
