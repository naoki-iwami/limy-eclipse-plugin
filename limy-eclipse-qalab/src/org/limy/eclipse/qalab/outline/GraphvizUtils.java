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
package org.limy.eclipse.qalab.outline;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.limy.common.ProcessUtils;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;

/**
 * Graphviz関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class GraphvizUtils {
    
    /**
     * private constructor
     */
    private GraphvizUtils() { }
    
    /**
     * dotファイルからイメージファイル（pngおよびsvg）を作成します。
     * @param env 環境設定
     * @param dotFile dotファイル
     * @param outputDir イメージファイル出力ディレクトリ
     * @param image 作成したイメージ情報を格納するPopupImage
     * @throws IOException I/O例外
     */
    public static void creteImageFile(LimyQalabEnvironment env, File dotFile,
            File outputDir, BasePopupImage image) throws IOException {
        
        File pngFile = new File(outputDir, "graph.png");
        File svgFile = new File(outputDir, "graph.svg");
        pngFile.getParentFile().mkdirs();
        
        String dotExe = env.getStore().getString(LimyQalabConstants.KEY_DOT_EXE);
        Writer out = new StringWriter();
        
        ProcessUtils.execProgram(new File("."), out,
                dotExe,
                "-Tpng", "-o" + pngFile.getAbsolutePath(),
                dotFile.getAbsolutePath());

        ProcessUtils.execProgram(new File("."), out,
                dotExe,
                "-Tsvg", "-o" + svgFile.getAbsolutePath(),
                dotFile.getAbsolutePath());
        
        image.setImageFile(pngFile);
        image.setSvgImageFile(svgFile);
    }


}
