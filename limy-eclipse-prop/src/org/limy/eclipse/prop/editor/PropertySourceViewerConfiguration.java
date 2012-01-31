/*
 * Created 2004/12/02
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
package org.limy.eclipse.prop.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * プロパティエディタ用のSourceViewerConfigurationクラスです。
 * @depend - - - PropHyperlinkDetector
 * @depend - - - PropertyDoubleClickStrategy
 * @author Naoki Iwami
 */
public class PropertySourceViewerConfiguration extends
        SourceViewerConfiguration {
    
    // ------------------------ Fields

    /**
     * エディタ
     */
    private PropertyEditor editor;
    
    /**
     * コードスキャナ
     */
    private PropertyCodeScanner codeScanner;

    // ------------------------ Constructors
    
    /**
     * PropertySourceViewerConfigurationインスタンスを構築します。
     * @param editor
     */
    public PropertySourceViewerConfiguration(PropertyEditor editor) {
        super();
        this.editor = editor;
        codeScanner = ProviderManager.getInstance().getPropertyScanner();
    }
    
    /**
     * PropertySourceViewerConfigurationインスタンスを構築します。
     * @param codeScanner
     */
    public PropertySourceViewerConfiguration(PropertyCodeScanner codeScanner) {
        super();
        this.codeScanner = codeScanner;
    }
    
    // ------------------------ Override Methods

    public IPresentationReconciler getPresentationReconciler(
            ISourceViewer sourceViewer) {
        
        PresentationReconciler reconciler = new PresentationReconciler();
        DefaultDamagerRepairer dr = new PropertyDamagerRepairer(codeScanner);
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        return reconciler;
    }
    
    public ITextDoubleClickStrategy getDoubleClickStrategy(
            ISourceViewer sourceViewer, String contentType) {
        return new PropertyDoubleClickStrategy();
    }
    
    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
        return new IHyperlinkDetector[] {
                new URLHyperlinkDetector(sourceViewer),
                new PropHyperlinkDetector(editor),
        };
    }

}
