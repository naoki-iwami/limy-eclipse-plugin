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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * Velocityエディタ用のSourceViewerConfigurationクラスです。
 * @depend - - - VelocityHyperlinkDetector
 * @author Naoki Iwami
 */
public class VelocitySourceViewerConfiguration extends SourceViewerConfiguration {
    
    // fields
    
    /**
     * エディタ
     */
    private VelocityEditor editor;
    
    /**
     * コードスキャナ
     */
    private VelocityCodeScanner codeScanner;
    
    // constructors

    /**
     * @param editor
     */
    public VelocitySourceViewerConfiguration(VelocityEditor editor) {
        super();
        this.editor = editor;
        codeScanner = ProviderManager.getInstance().getVelocityCodeScanner();
    }

    /**
     * @param scanner
     */
    public VelocitySourceViewerConfiguration(VelocityCodeScanner scanner) {
        super();
        this.codeScanner = scanner;
    }
    
    // methods in SourceViewerConfiguration

    /**
     * エディタの色付け情報を取得します。
     * @param sourceViewer
     * @return エディタの色付け情報
     */
    public IPresentationReconciler getPresentationReconciler(
            ISourceViewer sourceViewer) {
        
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new MultiDamagerRepairer(codeScanner);
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        
        return reconciler;
    }

    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
        return new IHyperlinkDetector[] {
                new VelocityHyperlinkDetector(editor),
        };
    }

}
