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
package org.limy.eclipse.qalab.outline.umlimage;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.limy.eclipse.qalab.umlgraph.ContextView;
import org.limy.eclipse.qalab.umlgraph.OptionProvider;
import org.limy.eclipse.qalab.umlgraph.Options;
import org.limy.eclipse.qalab.umlgraph.PackageView;
import org.limy.eclipse.qalab.umlgraph.UmlGraph;
import org.limy.eclipse.qalab.umlgraph.javadoc.ClassDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.PackageDoc;
import org.limy.eclipse.qalab.umlgraph.javadoc.RootDoc;

/**
 * dotファイル作成担当クラスです。
 * @author Naoki Iwami
 */
public final class DotCreator {
    
    /**
     * private constructor
     */
    private DotCreator() { }
    
    // ------------------------ Public Methods
    
    /**
     * クラスを解析してdotファイルを出力します。
     * @param outDir 出力基準ディレクトリ
     * @param root RootDoc
     * @return 作成されたdotファイル
     * @throws IOException I/O例外
     */
    public static File createDotFile(File outDir, RootDoc root)
            throws IOException {
        
        Options op = UmlGraph.buildOptions(root);
        File dotFile = new File(outDir, "graph.dot");

        Collection<PackageDoc> packages = new HashSet<PackageDoc>();
        for (ClassDoc classDoc : root.classes()) {
            packages.add(classDoc.containingPackage());
        }
        
        if (root.classes().length == 1) {
            ClassDoc classDoc = root.classes()[0];
            ContextView view = new ContextView(
                    outDir.getAbsolutePath(), classDoc, root, op);
            
            UmlGraph.buildGraph(root, view, null);
            dotFile = new File(outDir, classDoc.qualifiedName().replace('.', '/') + ".dot");
            
        } else if (packages.size() == 1) {
            
            PackageDoc packageDoc = packages.iterator().next();
            OptionProvider view = new PackageView(outDir.getAbsolutePath(),
                    packageDoc, root, op);

            UmlGraph.buildGraph(root, view, null);
            
            String packageName = packageDoc.name(); // a.b.c
            dotFile = new File(outDir, packageName.replace('.', '/') + "/"
                    + packageName + ".dot"); // a/b/c/c.dot
            
        } else {
            UmlGraph.buildGraph(root, op, null);
        }
        return dotFile;
    }

}
