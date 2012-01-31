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
package org.limy.eclipse.qalab.ant.pmd;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.PapariTextRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.SummaryHTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.renderers.XSLTRenderer;
import net.sourceforge.pmd.renderers.YAHTMLRenderer;
import net.sourceforge.pmd.renderers.CSVRenderer;
import org.apache.tools.ant.BuildException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class LimyFormatter {

    private interface RendererBuilder {
        Renderer build(Object[] optionalArg);
    } // factory template

    private File toFile;
    private String linkPrefix;
    private String linePrefix;
    private String type;
    private boolean toConsole;
    private boolean showSuppressed;

    private static final Map<String, RendererBuilder> renderersByCode = new HashMap<String, RendererBuilder>(8);

    static {
        renderersByCode.put("xml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new XMLRenderer(); }
        });
        renderersByCode.put("betterhtml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new XSLTRenderer(); }
        });
        renderersByCode.put("html", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new HTMLRenderer((String) arg[0], (String) arg[1]); }
        });
        renderersByCode.put("summaryhtml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new SummaryHTMLRenderer((String) arg[0], (String) arg[1]); }
        });
        renderersByCode.put("papari", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new PapariTextRenderer(); }
        });
        renderersByCode.put("csv", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new CSVRenderer(); }
        });
        renderersByCode.put("emacs", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new EmacsRenderer(); }
        });
        renderersByCode.put("vbhtml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new VBHTMLRenderer(); }
        });
        renderersByCode.put("yahtml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new YAHTMLRenderer(); }
        });
        renderersByCode.put("text", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new TextRenderer(); }
        });
        // add additional codes & factories here
    }

    public void setShowSuppressed(boolean value) {
        this.showSuppressed = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLinkPrefix(String linkPrefix) {
        this.linkPrefix = linkPrefix;
    }

    public void setToFile(File toFile) {
        this.toFile = toFile;
    }

    public void setToConsole(boolean toConsole) {
        this.toConsole = toConsole;
    }

    public void setLinePrefix(String linePrefix) {
        this.linePrefix = linePrefix;
    }

    private Writer writer;

    private Renderer renderer;

    public Renderer getRenderer() {
        return renderer;
    }

    public void start(String baseDir) {
        try {
            if (toConsole) {
                writer = new BufferedWriter(new OutputStreamWriter(System.out));
            }
            if (toFile != null) {
                writer = getToFileWriter(baseDir);
            }
            renderer = getRenderer(toConsole);
            renderer.setWriter(writer);
            renderer.start();
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage());
        }
    }

    public void end(Report errorReport) {
        try {
            renderer.renderFileReport(errorReport);
            renderer.end();
            writer.write(PMD.EOL);
            if (toConsole) {
                writer.flush();
            } else {
                writer.close();
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage());
        }
    }

    public boolean isNoOutputSupplied() {
        return toFile == null && !toConsole;
    }

    public String toString() {
        return "file = " + toFile + "; renderer = " + type;
    }

    private static String[] validRendererCodes() {
        return renderersByCode.keySet().toArray(new String[renderersByCode.size()]);
    }

    private static String unknownRendererMessage(String userSpecifiedType) {
        StringBuffer sb = new StringBuffer(100);
        sb.append("Formatter type must be one of: '");
        String[] typeCodes = validRendererCodes();
        sb.append(typeCodes[0]);
        for (int i = 1; i < typeCodes.length; i++) {
            sb.append("', '").append(typeCodes[i]);
        }
        sb.append("', or a class name; you specified: ");
        sb.append(userSpecifiedType);
        return sb.toString();
    }

    private Renderer fromClassname(String rendererClassname) {
        try {
            return (Renderer) Class.forName(rendererClassname).newInstance();
        } catch (Exception e) {
            throw new BuildException(unknownRendererMessage(rendererClassname));
        }
    }

    // FIXME - hm, what about this consoleRenderer thing... need a test for this
    private Renderer getRenderer(boolean consoleRenderer) {
        if ("".equals(type)) {
            throw new BuildException(unknownRendererMessage("<unspecified>"));
        }
        RendererBuilder builder = renderersByCode.get(type);
        Renderer renderer = builder == null ? fromClassname(type) : builder.build(new String[]{linkPrefix, linePrefix});
        renderer.showSuppressedViolations(showSuppressed);
        return renderer;
    }

    private Writer getToFileWriter(String baseDir) throws IOException {
        
        File file;
        if (!toFile.isAbsolute()) {
            file = new File(baseDir + System.getProperty("file.separator") + toFile.getPath());
        } else {
            file = toFile;
        }
        return new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
//        if (!toFile.isAbsolute()) {
//            return new BufferedWriter(new FileWriter(new File(baseDir + System.getProperty("file.separator") + toFile.getPath())));
//        }
//        return new BufferedWriter(new FileWriter(toFile));
    }
}
