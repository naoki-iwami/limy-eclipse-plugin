/*
 * Created 2007/12/29
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
package org.limy.velocity;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;

/**
 * Velocityのユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class VelocitySupport {
    
    // ------------------------ Fields

    /** Velocityエンジン */
    private static VelocityEngine engine;
    
    /** VMテンプレートファイルの文字セット */
    private static String vmCharset = "UTF-8";

    // ------------------------ Constructors

    /**
     * private constructor
     */
    private VelocitySupport() { }

    // ------------------------ Public Methods
    
    /**
     * Velocityエンジンを初期化します。
     */
    public static void cleanEngine() {
        engine = null;
    }
    
    public static void write(String templateName, Context context, Writer out) throws IOException {
        
        try {
            Template template = getEngine().getTemplate(templateName);
            template.merge(context, out);
        } catch (VelocityException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        
    }

    // ------------------------ Private Methods

    /**
     * Velocityエンジンを取得します。
     * @return Velocityエンジン
     */
    private static VelocityEngine getEngine() {
        if (engine == null) {
            engine = new VelocityEngine();
            try {
                Properties p = new Properties();
                p.setProperty(Velocity.RESOURCE_LOADER, "localfile");
                p.setProperty("localfile.resource.loader.class",
                        LocalFileResourceLoader.class.getName());
                p.setProperty("input.encoding", vmCharset);
                engine.init(p);
            } catch (Exception e) {
                engine.getLog().error(e.getMessage(), e);
            }
        }
        return engine;
    }

    

}
