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
package org.limy.eclipse.qalab.keybind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeySequenceText;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.keys.IBindingService;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.swt.FormDataCreater;

/**
 * キーバインド変更用のウィンドウクラスです。
 * @author Naoki Iwami
 */
public class KeybindWindow extends Window {
    
    // ------------------------ Fields

    /** キーバインドService */
    private final IBindingService bindingService;
    
    /** テキストコンポーネント一覧 */
    private List<Text> texts = new ArrayList<Text>();

    /** キーシーケンス用テキストコンポーネント一覧 */
    private List<KeySequenceText> sequenceTexts = new ArrayList<KeySequenceText>();

    /** キーシーケンス変更内容 */
    private Map<Binding, KeySequence> sequences;

    // ------------------------ Constructors

    /**
     * KeybindWindowインスタンスを構築します。
     * @param parentShell
     * @param bindingService 
     */
    public KeybindWindow(Shell parentShell, IBindingService bindingService) {
        super(parentShell);
        this.bindingService = bindingService;
    }

    // ------------------------ Public Methods

    public Map<Binding, KeySequence> getResults() {
        return sequences;
    }
    
    // ------------------------ Override Methods

    @Override
    protected Point getInitialSize() {
        return new Point(420, 300);
    }

    @Override
    protected Control createContents(Composite parent) {

        parent.setLayout(new FillLayout());
        Composite comp = new Composite(parent, SWT.BORDER);
        comp.setLayout(FormDataCreater.createLayout(4, 4));

        Control lastTarget = null;
        
        String[] commandIds = new String[] {
                "org.limy.eclipse.qalab.action.MakeMarkerAction",
                "org.limy.eclipse.qalab.action.ResetMarkerAction",
                "org.limy.eclipse.qalab.action.CreateBuildXmlAction",
//                "org.limy.eclipse.qalab.action.CreateReportAction",
                "org.limy.eclipse.qalab.action.ViewReportAction",
                "org.limy.eclipse.qalab.action.ViewGraphAction",
                "org.limy.eclipse.qalab.action.KeyBindAction",
        };

        try {
            for (String commandId : commandIds) {
                lastTarget = createCommandComp(comp, commandId, lastTarget);
            }
        } catch (NotDefinedException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
        Button cancelButton = new Button(comp, SWT.PUSH);
        cancelButton.setText("Cancel");
        cancelButton.setLayoutData(FormDataCreater.bottomAttach(80));
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                close();
            }
        });

        Button okButton = new Button(comp, SWT.PUSH);
        okButton.setText("OK");
        okButton.setLayoutData(FormDataCreater.controlLeft(cancelButton, 4, 80));
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                changeKeybind();
                close();
            }
        });

        getShell().setText("キーバインド変更");
        return comp;
    }

    /**
     * @param comp
     * @param commandId 
     * @param commandBinding 
     * @param commandName
     * @param lastTarget
     * @return 
     * @throws NotDefinedException 
     */
    private Control createCommandComp(Composite comp, String commandId,
            Control lastTarget) throws NotDefinedException {
        
        Binding[] bindings = bindingService.getBindings();
        
        Binding systemBinding = null;
        Collection<Binding> userBindings = new ArrayList<Binding>();
        for (Binding binding : bindings) {
            ParameterizedCommand command = binding.getParameterizedCommand();
            
            if (command != null && commandId.equals(command.getId())) {
                
                if (binding.getType() == Binding.SYSTEM) {
                    systemBinding = binding;
                } else {
                    userBindings.add(binding);
                }
            }
        }

        Label label = new Label(comp, SWT.NONE);
        label.setText(systemBinding.getParameterizedCommand().getCommand().getDescription());
        label.setLayoutData(FormDataCreater.controlDown(lastTarget, 6, 280));
        label.setFont(new Font(comp.getDisplay(), "Serif", 10, SWT.NORMAL));

        Text text = new Text(comp, SWT.BORDER);
        text.setLayoutData(FormDataCreater.controlDownWithWidth(lastTarget, 4, 288, 100));
        text.setData(systemBinding);
        
        text.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                bindingService.setKeyFilterEnabled(false);
            }

            public void focusLost(FocusEvent e) {
                bindingService.setKeyFilterEnabled(true);
            }
        });

        KeySequenceText sequenceText = new KeySequenceText(text);
        if (!userBindings.isEmpty()) {
            sequenceText.setKeySequence((KeySequence)
                    userBindings.iterator().next().getTriggerSequence());
        } else {
            sequenceText.setKeySequence((KeySequence)
                    systemBinding.getTriggerSequence());
        }
        sequenceText.setKeyStrokeLimit(4);
        
        texts.add(text);
        sequenceTexts.add(sequenceText);
        return text;
    }

    /**
     * 
     */
    private void changeKeybind() {
        
        sequences = new HashMap<Binding, KeySequence>();
        for (int i = 0; i < texts.size(); i++) {
            
            sequences.put((Binding)texts.get(i).getData(),
                    sequenceTexts.get(i).getKeySequence());
        }
    }
    
}
