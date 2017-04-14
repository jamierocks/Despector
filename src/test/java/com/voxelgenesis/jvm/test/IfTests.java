/*
 * The MIT License (MIT)
 *
 * Copyright (c) Despector <https://despector.voxelgenesis.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelgenesis.jvm.test;

import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import com.voxelgenesis.despector.jvm.JvmMain;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.spongepowered.test.util.TestMethodBuilder;

public class IfTests {

    @Test
    public void testSimple() {
        TestMethodBuilder builder = new TestMethodBuilder("test_mth", "(Z)V");
        MethodVisitor mv = builder.getGenerator();
        Label start = new Label();
        mv.visitLabel(start);
        Label end = new Label();
        mv.visitVarInsn(ILOAD, 0);
        mv.visitJumpInsn(IFEQ, end);
        mv.visitMethodInsn(INVOKESTATIC, "Ljava/lang/Test;", "body", "()V", false);
        mv.visitLabel(end);
        mv.visitInsn(RETURN);
        mv.visitLocalVariable("a", "Z", null, start, end, 0);

        String insn = CoreTestHelper.getAsString(builder.finish(), JvmMain.JAVA_LANG, "test_mth");
        String good = "if (a) {\n"
                + "    Test.body();\n"
                + "}";
        Assert.assertEquals(good, insn);
    }
}