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
package org.spongepowered.despector.emitter.instruction;

import org.spongepowered.despector.ast.members.MethodEntry;
import org.spongepowered.despector.ast.members.insn.arg.Instruction;
import org.spongepowered.despector.ast.members.insn.arg.field.FieldArg;
import org.spongepowered.despector.ast.members.insn.arg.field.InstanceFieldArg;
import org.spongepowered.despector.ast.members.insn.arg.field.StaticFieldArg;
import org.spongepowered.despector.ast.members.insn.assign.FieldAssignment;
import org.spongepowered.despector.ast.members.insn.assign.InstanceFieldAssignment;
import org.spongepowered.despector.ast.members.insn.assign.StaticFieldAssignment;
import org.spongepowered.despector.ast.members.insn.function.StaticMethodInvoke;
import org.spongepowered.despector.ast.members.insn.misc.Return;
import org.spongepowered.despector.ast.type.TypeEntry;
import org.spongepowered.despector.emitter.EmitterContext;
import org.spongepowered.despector.emitter.InstructionEmitter;
import org.spongepowered.despector.util.TypeHelper;

public class StaticMethodInvokeEmitter implements InstructionEmitter<StaticMethodInvoke> {

    @Override
    public void emit(EmitterContext ctx, StaticMethodInvoke arg, String type) {
        String owner = TypeHelper.descToType(arg.getOwner());
        if (arg.getMethodName().startsWith("access$") && ctx.getType() != null) {
            // synthetic accessor
            // we resolve these to the field that they are accessing directly
            TypeEntry owner_type = ctx.getType().getSource().get(owner);
            if (owner_type != null) {
                MethodEntry accessor = owner_type.getStaticMethod(arg.getMethodName());
                if (accessor.getReturnType().equals("V")) {
                    // setter
                    FieldAssignment assign = (FieldAssignment) accessor.getInstructions().getStatements().get(0);
                    FieldAssignment replacement = null;
                    if (arg.getParams().length == 2) {
                        replacement = new InstanceFieldAssignment(assign.getFieldName(), assign.getFieldDescription(), assign.getOwnerType(),
                                arg.getParams()[0], arg.getParams()[1]);
                    } else {
                        replacement = new StaticFieldAssignment(assign.getFieldName(), assign.getFieldDescription(), assign.getOwnerType(),
                                arg.getParams()[0]);
                    }
                    ctx.emit(replacement, true);
                    return;
                }
                // getter
                Return ret = (Return) accessor.getInstructions().getStatements().get(0);
                FieldArg getter = (FieldArg) ret.getValue().get();
                FieldArg replacement = null;
                if (arg.getParams().length == 1) {
                    replacement = new InstanceFieldArg(getter.getFieldName(), getter.getTypeDescriptor(), getter.getOwner(), arg.getParams()[0]);
                } else {
                    replacement = new StaticFieldArg(getter.getFieldName(), getter.getTypeDescriptor(), getter.getOwner());
                }
                ctx.emit(replacement, null);
                return;
            }
        }
        if (ctx.getType() == null || !owner.equals(ctx.getType().getName())) {
            ctx.emitTypeName(owner);
            ctx.printString(".");
        }
        ctx.printString(arg.getMethodName());
        ctx.printString("(");
        // TODO get param types if we have the ast
        for (int i = 0; i < arg.getParams().length; i++) {
            Instruction param = arg.getParams()[i];
            ctx.emit(param, null);
            if (i < arg.getParams().length - 1) {
                ctx.printString(", ");
            }
        }
        ctx.printString(")");
    }

}