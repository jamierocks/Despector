/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
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
package org.spongepowered.despector.ast.members.insn;

import java.util.ArrayList;
import java.util.List;

public class Comment implements Statement {

    private final List<String> comment_text = new ArrayList<>();

    public Comment(List<String> text) {
        this.comment_text.addAll(text);
    }

    public List<String> getCommentText() {
        return this.comment_text;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
    }

    @Override
    public String toString() {
        if (this.comment_text.isEmpty()) {
            return "";
        }
        if (this.comment_text.size() == 1) {
            return "// " + this.comment_text.get(0);
        }
        StringBuilder str = new StringBuilder();
        str.append("/*\n");
        for (String line : this.comment_text) {
            str.append(" * ").append(line).append("\n");
        }
        str.append(" */");
        return str.toString();
    }

}
