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
package org.limy.eclipse.qalab.outline.umlimage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.outline.BasePopupImage;
import org.limy.eclipse.qalab.umlgraph.javadoc.RootDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.impl.RootDocImpl;

/**
 * @author Naoki Iwami
 */
public final class UmlImageGraphvizSupport {
    
    /**
     * private constructor
     */
    private UmlImageGraphvizSupport() { }

    /**
     * dotファイルを作成します。
     * @param umlImage 
     * @param env 環境設定
     * @param targetTypes 
     * @param dotDir 
     * @return
     * @throws IOException 
     */
    public static File createDotFile(BasePopupImage umlImage,
            LimyQalabEnvironment env, IType[] targetTypes,
            File dotDir) throws IOException {
        
        RootDoc root = new RootDocImpl(env,
                targetTypes,
                new IType[0],
                createOptions(umlImage, dotDir));

        return DotCreator.createDotFile(dotDir, root);
    }

    // ------------------------ Private Methods

    /**
     * Optionsを作成します。
     * @param umlImage 
     * @param destDir 出力ディレクトリ
     * @return Options
     */
    private static String[][] createOptions(BasePopupImage umlImage, File destDir) {
        List<String[]> baseOptions = new ArrayList<String[]>();
        baseOptions.add(new String[] { "-inferrel" });
        baseOptions.add(new String[] { "-collpackages", "java.util.*" });
        baseOptions.add(new String[] { "-hide", "^java\\..*" });
        baseOptions.add(new String[] { "-d", destDir.getAbsolutePath() });
        baseOptions.add(new String[] { "-enumerations" });
        if (umlImage.isHorizontal()) {
            baseOptions.add(new String[] { "-horizontal" });
        }
        
        String[][] options = new String[baseOptions.size()][];
        for (int i = 0; i < baseOptions.size(); i++) {
            options[i] = baseOptions.get(i);
        }
        return options;
    }
    
}
