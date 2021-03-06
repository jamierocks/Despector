/*
 * The MIT License (MIT)
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.spongepowered.despector.ast.members.insn.function;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.despector.ast.members.insn.InstructionVisitor;
import org.spongepowered.despector.ast.members.insn.Statement;
import org.spongepowered.despector.ast.members.insn.arg.Instruction;
import org.spongepowered.despector.util.serialization.AstSerializer;
import org.spongepowered.despector.util.serialization.MessagePacker;

import java.io.IOException;

/**
 * A statement wrapping an instruction. Typically used for method invokes that
 * have no return value and therefore are called as instructions rather than as
 * part of another statement.
 */
public class InvokeStatement implements Statement {

    private Instruction inner;

    public InvokeStatement(Instruction inner) {
        this.inner = checkNotNull(inner, "instruction");
    }

    /**
     * Gets the instruction being invoked in this statement.
     */
    public Instruction getInstruction() {
        return this.inner;
    }

    /**
     * Sets the instruction being invoked in this statement.
     */
    public void setInstruction(Instruction insn) {
        this.inner = checkNotNull(insn, "instruction");
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        this.inner.accept(visitor);
    }

    @Override
    public String toString() {
        return this.inner.toString();
    }

    @Override
    public void writeTo(MessagePacker pack) throws IOException {
        pack.startMap(2);
        pack.writeString("id").writeInt(AstSerializer.STATEMENT_ID_INVOKE);
        pack.writeString("inner");
        this.inner.writeTo(pack);
    }

}
