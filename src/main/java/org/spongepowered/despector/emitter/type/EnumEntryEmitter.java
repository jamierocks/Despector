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
package org.spongepowered.despector.emitter.type;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.spongepowered.despector.ast.AccessModifier;
import org.spongepowered.despector.ast.Annotation;
import org.spongepowered.despector.ast.generic.ClassTypeSignature;
import org.spongepowered.despector.ast.members.FieldEntry;
import org.spongepowered.despector.ast.members.MethodEntry;
import org.spongepowered.despector.ast.members.insn.Statement;
import org.spongepowered.despector.ast.members.insn.assign.StaticFieldAssignment;
import org.spongepowered.despector.ast.members.insn.function.New;
import org.spongepowered.despector.ast.type.EnumEntry;
import org.spongepowered.despector.ast.type.TypeEntry;
import org.spongepowered.despector.ast.type.TypeEntry.InnerClassInfo;
import org.spongepowered.despector.config.ConfigManager;
import org.spongepowered.despector.emitter.AstEmitter;
import org.spongepowered.despector.emitter.EmitterContext;
import org.spongepowered.despector.util.TypeHelper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EnumEntryEmitter implements AstEmitter<EnumEntry> {

    @Override
    public boolean emit(EmitterContext ctx, EnumEntry type) {

        for (Annotation anno : type.getAnnotations()) {
            ctx.printIndentation();
            ctx.emit(anno);
            ctx.newLine();
        }
        ctx.printIndentation();
        ctx.printString(type.getAccessModifier().asString());
        if (type.getAccessModifier() != AccessModifier.PACKAGE_PRIVATE) {
            ctx.printString(" ");
        }
        ctx.printString("enum ");
        InnerClassInfo inner_info = null;
        if (type.isInnerClass() && ctx.getOuterType() != null) {
            inner_info = ctx.getOuterType().getInnerClassInfo(type.getName());
        }
        if (inner_info != null) {
            ctx.printString(inner_info.getSimpleName());
        } else {
            String name = type.getName().replace('/', '.');
            if (name.indexOf('.') != -1) {
                name = name.substring(name.lastIndexOf('.') + 1, name.length());
            }
            name = name.replace('$', '.');
            ctx.printString(name);
        }
        if (!type.getInterfaces().isEmpty()) {
            ctx.printString(" implements ");
            for (int i = 0; i < type.getInterfaces().size(); i++) {
                ctx.emitType(type.getInterfaces().get(i));
                if (i < type.getInterfaces().size() - 1) {
                    ctx.printString(" ", ctx.getFormat().insert_space_before_comma_in_superinterfaces);
                    ctx.printString(",");
                    ctx.printString(" ", ctx.getFormat().insert_space_after_comma_in_superinterfaces);
                    ctx.markWrapPoint();
                }
            }
        }
        ctx.printString(" ", ctx.getFormat().insert_space_before_opening_brace_in_type_declaration);
        ctx.printString("{");
        ctx.newLine(ctx.getFormat().blank_lines_before_first_class_body_declaration + 1);
        ctx.indent();

        // we look through the class initializer to find the enum constant
        // initializers so that we can emit those specially before the rest of
        // the class contents.

        MethodEntry clinit = type.getStaticMethod("<clinit>");
        List<Statement> remaining = Lists.newArrayList();
        Set<String> found = Sets.newHashSet();
        if (clinit != null && clinit.getInstructions() != null) {
            Iterator<Statement> initializers = clinit.getInstructions().getStatements().iterator();
            boolean first = true;
            while (initializers.hasNext()) {
                Statement next = initializers.next();
                if (!(next instanceof StaticFieldAssignment)) {
                    break;
                }
                StaticFieldAssignment assign = (StaticFieldAssignment) next;
                if (assign.getFieldName().equals("$VALUES")) {
                    continue;
                }
                if (!TypeHelper.descToType(assign.getOwnerType()).equals(type.getName()) || !(assign.getValue() instanceof New)) {
                    remaining.add(assign);
                    break;
                }
                if (!first) {
                    ctx.printString(",");
                    ctx.newLine();
                }
                New val = (New) assign.getValue();
                ctx.printIndentation();
                ctx.printString(assign.getFieldName());
                found.add(assign.getFieldName());
                if (val.getParameters().length != 2) {
                    ctx.printString("(");
                    List<String> args = TypeHelper.splitSig(val.getCtorDescription());
                    for (int i = 2; i < val.getParameters().length; i++) {
                        ctx.emit(val.getParameters()[i], ClassTypeSignature.of(args.get(i)));
                        if (i < val.getParameters().length - 1) {
                            ctx.printString(", ");
                        }
                    }
                    ctx.printString(")");
                }
                first = false;
            }
            if (!first) {
                ctx.printString(";");
                ctx.newLine();
            }
            // We store any remaining statements to be emitted later
            while (initializers.hasNext()) {
                remaining.add(initializers.next());
            }
        }
        if (!found.isEmpty()) {
            ctx.newLine();
        }

        if (!type.getStaticFields().isEmpty()) {
            boolean at_least_one = false;
            for (FieldEntry field : type.getStaticFields()) {
                if (field.isSynthetic()) {
                    // Skip the values array.
                    if(ConfigManager.getConfig().emitter.emit_synthetics) {
                        ctx.printIndentation();
                        ctx.printString("// Synthetic");
                        ctx.newLine();
                    } else {
                        continue;
                    }
                }
                if (found.contains(field.getName())) {
                    // Skip the fields for any of the enum constants that we
                    // found earlier.
                    continue;
                }
                ctx.printIndentation();
                ctx.emit(field);
                ctx.printString(";");
                ctx.newLine();
                at_least_one = true;
            }
            if (at_least_one) {
                ctx.newLine();
            }
        }
        if (!remaining.isEmpty()) {
            // if we found any additional statements in the class initializer
            // while looking for enum constants we emit them here
            ctx.printIndentation();
            ctx.printString("static {");
            ctx.newLine();
            ctx.indent();
            for (Statement stmt : remaining) {
                ctx.printIndentation();
                ctx.emit(stmt, true);
                ctx.newLine();
            }
            ctx.dedent();
            ctx.printIndentation();
            ctx.printString("}");
            ctx.newLine();
        }
        if (!type.getStaticMethods().isEmpty()) {
            for (MethodEntry mth : type.getStaticMethods()) {
                if (mth.getName().equals("valueOf") || mth.getName().equals("values") || mth.getName().equals("<clinit>")) {
                    // Can skip these boilerplate methods and the class
                    // initializer
                    continue;
                } else if (mth.isSynthetic()) {
                    if(ConfigManager.getConfig().emitter.emit_synthetics) {
                        ctx.printIndentation();
                        ctx.printString("// Synthetic");
                        if (mth.isBridge()) {
                            ctx.printString(" - Bridge");
                        }
                        ctx.newLine();
                    } else {
                        continue;
                    }
                }
                ctx.emit(mth);
                ctx.newLine();
                ctx.newLine();
            }
        }
        if (!type.getFields().isEmpty()) {
            for (FieldEntry field : type.getFields()) {
                if (field.isSynthetic()) {
                    if(ConfigManager.getConfig().emitter.emit_synthetics) {
                        ctx.printIndentation();
                        ctx.printString("// Synthetic");
                        ctx.newLine();
                    } else {
                        continue;
                    }
                }
                ctx.printIndentation();
                ctx.emit(field);
                ctx.printString(";");
                ctx.newLine();
            }
            ctx.newLine();
        }
        if (!type.getMethods().isEmpty()) {
            for (MethodEntry mth : type.getMethods()) {
                if (mth.isSynthetic()) {
                    if(ConfigManager.getConfig().emitter.emit_synthetics) {
                        ctx.printIndentation();
                        ctx.printString("// Synthetic");
                        if (mth.isBridge()) {
                            ctx.printString(" - Bridge");
                        }
                        ctx.newLine();
                    } else {
                        continue;
                    }
                }
                if (mth.getName().equals("<init>") && mth.getInstructions().getStatements().size() == 2) {
                    // If the initializer contains only two statement (which
                    // will be the invoke of the super constructor and the void
                    // return) then we can
                    // skip emitting it
                    continue;
                }
                ctx.emit(mth);
                ctx.newLine();
                ctx.newLine();
            }
        }

        Collection<InnerClassInfo> inners = type.getInnerClasses();
        for (InnerClassInfo inner : inners) {
            if (inner.getOuterName() == null || !inner.getOuterName().equals(type.getName())) {
                continue;
            }
            ctx.setOuterType(type);
            TypeEntry inner_type = type.getSource().get(inner.getName());
            ctx.newLine();
            ctx.emit(inner_type);
            ctx.setType(type);
            ctx.setOuterType(null);
        }

        ctx.dedent();
        ctx.printIndentation();
        ctx.printString("}");
        ctx.newLine();
        return true;
    }

}
