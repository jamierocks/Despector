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
package org.spongepowered.despector.ast.members;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import org.spongepowered.despector.ast.AccessModifier;
import org.spongepowered.despector.ast.Annotation;
import org.spongepowered.despector.ast.AnnotationType;
import org.spongepowered.despector.ast.AstEntry;
import org.spongepowered.despector.ast.SourceSet;
import org.spongepowered.despector.ast.generic.ClassTypeSignature;
import org.spongepowered.despector.ast.generic.MethodSignature;
import org.spongepowered.despector.ast.generic.TypeSignature;
import org.spongepowered.despector.ast.members.insn.StatementBlock;
import org.spongepowered.despector.util.TypeHelper;
import org.spongepowered.despector.util.serialization.AstSerializer;
import org.spongepowered.despector.util.serialization.MessagePacker;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a method declaration in a type.
 * 
 * <p>Only methods which actually have method bodies are present in a type, not
 * inherited types from super classes or super interfaces.</p>
 * 
 * <p>The exceptions is obviously interfaces which will contain methods which
 * are declared as well as default methods. But still no inherited methods.</p>
 */
public class MethodEntry extends AstEntry {

    protected AccessModifier access = AccessModifier.PACKAGE_PRIVATE;

    protected String owner;
    protected String name;
    protected String signature;

    protected boolean is_abstract;
    protected boolean is_final;
    protected boolean is_static;
    protected boolean is_synthetic;
    protected boolean is_bridge;

    protected TypeSignature return_type;
    protected final List<String> param_types = Lists.newArrayList();

    protected StatementBlock instructions = null;

    protected MethodSignature sig;

    protected final Map<AnnotationType, Annotation> annotations = new LinkedHashMap<>();

    public MethodEntry(SourceSet source) {
        super(source);
    }

    public AccessModifier getAccessModifier() {
        return this.access;
    }

    public void setAccessModifier(AccessModifier access) {
        this.access = checkNotNull(access, "AccessModifier");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type containing this method.
     */
    public String getOwner() {
        return this.owner;
    }

    public String getOwnerName() {
        return TypeHelper.descToType(this.owner);
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isAbstract() {
        return this.is_abstract;
    }

    public void setAbstract(boolean state) {
        this.is_abstract = state;
    }

    public boolean isFinal() {
        return this.is_final;
    }

    public void setFinal(boolean state) {
        this.is_final = state;
    }

    /**
     * Gets if this method is static.
     */
    public boolean isStatic() {
        return this.is_static;
    }

    public void setStatic(boolean state) {
        this.is_static = state;
    }

    public boolean isSynthetic() {
        return this.is_synthetic;
    }

    public void setSynthetic(boolean state) {
        this.is_synthetic = state;
    }

    public boolean isBridge() {
        return this.is_bridge;
    }

    public void setBridge(boolean state) {
        this.is_bridge = state;
    }

    /**
     * Gets the return type of this method. Will never be null but may represent
     * void.
     */
    public TypeSignature getReturnType() {
        return this.return_type;
    }

    public String getReturnTypeName() {
        return this.return_type.getName();
    }

    /**
     * Gets the obfuscated signature of the method.
     */
    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
        this.return_type = ClassTypeSignature.of(TypeHelper.getRet(signature));
        this.param_types.clear();
        this.param_types.addAll(TypeHelper.splitSig(signature));
    }

    /**
     * Gets the type entries of this methods parameters.
     */
    public List<String> getParamTypes() {
        return this.param_types;
    }

    public MethodSignature getMethodSignature() {
        return this.sig;
    }

    public void setMethodSignature(MethodSignature sig) {
        this.sig = sig;
    }

    public StatementBlock getInstructions() {
        if (this.is_abstract) {
            return null;
        }
        return this.instructions;
    }

    public void setInstructions(StatementBlock block) {
        this.instructions = block;
    }

    public Annotation getAnnotation(AnnotationType type) {
        return this.annotations.get(type);
    }

    public Collection<Annotation> getAnnotations() {
        return this.annotations.values();
    }

    public void addAnnotation(Annotation anno) {
        this.annotations.put(anno.getType(), anno);
    }

    @Override
    public void writeTo(MessagePacker pack) throws IOException {
        pack.startMap(15);
        pack.writeString("id").writeInt(AstSerializer.ENTRY_ID_METHOD);
        pack.writeString("access").writeInt(this.access.ordinal());
        pack.writeString("owner").writeString(this.owner);
        pack.writeString("name").writeString(this.name);
        pack.writeString("signature").writeString(this.signature);
        pack.writeString("abstract").writeBool(this.is_abstract);
        pack.writeString("final").writeBool(this.is_final);
        pack.writeString("static").writeBool(this.is_static);
        pack.writeString("synthetic").writeBool(this.is_synthetic);
        pack.writeString("bridge").writeBool(this.is_bridge);
        pack.writeString("returntype");
        this.return_type.writeTo(pack);
        pack.writeString("paramtypes").startArray(this.param_types.size());
        for (String param : this.param_types) {
            pack.writeString(param);
        }
        pack.writeString("methodsignature");
        this.sig.writeTo(pack);
        pack.writeString("instructions");
        this.instructions.writeTo(pack);
        pack.writeString("annotations").startArray(this.annotations.size());
        for (Annotation anno : this.annotations.values()) {
            anno.writeTo(pack);
        }
    }

    @Override
    public String toString() {
        return "Method: " + this.name + " " + this.signature;
    }

}
