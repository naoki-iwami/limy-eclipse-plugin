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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.limy.eclipse.common.jdt.LimyJavaUtils;
import org.limy.eclipse.common.ui.LimyUIUtils;
import org.limy.eclipse.qalab.common.ClickableXmlParser;
import org.limy.eclipse.qalab.common.LimyQalabConstants;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.outline.BasePopupImage;
import org.limy.eclipse.qalab.outline.CanvasMouseMoveListener;
import org.limy.eclipse.qalab.outline.CommonKeyExecutor;
import org.limy.eclipse.qalab.outline.DialogSupport;
import org.limy.eclipse.qalab.outline.GraphPopupDialog;
import org.limy.eclipse.qalab.outline.GraphvizUtils;
import org.limy.eclipse.qalab.outline.ImageCreator;
import org.limy.eclipse.qalab.outline.JavaElementClickPoint;
import org.limy.eclipse.qalab.outline.JavaElementMouseListener;
import org.limy.eclipse.qalab.outline.ListenerCreator;
import org.limy.eclipse.qalab.outline.PopupImage;
import org.limy.eclipse.qalab.outline.QalabKeyListener;
import org.limy.eclipse.qalab.outline.SvgParser;
import org.w3c.dom.Element;

/**
 * UMLクラス図作成を担当するクラスです。
 * @author Naoki Iwami
 */
public class UmlImageCreator implements ImageCreator, ListenerCreator, DialogSupport {
    
    // ------------------------ Fields

    /** 環境設定 */
    private final LimyQalabEnvironment env;
    
    /** 対象要素 */
    private final IJavaElement targetElement;
    
    /** イメージ情報 */
    private final BasePopupImage image;

    /** 対象IType */
    private IType[] targetTypes;

    /** SVGパーサ */
    private ClickableXmlParser parser;

    // ------------------------ Constructors

    /**
     * UmlImageCreatorインスタンスを構築します。
     * @param env
     * @param targetElement 
     */
    public UmlImageCreator(LimyQalabEnvironment env, IJavaElement targetElement) {
        super();
        this.env = env;
        if (targetElement instanceof IMember) {
            // メソッド選択時などは、そのクラスを対象とする
            IMember member = (IMember)targetElement;
            this.targetElement = member.getCompilationUnit();
        } else {
            // クラスやパッケージ選択時は、それを対象とする
            this.targetElement = targetElement;
        }
        image = new BasePopupImage();
    }

    // ------------------------ Implement Methods

    public BasePopupImage create() throws CoreException, IOException {
        createPngFile();
        
        parser = new ClickableXmlParser() {

            public String getQualifiedName(Element el) {
                String elText = el.getFirstChild().getNodeValue().trim();

                // クラス選択時には、ICodeAssist を使ってITypeを検索
                if (targetElement instanceof ICompilationUnit) {
                    ICompilationUnit unit = (ICompilationUnit)targetElement;
                    try {
                        String source = unit.getSource();
                        int pos = -1;
                        while (true) {
                            pos = source.indexOf(elText, pos + 1);
                            if (pos < 0) {
                                break;
                            }
                            IJavaElement[] elements = unit.codeSelect(pos, elText.length());
                            for (IJavaElement javaElement : elements) {
                                if (javaElement instanceof IType) {
                                    IType type = (IType)javaElement;
                                    return type.getFullyQualifiedName();
                                }
                            }
                        }
                    } catch (JavaModelException e) {
                        // ignore
                    }
                    
                } else {
                    // パッケージの選択時には、パッケージの全子要素からITypeを検索
                    for (IType type : targetTypes) {
                        if (type.getElementName().equals(elText)) {
                            return type.getFullyQualifiedName();
                        }
                    }
                }
                return null;
            }

            public String getTooltip(IJavaElement javaElement) {
                if (javaElement != null) {
                    IType type = null;
                    if (javaElement instanceof IType) {
                        type = (IType)javaElement;
                    }
                    if (javaElement instanceof ICompilationUnit) {
                        ICompilationUnit unit = (ICompilationUnit)javaElement;
                        type = unit.findPrimaryType();
                    }
                    if (type != null) {
                        return UmlImageSourceSupport.createTooltipText(type);
                    }
                }
                return null;
            }
            
        };
        image.setElements(SvgParser.makePointElementInfos(
                image.getSvgImageFile(),
                new JavaElementClickPoint.Creator(env.getJavaProject(), parser)));
        
//        adjustElementInfos();
        return image;
    }

    public String getDialogTitle() {
        return "Classes Relation   (Press 'c' to change alignment, Press 'v' to show view)";
    }

    public MouseListener createMouseListener(GraphPopupDialog dialog) {
        return new JavaElementMouseListener(image);
    }

    public MouseMoveListener createMouseMoveListener(GraphPopupDialog dialog) {
        return new CanvasMouseMoveListener(dialog.getCanvas(), image);
    }

    public KeyListener createKeyListener(GraphPopupDialog dialog) {
        return new QalabKeyListener(new CommonKeyExecutor(this, this, dialog));
    }

    /**
     * 画像の方向を転換させて再描画します。
     * @return
     * @throws CoreException 
     * @throws IOException 
     */
    public BasePopupImage changeHorizontal() throws CoreException, IOException {
        
        image.setHorizontal(!image.isHorizontal());
        create();
        return image;
    }

    // ------------------------ Public Methods

    /**
     * イメージ情報を取得します。
     * @return イメージ情報
     */
    public BasePopupImage getPopupImage() {
        return image;
    }

    // ------------------------ Private Methods

    /**
     * targetElement 内の全要素をUMLによりグラフ化します。
     * @throws JavaModelException
     * @throws IOException
     */
    private void createPngFile() throws JavaModelException, IOException {
        
        IPreferenceStore store = env.getStore();
        String dotExe = store.getString(LimyQalabConstants.KEY_DOT_EXE);
        if (dotExe.length() == 0) {
            LimyUIUtils.showConfirmDialog("dot.exeの場所が指定されていません。\n"
                    + "プロジェクトのプロパティページから QALab -> Report のタブを選択して"
                    + "'dotバイナリ'の欄から設定できます。");
            return;
        }
        
        File dotDir = LimyQalabUtils.createTempFile(env.getProject(), "dest/javadoc");
        dotDir.mkdirs();
        
        targetTypes = LimyJavaUtils.getAllTypes(targetElement);
        
        File dotFile = UmlImageGraphvizSupport.createDotFile(image, env, targetTypes, dotDir);
        GraphvizUtils.creteImageFile(env, dotFile, dotDir, image);
    }

    public IJavaElement getTargetElement() {
        return targetElement;
    }

    public PopupImage changeLocation() throws CoreException, IOException {
        image.setHorizontal(!image.isHorizontal());
        create();
        return image;
    }

}
