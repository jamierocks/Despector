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
package org.spongepowered.despector.ast.members.insn.branch.condition;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import org.spongepowered.despector.ast.members.insn.InstructionVisitor;
import org.spongepowered.despector.util.serialization.AstSerializer;
import org.spongepowered.despector.util.serialization.MessagePacker;

import java.io.IOException;
import java.util.List;

/**
 * A condition for the or of several other conditions.
 */
public class OrCondition extends Condition {

    private final List<Condition> args;

    public OrCondition(Condition... args) {
        this.args = Lists.newArrayList(checkNotNull(args, "args"));
    }

    public OrCondition(List<Condition> args) {
        this.args = checkNotNull(args, "args");
    }

    /**
     * Gets the operands of this condition.
     */
    public List<Condition> getOperands() {
        return this.args;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visitOrCondition(this);
        for (Condition c : this.args) {
            c.accept(visitor);
        }
    }

    @Override
    public void writeTo(MessagePacker pack) throws IOException {
        pack.startMap(2);
        pack.writeString("id").writeInt(AstSerializer.CONDITION_ID_OR);
        pack.writeString("args").startArray(this.args.size());
        for (Condition arg : this.args) {
            arg.writeTo(pack);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.args.size(); i++) {
            sb.append(this.args.get(i));
            if (i < this.args.size() - 1) {
                sb.append(" || ");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrCondition)) {
            return false;
        }
        OrCondition and = (OrCondition) o;
        if (and.getOperands().size() != this.args.size()) {
            return false;
        }
        for (int i = 0; i < this.args.size(); i++) {
            if (!this.args.get(i).equals(and.getOperands().get(i))) {
                return false;
            }
        }
        return true;
    }

}
