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
package org.spongepowered.despector.emitter.kotlin.condition;

import org.spongepowered.despector.ast.members.insn.arg.NumberCompare;
import org.spongepowered.despector.ast.members.insn.branch.condition.CompareCondition;
import org.spongepowered.despector.ast.members.insn.branch.condition.CompareCondition.CompareOperator;
import org.spongepowered.despector.emitter.ConditionEmitter;
import org.spongepowered.despector.emitter.EmitterContext;

public class KotlinCompareConditionEmitter implements ConditionEmitter<CompareCondition> {

    @Override
    public void emit(EmitterContext ctx, CompareCondition compare) {
        if (compare.getLeft() instanceof NumberCompare) {
            NumberCompare cmp = (NumberCompare) compare.getLeft();
            ctx.emit(cmp.getLeftOperand(), null);
            ctx.markWrapPoint();
            if (compare.getOperator() == CompareOperator.EQUAL) {
                ctx.printString(" === ");
            } else {
                ctx.printString(compare.getOperator().asString());
            }
            ctx.emit(cmp.getRightOperand(), null);
            return;
        }
        ctx.emit(compare.getLeft(), null);
        ctx.markWrapPoint();
        if (compare.getOperator() == CompareOperator.EQUAL) {
            ctx.printString(" === ");
        } else {
            ctx.printString(compare.getOperator().asString());
        }
        ctx.emit(compare.getRight(), null);
    }

}
