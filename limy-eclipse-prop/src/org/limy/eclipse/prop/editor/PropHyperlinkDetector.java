/*
 * Created 2005/07/21
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertyKeyHyperlink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * プロパティエディタ内のハイパーリンクを検索するクラスです。
 * @author Naoki Iwami
 * @see org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertyKeyHyperlinkDetector
 */
public class PropHyperlinkDetector implements IHyperlinkDetector {
    
    // ------------------------ Fields

    /**
     * テキストエディタ
     */
    private ITextEditor fTextEditor;
    
    // ------------------------ Constructors

    /**
     * PropHyperlinkDetectorインスタンスを構築します。
     * @param editor テキストエディタ
     */
    public PropHyperlinkDetector(ITextEditor editor) {
//        Assert.isNotNull(editor);
        fTextEditor = editor;
    }

    // ------------------------ Implement Methods

    public IHyperlink[] detectHyperlinks(
            ITextViewer textViewer, IRegion region, boolean canShowMulti) {
        
        if (region == null || fTextEditor == null || canShowMulti) {
            return null;
        }

        IEditorSite site = fTextEditor.getEditorSite();
        if (site == null) {
            return null;
        }

        if (!checkEnabled(region)) {
            return null;
        }

        int offset = region.getOffset();
        ITypedRegion partition = null;
        try {
            IStorageEditorInput editorInput =
                    (IStorageEditorInput)fTextEditor.getEditorInput();
            IDocument document = fTextEditor.getDocumentProvider().getDocument(editorInput);
            partition = createPartition(document, offset);

            // Check whether it is the correct partition
            if (partition == null || !IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType())) {
                return null;
            }

            // Check whether the partition covers the selection
            if (offset + region.getLength() > partition.getOffset() + partition.getLength()) {
                return null;
            }

            // Extract the key from the partition (which contains key and assignment
            String key = document.get(partition.getOffset(), partition.getLength());

            String realKey = key.trim();
            int delta = key.indexOf(realKey);

            // Check whether the key is valid
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(document.get().getBytes()));
            if (properties.getProperty(realKey) == null) {
                return null;
            }

            return new PropertyKeyHyperlink[] {
                    new PropertyKeyHyperlink(
                            new Region(partition.getOffset() + delta, realKey.length()),
                            realKey, fTextEditor),
            };

        } catch (BadLocationException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    // ------------------------ Private Methods

    /**
     * リージョンを作成します。
     * @param document ドキュメント
     * @param offset オフセット
     * @return リージョン
     * @throws BadLocationException ポジション例外 
     */
    private ITypedRegion createPartition(IDocument document, int offset)
            throws BadLocationException {
        
        int line = document.getLineOfOffset(offset);
        int startPos = document.getLineOffset(line);
        int endPos = startPos;
        while (endPos < document.getLength()) {
            char c = document.getChar(endPos);
            if (" \t\n\r=".indexOf(c) >= 0) {
                break;
            }
            ++endPos;
        }
        
        ITypedRegion partition = new TypedRegion(
                startPos, endPos - startPos,
                IDocument.DEFAULT_CONTENT_TYPE);
        return partition;
    }

    /**
     * リージョンが有効かどうかを返します。
     * @param region リージョン
     * @return リージョンが有効ならば真
     */
    private boolean checkEnabled(IRegion region) {
        if (region == null || region.getOffset() < 0) {
            return false;
        }

        return fTextEditor.getEditorInput() instanceof IFileEditorInput;
    }
    
}
