/*
 * Created 2009/02/08
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
package org.limy.eclipse.qalab.outline.sequence;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.limy.eclipse.common.LimyEclipsePluginUtils;
import org.limy.eclipse.common.io.LimyIOUtils;
import org.limy.eclipse.qalab.LimyQalabPlugin;
import org.limy.eclipse.qalab.common.LimyQalabEnvironment;
import org.limy.eclipse.qalab.common.LimyQalabUtils;
import org.limy.eclipse.qalab.outline.ASTUtils;
import org.limy.eclipse.qalab.outline.BasePopupImage;
import org.limy.eclipse.qalab.outline.CanvasMouseMoveListener;
import org.limy.eclipse.qalab.outline.ClickablePointInfo;
import org.limy.eclipse.qalab.outline.CommonKeyExecutor;
import org.limy.eclipse.qalab.outline.DialogSupport;
import org.limy.eclipse.qalab.outline.GraphPopupDialog;
import org.limy.eclipse.qalab.outline.ImageCreator;
import org.limy.eclipse.qalab.outline.JavaElementClickPoint;
import org.limy.eclipse.qalab.outline.JavaElementMouseListener;
import org.limy.eclipse.qalab.outline.ListenerCreator;
import org.limy.eclipse.qalab.outline.PopupImage;
import org.limy.eclipse.qalab.outline.QalabKeyListener;
import org.limy.velocity.VelocitySupport;

import antlr.ANTLRException;
import antlr.collections.AST;

import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.zanthan.sequence.diagram.Diagram;
import com.zanthan.sequence.diagram.MethodExecution;
import com.zanthan.sequence.diagram.NodeFactory;
import com.zanthan.sequence.layout.LayoutData;
import com.zanthan.sequence.parser.Parser;
import com.zanthan.sequence.parser.ParserException;
import com.zanthan.sequence.parser.ParserFactory;
import com.zanthan.sequence.preferences.Prefs;
import com.zanthan.sequence.swing.ExceptionHandler;
import com.zanthan.sequence.swing.display.SwingPainter;
import com.zanthan.sequence.swing.display.SwingStringMeasure;
import com.zanthan.sequence.swing.model.Model;

/**
 * シーケンス図作成担当クラスです。
 * <p>
 * http://www.zanthan.com/itymbi/archives/cat_sequence.html
 * </p>
 * @author Naoki Iwami
 */
public class SequenceImageCreator implements ImageCreator, DialogSupport, ListenerCreator {

    // ------------------------ Fields
    
    /** 環境変数 */
    private LimyQalabEnvironment env;
    
    /** 対象メソッド */
    private IMethod method;
    
    /** 直前に作成したイメージ */
    private PopupImage image;

    /** 出力先pngファイル */
    private File pngFile;

    /** Sequenceレイアウトデータ */
    private LayoutData layoutData;

    /** シーケンス図解析結果 */
    private SequenceBean root;

    // ------------------------ Constructors
    
    public SequenceImageCreator(LimyQalabEnvironment env, IMethod method) {
        this.env = env;
        this.method = method;
    }

    // ------------------------ Implement Methods (ImageCreator)
    
    public PopupImage changeLocation() {
        return image; // 向き変換には対応していない
    }

    public PopupImage create() throws IOException {
        
        // イメージを作成
        IResource javaResource = method.getResource();
        
        File file = javaResource.getLocation().toFile();
        try {
            String content = LimyIOUtils.getContent(file);
            String[] lines = content.split("\\n");
            FileContents contents = new FileContents(file.getAbsolutePath(), lines);
            AST ast = TreeWalker.parse(contents);
            ASTUtils.debugSourceAst(ast);
            parseAst(ast);
            BasePopupImage popupImage = new BasePopupImage();
            popupImage.setImageFile(pngFile);
            popupImage.setElements(createElements());
            image = popupImage;
            return image;
        } catch (ANTLRException e) {
            LimyEclipsePluginUtils.log(e);
        } catch (JavaModelException e) {
            LimyEclipsePluginUtils.log(e);
        }
        return null;
    }
    
    // ------------------------ Implement Methods (DialogSupport)

    public PopupImage changeHorizontal() {
        return image; // 向き変換には対応していない
    }

    public String getDialogTitle() {
        return "シーケンス図表示   (Enable key 'v')";
    }

    public IJavaElement getTargetElement() {
        return method;
    }

    // ------------------------ Implement Methods (ListenerCreator)

    public KeyListener createKeyListener(GraphPopupDialog dialog) {
        return new QalabKeyListener(new CommonKeyExecutor(this, this, dialog));
    }

    public MouseListener createMouseListener(GraphPopupDialog dialog) {
        return new JavaElementMouseListener(image);
    }

    public MouseMoveListener createMouseMoveListener(GraphPopupDialog dialog) {
        return new CanvasMouseMoveListener(dialog.getCanvas(), image);
    }

    // ------------------------ Private Methods

