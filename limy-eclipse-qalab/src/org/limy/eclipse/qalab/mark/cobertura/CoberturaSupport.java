/*
 * Created 2007/08/29
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
package org.limy.eclipse.qalab.mark.cobertura;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.LineData;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.coveragedata.SourceFileData;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.resource.LimyMarkerUtils;
import org.limy.eclipse.qalab.LimyQalabMarker;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.common.QalabResourceUtils;

/**
 * Coberturaのサポートクラスです。
 * @author Naoki Iwami
 */
public class CoberturaSupport {
    
    /** 環境設定 */
    private final LimyQalabEnvironment env;

    /** リソース一覧 */
    private final IResource[] resources;
    
    /**
     * CoberturaSupport インスタンスを構築します。
     * @param env 環境設定
     * @param resources リソース一覧
     */
    public CoberturaSupport(LimyQalabEnvironment env, IResource[] resources) {
        super();
        this.env = env;
        this.resources = resources;
    }

    /**
     * カバレッジ結果を元にマーカーを作成します。
     * @param dataFile cobertura.serファイル
     * @throws CoreException コア例外
     * @throws IOException I/O例外
     */
    public void addCoverageMarker(File dataFile)
            throws CoreException, IOException {

        Map<String, IDocument> documentMap = new HashMap<String, IDocument>();
        Map<String, IResource> resourceMap = new HashMap<String, IResource>();
        
        for (IResource resource : resources) {
            IDocument document = QalabResourceUtils.parseDocument(resource);
            String className = LimyQalabUtils.getQualifiedClassName(env, resource);
            documentMap.put(className, document);
            resourceMap.put(className, resource);
        }
        
        ProjectData projectData = CoverageDataFileHandler.loadCoverageData(dataFile);

        for (PackageData packageData : (Collection<PackageData>)projectData.getPackages()) {
            
            Collection<SourceFileData> sourceFiles = packageData.getSourceFiles();
            for (SourceFileData sourceFile : sourceFiles) {
                SortedSet<ClassData> classDatas = sourceFile.getClasses();
                for (ClassData classData : classDatas) {
                    String className = classData.getName();
                    IDocument document = documentMap.get(className);
                    IResource resource = resourceMap.get(className);
                    if (resource != null && document != null) {
                        SortedSet<LineData> lines = classData.getLines();
                        for (LineData line : lines) {
                            addCoverageMarker(resource, line, document);
                        }
                        addClassMarker(resource, classData);
                    }
                }
            }
        }

    }

    // ------------------------ Private Methods

    /**
     * リソースにマーカー情報（カバレッジ率）を付加します。
     * @param resource リソース
     * @param classData Cobertura実行結果
     * @throws CoreException 
     */
    private void addClassMarker(IResource resource, ClassData classData) throws CoreException {
        
        int coverageLines = classData.getNumberOfCoveredLines();
        int coverageBranchs = classData.getNumberOfCoveredBranches();
        int allLines = classData.getNumberOfValidLines();
        int allBranchs = classData.getNumberOfValidBranches();
        
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(LimyQalabMarker.ALL_LINE_NUMBER, Integer.toString(allLines));
        attrs.put(LimyQalabMarker.ALL_BRANCH_NUMBER, Integer.toString(allBranchs));
        attrs.put(LimyQalabMarker.COVERAGE_LINE, Integer.toString(coverageLines));
        attrs.put(LimyQalabMarker.COVERAGE_BRANCH, Integer.toString(coverageBranchs));
        LimyMarkerUtils.addMarker(LimyQalabMarker.COVERAGE_RESULT,
                resource, attrs);
        
    }

    /**
     * リソースにマーカー情報（行ごとのカバレッジ）を付加します。
     * @param resource 対象リソース
     * @param line カバレッジ結果
     * @param document ドキュメント
     * @throws CoreException コア例外
     */
    private void addCoverageMarker(IResource resource, LineData line, IDocument document)
            throws CoreException {
        
        Map<String, Integer> attrs = new HashMap<String, Integer>();
        try {
            attrs.put(IMarker.CHAR_START, Integer.valueOf(
                    document.getLineOffset(line.getLineNumber() - 1)));
            attrs.put(IMarker.CHAR_END, Integer.valueOf(
                    document.getLineOffset(line.getLineNumber()) - 1));
        } catch (BadLocationException e) {
            LimyEclipsePluginUtils.log(e);
        }
        
        if (line.getHits() == 0) {
            LimyMarkerUtils.addMarker(LimyQalabMarker.NO_COVERAGE_ID,
                    resource, line.getLineNumber(), "coverage NG", attrs);
        } else {
            LimyMarkerUtils.addMarker(LimyQalabMarker.COVERAGE_ID,
                    resource, line.getLineNumber(), "coverage OK", attrs);
        }
        
    }


}
