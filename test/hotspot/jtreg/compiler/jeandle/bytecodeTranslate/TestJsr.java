/*
 * Copyright (c) 2026, the Jeandle-JDK Authors. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

/**
 * @test
 * @library /test/lib
 * @build jdk.test.lib.Asserts
 * @compile JsrExecution.jasm
 * @run main/othervm -Xcomp -XX:-TieredCompilation
 *      -XX:CompileCommand=compileonly,compiler.jeandle.bytecodeTranslate.JsrExecution::testWithJSR
 *      -XX:CompileCommand=compileonly,compiler.jeandle.bytecodeTranslate.JsrExecution::testWithNestedJSR
 *      -XX:CompileCommand=compileonly,compiler.jeandle.bytecodeTranslate.JsrExecution::testWithSharedJSR
 *      -XX:CompileCommand=compileonly,compiler.jeandle.bytecodeTranslate.JsrExecution::testWithGotoJSR
 *      -XX:+UseJeandleCompiler compiler.jeandle.bytecodeTranslate.TestJsr
 */

package compiler.jeandle.bytecodeTranslate;

import jdk.test.lib.Asserts;

public class TestJsr {
    public static void main(String[] args) throws Exception {
        try {
            SimpleJsrTests();
            ControlFlowJsrTests();
        } catch (Throwable t) {
            Asserts.fail("TestJsr failed", t);
        }
    }

    public static void SimpleJsrTests() {
        // See compiler.jeandle.bytecodeTranslate.JsrExecution::testWithJSR
        Asserts.assertEquals(testJSR(), 2);
        // See compiler.jeandle.bytecodeTranslate.JsrExecution::testWithNestedJSR
        Asserts.assertEquals(testNestedJSR(), 3);
        // See compiler.jeandle.bytecodeTranslate.JsrExecution::testWithSharedJSR
        Asserts.assertEquals(testSharedJSR(), 4);
    }

    public static void ControlFlowJsrTests() {
        // See compiler.jeandle.bytecodeTranslate.JsrExecution::testWithGotoJSR
        Asserts.assertEquals(testGotoJSR(), 9);
    }

    public static int testJSR() {
        return (int) JsrExecution.testWithJSR();
    }

    public static int testNestedJSR() {
        return (int) JsrExecution.testWithNestedJSR();
    }

    public static int testSharedJSR() {
        return (int) JsrExecution.testWithSharedJSR();
    }

    public static int testGotoJSR() {
        return (int) JsrExecution.testWithGotoJSR();
    }
}
