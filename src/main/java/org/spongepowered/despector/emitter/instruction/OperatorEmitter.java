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

import org.spongepowered.despector.ast.generic.TypeSignature;
import org.spongepowered.despector.ast.members.insn.arg.operator.Operator;
import org.spongepowered.despector.emitter.EmitterContext;
import org.spongepowered.despector.emitter.InstructionEmitter;

public class OperatorEmitter implements InstructionEmitter<Operator> {

    @Override
    public void emit(EmitterContext ctx, Operator arg, TypeSignature type) {
        if (arg.getLeftOperand() instanceof Operator) {
            Operator left = (Operator) arg.getLeftOperand();
            if (arg.getOperator().getPrecedence() > left.getOperator().getPrecedence()) {
                ctx.printString("(");
            }
            ctx.emit(arg.getLeftOperand(), null);
            if (arg.getOperator().getPrecedence() > left.getOperator().getPrecedence()) {
                ctx.printString(")");
            }
        } else {
            ctx.emit(arg.getLeftOperand(), null);
        }
        ctx.markWrapPoint();
        ctx.printString(" " + arg.getOperator().getSymbol() + " ");
        if (arg.getRightOperand() instanceof Operator) {
            Operator right = (Operator) arg.getRightOperand();
            if (arg.getOperator().getPrecedence() > right.getOperator().getPrecedence()) {
                ctx.printString("(");
            }
            ctx.emit(arg.getRightOperand(), null);
            if (arg.getOperator().getPrecedence() > right.getOperator().getPrecedence()) {
                ctx.printString(")");
            }
        } else {
            ctx.emit(arg.getRightOperand(), null);
        }
    }

}
