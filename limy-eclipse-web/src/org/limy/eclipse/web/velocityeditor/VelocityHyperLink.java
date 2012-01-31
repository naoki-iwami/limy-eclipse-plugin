/*
 * Created 2006/01/14
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
package org.limy.eclipse.web.velocityeditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.limy.eclipse.common.ui.LimyUIUtils;

/**
 * Velocityエディタ内のハイパーリンクを表すクラスです。
 * @author Naoki Iwami
 */
public class VelocityHyperLink implements IHyperlink {

    // ------------------------ Fields

    /**
     * ターゲット文字列
     */
    private String targetWord;

    /**
     * リージョン
     */
    private IRegion region;
    
    /**
     * 現ファイル
     */
    private IFile targetFile;
    
    // ------------------------ Constructors

    /**
     * Creates a new URL hyperlink.
     * 
     * @param region
     * @param targetWord
     * @param targetFile 
     */
    public VelocityHyperLink(IRegion region, String targetWord, IFile targetFile) {
        Assert.isNotNull(targetWord);
        Assert.isNotNull(region);

        this.region = region;
        this.targetWord = targetWord;
        this.targetFile = targetFile;
    }

    // ------------------------ Implement Methods

    public IRegion getHyperlinkRegion() {
        return region;
    }

    public void open() {
        openFile();
    }

    public String getTypeLabel() {
        return null;
    }

    public String getHyperlinkText() {
        return null;
    }
    
    // ------------------------ Private Methods

    /**
     * ターゲット先を開きます。
     */
    private void openFile() {
        
        IPath filePath = targetFile.getFullPath().removeFirstSegments(1).removeLastSegments(1);
        String word = targetWord;
        if (word.lastIndexOf('.') < 0) {
            word = targetWord + ".vm";
        }
        
        IFile file = targetFile.getProject().getFile(filePath.append(word));
        while (!file.exists() && filePath.segmentCount() > 0) {
            filePath = filePath.removeLastSegments(1);
            file = targetFile.getProject().getFile(filePath.append(word));
        }
        
        if (file.exists()) {
            LimyUIUtils.openFile(file);
        }
        
    }

}
