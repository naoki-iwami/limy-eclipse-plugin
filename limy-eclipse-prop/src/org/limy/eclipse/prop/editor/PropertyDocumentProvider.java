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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * プロパティエディタ用ドキュメントプロバイダクラスです。
 * <p>
 * 主にファイルのUnicode<=>日本語変換を行います。
 * </p>
 * @depend - - - PropertyDocumentUtils
 * @author Naoki Iwami
 */
public class PropertyDocumentProvider extends FileDocumentProvider {

    @Override
    protected IDocument createDocument(Object element) throws CoreException {
        IDocument document = super.createDocument(element);
        PropertyDocumentUtils.createDocumentLocal(document);
        return document;
    }

    @Override
    protected void doSaveDocument(IProgressMonitor monitor,
            Object element, IDocument document, boolean overwrite) throws CoreException {
        
        super.doSaveDocument(monitor, element,
                new Document(PropertyDocumentUtils.createSaveString(document)),
                overwrite);
        
//        super.doSaveDocument(monitor, element, document, overwrite);
//        PropertyDocumentUtils.createDocumentLocal(document);
    }
    
}