    private void parseAst(AST ast) throws JavaModelException, IOException {
        
        AST methodAst = ASTUtils.searchMethodFromSource(ast, method);
        if (methodAst == null) {
            throw new IOException("インナークラスには対応していません。");
        }
        
        root = new SequenceBean(null, method);
        SequenceBean target = root;

        // 全メソッド呼び出しを取得。ただしcatch/finally節内のものは除く
        AST[] methodCalls = ASTUtils.searchMultiAllSiblingEx(methodAst,
                new int[] { TokenTypes.LITERAL_CATCH, TokenTypes.LITERAL_FINALLY },
                TokenTypes.METHOD_CALL);

        for (AST methodCall : methodCalls) {
            AST dotAst = methodCall.getFirstChild();
            if (dotAst.getType() == TokenTypes.IDENT) {
                // b()形式。自クラス内メソッドの呼び出し
                // ICodeAssist を使って対象メソッドを検索
                int sourceOffset = method.getSourceRange().getOffset();
                int pos = -1;
                while (true) {
                    pos = method.getSource().indexOf(dotAst.getText(), pos + 1);
                    if (pos < 0) {
                        break;
                    }
                    IJavaElement[] elements = method.getCompilationUnit().codeSelect(
                            sourceOffset + pos, dotAst.getText().length());
                    for (IJavaElement element : elements) {
                        // スーパークラスのメソッドを呼び出している場合は、
                        // 強制的に自クラスをコンテナ名とする。同一ライン上に配置したい為
                        target.addChild(new SequenceBean(null, element,
                                method.getDeclaringType().getElementName()));
                        pos = method.getSource().length() - 1;
                        break;
                    }
                }
            } else {
                // a.b()形式
                AST callingAst = dotAst.getFirstChild();
                AST calledAst = callingAst.getNextSibling();
                
                // ICodeAssist を使って対象メソッドを検索
                int sourceOffset = method.getSourceRange().getOffset();
                
                int pos = -1;
                while (true) {
                    pos = method.getSource().indexOf(calledAst.getText(), pos + 1);
                    if (pos < 0) {
                        break;
                    }
                    IJavaElement[] elements = method.getCompilationUnit().codeSelect(
                            sourceOffset + pos, calledAst.getText().length());
                    for (IJavaElement element : elements) {
                        String instanceName = callingAst.getText();
                        if (instanceName.equals("(") || instanceName.equals(".")) {
                            instanceName = null;
                        }
                        target.addChild(new SequenceBean(instanceName, element));
                        pos = method.getSource().length() - 1;
                        break;
                    }
                }
            }
        }
        try {
            writeSequence(root);
        } catch (ParserException e) {
            throw new IOException(e.getMessage());
        }
        
    }

    /**
     * シーケンス図を作成して layoutData, pngFile に出力します。
     * @param root シーケンス図Bean
     * @throws IOException I/O例外
     * @throws ParserException 
     */
    private void writeSequence(SequenceBean root) throws IOException, ParserException {
        StringWriter out = new StringWriter();
        Context context = new VelocityContext();
        context.put("root", root);
        VelocitySupport.write(
                new File(LimyQalabPlugin.getDefault().getPluginRoot(),
                        "resource/sequence/index.vm").getAbsolutePath(),
                context, out);
        File txtFile = LimyQalabUtils.createTempFile(env.getProject(), "sequence.txt");
        pngFile = LimyQalabUtils.createTempFile(env.getProject(), "sequence.png");
        FileUtils.writeByteArrayToFile(txtFile, out.toString().getBytes());
        
        Parser parser = ParserFactory.getInstance().getDefaultParser();
        NodeFactory nodeFactory = ParserFactory.getInstance().getNodeFactoryForParser(parser);
        Diagram diagram = new Diagram(parser, nodeFactory);
        
        PushbackReader reader = new PushbackReader(new FileReader(txtFile));
        try {
            diagram.parse(reader);
        } finally {
            reader.close();
        }
        
        Model model = new Model(new ExceptionHandler() {
            public void exception(Exception e) {
                e.printStackTrace();
            }
        }, diagram);
        
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        layoutData = new LayoutData(new SwingStringMeasure(graphics));
        diagram.layout(layoutData);
        
        int height = layoutData.getHeight();
        int width = layoutData.getWidth();

        BufferedImage png = new BufferedImage(width,
                                              height,
                                              BufferedImage.TYPE_INT_ARGB);
        Graphics2D pngGraphics = png.createGraphics();
        pngGraphics.setClip(0, 0, width, height);
        Map<Key, Object> hintsMap = new HashMap<Key, Object>();
        hintsMap.put(RenderingHints.KEY_ANTIALIASING,
                     RenderingHints.VALUE_ANTIALIAS_ON);
        pngGraphics.addRenderingHints(hintsMap);
        pngGraphics.setBackground(Prefs.getColorValue(Prefs.BACKGROUND_COLOR));
        pngGraphics.fillRect(0, 0, width, height);

        SwingPainter painter = new SwingPainter();
        painter.setGraphics(pngGraphics);
        model.layout(layoutData);
        layoutData.paint(painter);
        
        ImageIO.write(png, "png", pngFile);
    }

    private Collection<? extends ClickablePointInfo> createElements() {
        Collection<ClickablePointInfo> results = new ArrayList<ClickablePointInfo>();
        List<MethodExecution> methodExecutions = layoutData.getMethodExecutions();
        
        SequenceBean lastBean = null;
        for (MethodExecution methodExecution : methodExecutions) {
            Double rect = new Rectangle2D.Double(
                    methodExecution.getMinX(),
                    methodExecution.getStartY(),
                    methodExecution.getMaxX() - methodExecution.getMinX(),
                    methodExecution.getEndY() - methodExecution.getStartY());
            ClickablePointInfo info = new JavaElementClickPoint(root.searchElement(
                    lastBean, methodExecution.getName()));
            info.setRect(rect);
            info.setTooltipText(methodExecution.getName());
            results.add(info);
        }
        return results;
    }
}
