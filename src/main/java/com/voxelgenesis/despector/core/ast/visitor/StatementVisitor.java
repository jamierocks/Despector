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
package com.voxelgenesis.despector.core.ast.visitor;

import com.voxelgenesis.despector.core.ast.method.invoke.InvokeStatement;
import com.voxelgenesis.despector.core.ast.method.stmt.assign.InstanceFieldAssignment;
import com.voxelgenesis.despector.core.ast.method.stmt.assign.LocalAssignment;
import com.voxelgenesis.despector.core.ast.method.stmt.assign.StaticFieldAssignment;
import com.voxelgenesis.despector.core.ast.method.stmt.misc.Return;

public interface StatementVisitor extends AstVisitor {

    void visitReturn(Return stmt);

    void visitLocalAssignment(LocalAssignment stmt);

    void visitInvokeStatement(InvokeStatement stmt);

    void visitInstanceFieldAssignment(InstanceFieldAssignment stmt);

    void visitStaticFieldAssignment(StaticFieldAssignment stmt);

}