/*
 * Created 2006/08/05
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
package org.limy.velocity.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.limy.velocity.VelocitySupport;
import org.limy.velocity.VmParam;
import org.limy.velocity.XmlToHtml;
import org.limy.velocity.XmlToHtmlImpl;

/**
 * xml -> text のフォーマット用AntTaskです。
 * @author Naoki Iwami
 */
public class VmStyleTask extends Task {
    
    // ------------------------ Fields

    /**
     * 入力元xmlファイル
     */
    private File in;

    /**
     * 入力元xmlファイル一覧
     */
    private List<SimpleParam> inFiles = new ArrayList<SimpleParam>();

    /**
     * 出力先ファイル
     */
    private File out;

    /**
     * スタイル(VM)ファイル
     */
    private File style;
    
    /**
     * ツールクラス
     */
    private String toolClass;
    
    /**
     * パラメータ一覧
     */
    private Collection<VmParam> params = new ArrayList<VmParam>();

    // ------------------------ Fields (inner)
    
    /** HTML出力担当 */
    private XmlToHtml xmlWriter = new XmlToHtmlImpl();

    // ------------------------ Override Methods
    
    @Override
    public void execute() {
        
        VelocitySupport.cleanEngine();
        try {
            
            supportToolClass();
            
            if (!inFiles.isEmpty()) {
                // 入力ファイルが複数ある場合
                File[] xmlFiles = new File[inFiles.size()];
                for (int i = 0; i < xmlFiles.length; i++) {
                    xmlFiles[i] = new File(inFiles.get(i).getValue());
                }
                xmlWriter.createHtml(Arrays.asList(xmlFiles), style, out, params);
            } else if (in != null) {
                xmlWriter.createHtml(Arrays.asList(in), style, out, params);
            } else {
                xmlWriter.createHtml(new ArrayList<File>(), style, out, params);
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }
        
    }
    

    // ------------------------ Public Methods

    /**
     * param要素を追加します。
     * @return param要素
     */
    public VmParam createParam() {
        VmParam p = new VmParam();
        params.add(p);
        return p;
    }

    /**
     * infile要素を追加します。
     * @return infile要素
     */
    public SimpleParam createInfile() {
        SimpleParam p = new SimpleParam();
        inFiles.add(p);
        return p;
    }

    // ------------------------ Getter/Setter Methods

    /**
     * 入力元xmlファイルを設定します。
     * @param in 入力元xmlファイル
     */
    public void setIn(File in) {
        this.in = in;
    }

    /**
     * 出力先ファイルを設定します。
     * @param out 出力先ファイル
     */
    public void setOut(File out) {
        this.out = out;
    }
    
    /**
     * スタイル(VM)ファイルを設定します。
     * @param style スタイル(VM)ファイル
     */
    public void setStyle(File style) {
        this.style = style;
    }
    
    /**
     * ツールクラスを設定します。
     * @param toolClass ツールクラス
     */
    public void setToolClass(String toolClass) {
        this.toolClass = toolClass;
    }

    // ------------------------ Private Methods

    /**
     * ツールクラスをパラメータに追加します。
     * @throws Exception インスタンス例外
     */
    private void supportToolClass() throws Exception {
        params.add(new VmParam("Util", new VmParseMacro(xmlWriter)));
        if (toolClass != null) {
            Object instance = Class.forName(toolClass).newInstance();
            params.add(new VmParam("Tool", instance));
        }
        Map<String, Object> innerValues = new HashMap<String, Object>();
        for (VmParam param : params) {
            innerValues.put(param.getName(), param.getExpression());
        }
        params.add(new VmParam("__INNER_VALUES__", innerValues));
        
        params.add(new VmParam("__BASE_PATH__", out.getParent()));
    }

}
