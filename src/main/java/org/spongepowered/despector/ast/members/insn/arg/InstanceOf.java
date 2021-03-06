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
package org.spongepowered.despector.ast.members.insn.arg;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.despector.ast.generic.ClassTypeSignature;
import org.spongepowered.despector.ast.generic.TypeSignature;
import org.spongepowered.despector.ast.members.insn.InstructionVisitor;
import org.spongepowered.despector.util.TypeHelper;
import org.spongepowered.despector.util.serialization.AstSerializer;
import org.spongepowered.despector.util.serialization.MessagePacker;

import java.io.IOException;

/**
 * An instruction that checks if a value is of a certain instance.
 */
public class InstanceOf implements Instruction {

    private Instruction check;
    private String type;

    public InstanceOf(Instruction check, String type) {
        this.check = checkNotNull(check, "check");
        this.type = checkNotNull(type, "type");
        checkArgument(TypeHelper.IS_OBJECT_OR_ARRAY.test(this.type));
    }

    /**
     * Gets the value being tested.
     */
    public Instruction getCheckedValue() {
        return this.check;
    }

    /**
     * Sets the value being tested, must be an array or object.
     */
    public void setCheckedValue(Instruction val) {
        this.check = checkNotNull(val, "check");
    }

    /**
     * Gets the type being tested for.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the type being tested for, must be an array or object.
     */
    public String getTypeName() {
        return TypeHelper.descToType(this.type);
    }

    public void setType(String type) {
        checkArgument(TypeHelper.IS_OBJECT_OR_ARRAY.test(type));
        this.type = checkNotNull(type, "type");
    }

    @Override
    public TypeSignature inferType() {
        return ClassTypeSignature.BOOLEAN;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitInstanceOf(this);
        this.check.accept(visitor);
    }

    @Override
    public void writeTo(MessagePacker pack) throws IOException {
        pack.startMap(3);
        pack.writeString("id").writeInt(AstSerializer.STATEMENT_ID_INSTANCE_OF);
        pack.writeString("val");
        this.check.writeTo(pack);
        pack.writeString("type").writeString(this.type);
    }

    @Override
    public String toString() {
        return this.check + " instanceof " + getTypeName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof InstanceOf)) {
            return false;
        }
        InstanceOf insn = (InstanceOf) obj;
        return this.check.equals(insn.check) && this.type.equals(insn.type);
    }

}
