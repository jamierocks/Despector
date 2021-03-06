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
package org.spongepowered.despector.decompiler.method.graph.create;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.spongepowered.despector.ast.Locals;
import org.spongepowered.despector.ast.Locals.LocalInstance;
import org.spongepowered.despector.decompiler.method.PartialMethod;
import org.spongepowered.despector.decompiler.method.graph.GraphProducerStep;
import org.spongepowered.despector.decompiler.method.graph.data.TryCatchMarkerType;
import org.spongepowered.despector.decompiler.method.graph.data.opcode.OpcodeBlock;
import org.spongepowered.despector.decompiler.method.graph.data.opcode.TryCatchMarkerOpcodeBlock;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A graph producer that creates opcode blocks for try-catch statements. It
 * inserts also inserts marker blocks to denote the start and end of try blocks
 * and the start of catch blocks.
 */
public class TryCatchGraphProducerStep implements GraphProducerStep {

    @SuppressWarnings("unchecked")
    @Override
    public void collectBreakpoints(PartialMethod partial, Set<Integer> break_points) {
        List<TryCatchBlockNode> tryCatchBlocks = partial.getAsmNode().tryCatchBlocks;
        Map<Label, Integer> label_indices = partial.getLabelIndices();
        List<AbstractInsnNode> instructions = partial.getOpcodes();
        Locals locals = partial.getLocals();

        for (TryCatchBlockNode tc : tryCatchBlocks) {
            break_points.add(label_indices.get(tc.start.getLabel()));
            break_points.add(label_indices.get(tc.end.getLabel()));
            break_points.add(label_indices.get(tc.handler.getLabel()));

            LocalInstance local = null;
            for (int i = label_indices.get(tc.handler.getLabel()) + 1; i < instructions.size(); i++) {
                AbstractInsnNode next = instructions.get(i);
                if (next instanceof LabelNode) {
                    local = locals.findLocal(((LabelNode) next).getLabel(), "L" + tc.type + ";");
                    if (local == null) {
                        local = locals.findLocal(((LabelNode) next).getLabel(), "Ljava/lang/RuntimeException;");
                        if (local == null) {
                            local = locals.findLocal(((LabelNode) next).getLabel(), "Ljava/lang/Exception;");
                        }
                    }
                    break;
                }
            }
            if (local != null && local.getEnd() < instructions.size() - 1) {
                break_points.add(local.getEnd());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void formEdges(PartialMethod partial, Map<Integer, OpcodeBlock> blocks, List<Integer> sorted_break_points, List<OpcodeBlock> block_list) {
        List<TryCatchBlockNode> tryCatchBlocks = partial.getAsmNode().tryCatchBlocks;
        Map<Label, Integer> label_indices = partial.getLabelIndices();

        for (int i = tryCatchBlocks.size() - 1; i >= 0; i--) {
            TryCatchBlockNode tc = tryCatchBlocks.get(i);
            OpcodeBlock start = blocks.get(sorted_break_points.get(sorted_break_points.indexOf(label_indices.get(tc.start.getLabel())) + 1));
            OpcodeBlock end = blocks.get(sorted_break_points.get(sorted_break_points.indexOf(label_indices.get(tc.end.getLabel())) + 1));
            OpcodeBlock handler = blocks.get(sorted_break_points.get(sorted_break_points.indexOf(label_indices.get(tc.handler.getLabel())) + 1));
            TryCatchMarkerOpcodeBlock start_marker = new TryCatchMarkerOpcodeBlock(TryCatchMarkerType.START, tc);
            TryCatchMarkerOpcodeBlock end_marker = new TryCatchMarkerOpcodeBlock(TryCatchMarkerType.END, tc);
            TryCatchMarkerOpcodeBlock handler_marker = new TryCatchMarkerOpcodeBlock(TryCatchMarkerType.CATCH, tc);
            start_marker.setEndMarker(end_marker);
            end_marker.setStartMarker(start_marker);
            handler_marker.setStartMarker(start_marker);
            handler_marker.setEndMarker(end_marker);
            block_list.add(block_list.indexOf(start), start_marker);
            block_list.add(block_list.indexOf(end), end_marker);
            block_list.add(block_list.indexOf(handler), handler_marker);
        }
    }

}
