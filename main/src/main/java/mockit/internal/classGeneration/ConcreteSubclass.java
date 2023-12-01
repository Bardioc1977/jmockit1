/*
 * Copyright (c) 2006 Rogério Liesenfeld
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.classGeneration;

import static mockit.asm.jvmConstants.Access.PUBLIC;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.asm.classes.ClassReader;
import mockit.asm.classes.ClassVisitor;

/**
 * Generates a concrete subclass for an {@code abstract} base class.
 */
public final class ConcreteSubclass<T> extends ImplementationClass<T> {
    public ConcreteSubclass(@Nonnull Class<?> baseClass) {
        super(baseClass);
    }

    @Nonnull
    @Override
    protected ClassVisitor createMethodBodyGenerator(@Nonnull ClassReader typeReader) {
        return new BaseSubclassGenerator(sourceClass, typeReader, null, generatedClassName, false) {
            @Override
            protected void generateMethodImplementation(String className, int access, @Nonnull String name,
                    @Nonnull String desc, @Nullable String signature, @Nullable String[] exceptions) {
                mw = cw.visitMethod(PUBLIC, name, desc, signature, exceptions);
                generateEmptyImplementation(desc);
            }
        };
    }
}
