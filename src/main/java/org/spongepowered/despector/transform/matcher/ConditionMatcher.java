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
package org.spongepowered.despector.transform.matcher;

import org.spongepowered.despector.ast.Locals.LocalInstance;
import org.spongepowered.despector.ast.members.insn.branch.condition.Condition;
import org.spongepowered.despector.transform.matcher.condition.BooleanConditionMatcher;
import org.spongepowered.despector.transform.matcher.condition.ConditionReferenceMatcher;

public interface ConditionMatcher<T extends Condition> {

    T match(MatchContext ctx, Condition cond);

    default T match(Condition cond) {
        return match(MatchContext.create(), cond);
    }

    default boolean matches(MatchContext ctx, Condition cond) {
        return match(ctx, cond) != null;
    }

    static final ConditionMatcher<?> ANY = new Any();

    static BooleanConditionMatcher.Builder bool() {
        return new BooleanConditionMatcher.Builder();
    }

    static ConditionReferenceMatcher references(LocalInstance local) {
        return new ConditionReferenceMatcher(local);
    }

    static ConditionReferenceMatcher references(String ctx) {
        return new ConditionReferenceMatcher(ctx);
    }

    public static class Any implements ConditionMatcher<Condition> {

        Any() {
        }

        @Override
        public Condition match(MatchContext ctx, Condition stmt) {
            return stmt;
        }

    }

}
