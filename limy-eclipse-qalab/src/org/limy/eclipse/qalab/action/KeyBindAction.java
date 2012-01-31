/*
 * Created 2007/02/21
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
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.CommandManager;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.commands.contexts.ContextManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.BindingManager;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.jface.bindings.keys.KeyBinding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.keys.IBindingService;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.jdt.AbstractUIAction;
import org.limy.eclipse.qalab.keybind.KeybindWindow;

/**
 *
 * @author Naoki Iwami
 */
public class KeyBindAction extends AbstractUIAction {

    @Override
    public void doRun(ISelection selection, IProgressMonitor monitor)
            throws CoreException {
        
        IBindingService bindingService = (IBindingService)
                getWindow().getWorkbench().getService(IBindingService.class);

        KeybindWindow keyWindow = new KeybindWindow(getWindow().getShell(), bindingService);
        keyWindow.setBlockOnOpen(true);
        keyWindow.open();
        Map<Binding, KeySequence> results = keyWindow.getResults();
        if (results != null) {
            
            BindingManager localChangeManager;
            try {
                localChangeManager = createLocalManager(bindingService);
            } catch (NotDefinedException e) {
                throw new WorkbenchException(e.getMessage(), e);
            }
            Binding[] bindings = bindingService.getBindings();

            for (Entry<Binding, KeySequence> entry : results.entrySet()) {
                Binding systemBinding = entry.getKey();
                String contextId = systemBinding.getContextId();
                String schemeId = systemBinding.getSchemeId();
                String commandId = systemBinding.getParameterizedCommand().getId();
                
                // 既存のキーバインドを削除
                for (Binding binding : bindings) {
                    ParameterizedCommand command = binding.getParameterizedCommand();

                    if (command != null && commandId.equals(command.getId())
                            && binding.getType() == Binding.USER) {
                        
                        localChangeManager.removeBindings(binding.getTriggerSequence(),
                                schemeId, contextId,
                                null, null, null, Binding.USER);
                    }
                }

                // キーバインドを登録
                final ParameterizedCommand command = systemBinding.getParameterizedCommand();
                localChangeManager.addBinding(new KeyBinding(entry.getValue(), command,
                        schemeId, contextId, null, null, null, Binding.USER));
                
            }
            
            try {
                bindingService.savePreferences(
                        localChangeManager.getActiveScheme(),
                        localChangeManager.getBindings());
            } catch (IOException e) {
                LimyEclipsePluginUtils.log(e);
            }

        }
        
    }

    // ------------------------ Private Methods

    /**
     * キーバインドのコピーを作成します。
     * @param bindingService
     * @return
     * @throws NotDefinedException
     */
    private BindingManager createLocalManager(IBindingService bindingService)
            throws NotDefinedException {
        BindingManager localChangeManager = new BindingManager(
                new ContextManager(), new CommandManager());
        
        Scheme[] definedSchemes = bindingService.getDefinedSchemes();
        // Make an internal copy of the binding manager, for local changes.
        for (int i = 0; i < definedSchemes.length; i++) {
            Scheme scheme = definedSchemes[i];
            Scheme copy = localChangeManager.getScheme(scheme.getId());
            copy.define(scheme.getName(), scheme.getDescription(),
                    scheme.getParentId());
        }
        localChangeManager.setActiveScheme(bindingService.getActiveScheme());
        
        localChangeManager.setLocale(bindingService.getLocale());
        localChangeManager.setPlatform(bindingService.getPlatform());
        localChangeManager.setBindings(bindingService.getBindings());
        return localChangeManager;
    }

}
