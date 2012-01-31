/*
 * Created 2007/07/08
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
package org.limy.eclipse.qalab.action.toolbar;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.PlatformUI;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.qalab.LimyQalabPlugin;

/**
 * 各種計測アクションを定義するプルダウンアクションクラスです。
 * @author Naoki Iwami
 */
public class PulldownAction implements IWorkbenchWindowPulldownDelegate2 {

    // ------------------------ Fields

    /** 作成されたメニュー */
    private Menu menu;

    /** WorkbenchWindow */
    private IWorkbenchWindow window;

    /** Selection */
    private ISelection selection;    

    // ------------------------ Implement Methods

    public Menu getMenu(Menu parent) {
        return null;
    }

    public Menu getMenu(Control parent) {
        
        if (menu == null) {
            menu = new Menu(parent);
            
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
                    PlatformUI.PLUGIN_ID, "actionSets");
            IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
            for (IConfigurationElement element : elements) {
                if ("org.limy.eclipse.qalab.calc".equals(element.getAttribute("id"))) {
                    
                    for (final IConfigurationElement child : element.getChildren()) {
                        
                        ActionContributionItem item = createItem(child);
                        item.fill(menu, 0);               
                    }
                }
            }
        }
        return menu;
    }

    public void dispose() {
        // do nothing
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void run(IAction action) {
        // do nothing
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    // ------------------------ Private Methods
    
    private ActionContributionItem createItem(final IConfigurationElement child) {
        
        ImageRegistry registry = LimyQalabPlugin.getDefault().getImageRegistry();

        ActionContributionItem item = new ActionContributionItem(
                new Action(child.getAttribute("label"),
                        registry.getDescriptor(child.getAttribute("icon"))) {
                    @Override
                    public void run() {
                        try {
                            ToolbarAction action = (ToolbarAction)
                                    Class.forName(child.getAttribute("class")).newInstance();
                            action.init(window);
                            action.selectionChanged(null, selection);
                            action.run(null);
                        } catch (Exception e) {
                            LimyEclipsePluginUtils.log(e);
                        }
                    }
                }
        );
        return item;
    }

}
