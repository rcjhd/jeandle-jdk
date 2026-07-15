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

package compiler.jeandle;

import java.lang.reflect.Method;

import jdk.test.lib.Asserts;
import jdk.test.whitebox.WhiteBox;

/*
 * @test
 * @summary Verify reconstruction of PEA-virtualized primitive arrays during deoptimization
 * @library /test/lib
 * @build jdk.test.whitebox.WhiteBox
 * @run driver jdk.test.lib.helpers.ClassFileInstaller jdk.test.whitebox.WhiteBox
 * @run main/othervm -Xbootclasspath/a:. -XX:+UnlockDiagnosticVMOptions
 *      -XX:+WhiteBoxAPI -Xbatch -Xcomp -XX:-TieredCompilation
 *      -XX:+UseJeandleCompiler -XX:CompileCommand=quiet
 *      -XX:CompileCommand=compileonly,compiler.jeandle.TestPrimitiveArrayDeoptimization::test*
 *      compiler.jeandle.TestPrimitiveArrayDeoptimization
 */

public class TestPrimitiveArrayDeoptimization {
    private static final WhiteBox WB = WhiteBox.getWhiteBox();

    public static void main(String[] args) throws Exception {
        Asserts.assertEQ(testBoolean(false), 5);
        assertCompiled("testBoolean");
        Asserts.assertEQ(testBoolean(true), 5);

        Asserts.assertEQ(testByte(false), 4);
        assertCompiled("testByte");
        Asserts.assertEQ(testByte(true), 4);

        Asserts.assertEQ(testChar(false), 4725);
        assertCompiled("testChar");
        Asserts.assertEQ(testChar(true), 4725);

        Asserts.assertEQ(testShort(false), 400);
        assertCompiled("testShort");
        Asserts.assertEQ(testShort(true), 400);

        Asserts.assertEQ(testInt(false), 469135);
        assertCompiled("testInt");
        Asserts.assertEQ(testInt(true), 469135);

        long expectedLong = 0x1122334455667788L - 7L;
        Asserts.assertEQ(testLong(false), expectedLong);
        assertCompiled("testLong");
        Asserts.assertEQ(testLong(true), expectedLong);

        int expectedFloatBits = Float.floatToRawIntBits(-2.25f);
        Asserts.assertEQ(testFloat(false), expectedFloatBits);
        assertCompiled("testFloat");
        Asserts.assertEQ(testFloat(true), expectedFloatBits);

        long expectedDoubleBits = Double.doubleToRawLongBits(-6.25d);
        Asserts.assertEQ(testDouble(false), expectedDoubleBits);
        assertCompiled("testDouble");
        Asserts.assertEQ(testDouble(true), expectedDoubleBits);
    }

    private static void assertCompiled(String name) throws Exception {
        Method method = TestPrimitiveArrayDeoptimization.class
                .getDeclaredMethod(name, boolean.class);
        Asserts.assertTrue(WB.isMethodCompiled(method),
                name + " must be compiled before forced deoptimization");
    }

    private static int testBoolean(boolean deopt) {
        boolean[] values = new boolean[4];
        values[0] = true;
        values[2] = true;
        deoptimize(deopt);
        return (values[0] ? 1 : 0)
                + (values[1] ? 2 : 0)
                + (values[2] ? 4 : 0)
                + (values[3] ? 8 : 0);
    }

    private static int testByte(boolean deopt) {
        byte[] values = new byte[4];
        values[0] = -7;
        values[2] = 11;
        deoptimize(deopt);
        return values[0] + values[1] + values[2] + values[3];
    }

    private static int testChar(boolean deopt) {
        char[] values = new char[4];
        values[0] = 'A';
        values[2] = '\u1234';
        deoptimize(deopt);
        return values[0] + values[1] + values[2] + values[3];
    }

    private static int testShort(boolean deopt) {
        short[] values = new short[4];
        values[0] = -300;
        values[2] = 700;
        deoptimize(deopt);
        return values[0] + values[1] + values[2] + values[3];
    }

    private static int testInt(boolean deopt) {
        int[] values = new int[4];
        values[0] = 1_234_567;
        values[2] = -765_432;
        deoptimize(deopt);
        return values[0] + values[1] + values[2] + values[3];
    }

    private static long testLong(boolean deopt) {
        long[] values = new long[4];
        values[0] = 0x1122334455667788L;
        values[2] = -7L;
        deoptimize(deopt);
        return values[0] + values[1] + values[2] + values[3];
    }

    private static int testFloat(boolean deopt) {
        float[] values = new float[4];
        values[0] = 1.25f;
        values[2] = -3.5f;
        deoptimize(deopt);
        return Float.floatToRawIntBits(
                values[0] + values[1] + values[2] + values[3]);
    }

    private static long testDouble(boolean deopt) {
        double[] values = new double[4];
        values[0] = 2.5d;
        values[2] = -8.75d;
        deoptimize(deopt);
        return Double.doubleToRawLongBits(
                values[0] + values[1] + values[2] + values[3]);
    }

    private static void deoptimize(boolean deopt) {
        if (deopt) {
            WB.deoptimizeAll();
        }
    }
}
