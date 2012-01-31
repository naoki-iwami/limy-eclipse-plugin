/*
 * Created 2006/05/26
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
package org.limy.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.limy.eclipse.common.LimyEclipsePluginUtils;

/**
 * プロセス関連のユーティリティクラスです。
 * @author Naoki Iwami
 */
public final class ProcessUtils {

    /**
     * private constructor
     */
    private ProcessUtils() {
        // empty
    }

    /**
     * プログラムを実行します。
     * @param execDir 実行ディレクトリ
     * @param out 出力先
     * @param args 実行パラメータ
     * @return 実行
     * @throws IOException I/O例外
     */
    public static int execProgram(File execDir, Writer out, String... args)
            throws IOException {
        
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.directory(execDir);
        builder.environment().put("ANT_OPTS", "-Xmx256M"); // pmd-cpd対策
        
        builder.redirectErrorStream(true);
//        for (String arg : args) {
//            System.out.println("ARG = " + arg);
//        }
        Process process = builder.start();
        
//        BufferedReader reader = new BufferedReader(
//                new InputStreamReader(process.getInputStream()));
//        try {
//            return waitEndProcess(out, process, reader);
//        } finally {
//            reader.close();
//        }
        
        BufferedInputStream stream = new BufferedInputStream(process.getInputStream());
        try {
            return waitEndProcess(out, process, stream);
        } finally {
            stream.close();
        }
    }

    /**
     * プログラムを実行します。プログラムの終了を待たずにすぐに処理を返します。
     * @param execDir 実行ディレクトリ
     * @param args 実行パラメータ
     * @throws IOException I/O例外
     */
    public static void execProgramNoWait(File execDir, String... args)
            throws IOException {
        
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.directory(execDir);
        builder.environment().put("ANT_OPTS", "-Xmx256M"); // pmd-cpd対策
        
        builder.redirectErrorStream(true);
        builder.start();
    }

    // ------------------------ Private Methods

    /**
     * プロセスが終了するまで待機します。
     * @param out プロセスの実行結果（標準出力およびエラー出力）の出力先
     * @param process プロセス
     * @param stream プロセスの入力ストリーム（標準出力およびエラー出力）
     * @return プロセスの実行結果
     * @throws IOException I/O例外
     */
    private static int waitEndProcess(Writer out, Process process, BufferedInputStream stream)
            throws IOException {
        
        int exitValue = -1;

        while (true) {
            try {
                Thread.sleep(100);
                
                while (true) {
                    int size = stream.available();
                    if (size <= 0) {
                        break;
                    }
                    byte[] bs = new byte[size];
                    stream.read(bs, 0, size);
                    
                    if (out != null) {
                        out.write(new String(bs));
                        out.flush();
                    }
                }
                exitValue = process.exitValue();
                break;
            } catch (IllegalThreadStateException e) {
                // empty
//                System.out.println("IllegalThreadStateException");
            } catch (InterruptedException e) {
                LimyEclipsePluginUtils.log(e);
            }
        }
        return exitValue;
    }

//    /**
//     * プロセスが終了するまで待機します。
//     * @param out プロセスの実行結果（標準出力およびエラー出力）の出力先
//     * @param process プロセス
//     * @param reader 
//     * @return プロセスの実行結果
//     * @throws IOException I/O例外
//     */
//    private static int waitEndProcess(Writer out, Process process, BufferedReader reader)
//            throws IOException {
//        
//        int exitValue = -1;
//
//        while (true) {
//            try {
//                Thread.sleep(100);
//                
//                String line = reader.readLine();
//                if (line == null) {
//                    break;
//                }
//                if (out != null) {
//                    System.out.println(line);
//                    out.write(line);
//                    out.flush();
//                }
//                
//                exitValue = process.exitValue();
//                break;
//            } catch (IllegalThreadStateException e) {
//                // empty
//            } catch (InterruptedException e) {
//                LimyEclipsePluginUtils.log(e);
//            }
//        }
//        return exitValue;
//    }

}
