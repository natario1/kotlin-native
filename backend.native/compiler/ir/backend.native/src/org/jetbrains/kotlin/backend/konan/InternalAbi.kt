/*
 * Copyright 2010-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */
package org.jetbrains.kotlin.backend.konan

import org.jetbrains.kotlin.backend.konan.descriptors.synthesizedName
import org.jetbrains.kotlin.descriptors.konan.CompiledKlibModuleOrigin
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.NaiveSourceBasedFileEntryImpl
import org.jetbrains.kotlin.ir.util.addFile
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Sometimes we need to reference symbols that are not declared in metadata.
 * For example, symbol might be declared during lowering.
 * In case of compiler caches, this means that it is not accessible as Lazy IR
 * and we have to explicitly add an external declaration in current module.
 */
internal class InternalAbi(private val context: Context) {
    /**
     * File that stores all internal ABI declarations/references.
     *
     * We have to store such declarations in top-level to avoid mangling that
     * makes referencing harder.
     * A bit better solution is to add files with proper packages, but it is impossible
     * during FileLowering (hello, ConcurrentModificationException).
     */
    private lateinit var internalAbiFile: IrFile

    fun init(module: IrModuleFragment) {
        internalAbiFile = module.addFile(
                NaiveSourceBasedFileEntryImpl("internal"), FqName("kotlin.native.caches.abi")
        )
    }

    /**
     * Adds external [function] from [origin] to list to external references.
     */
    fun reference(function: IrFunction, origin: CompiledKlibModuleOrigin) {
        assert(function.isExternal) { "Function that represents external ABI should be marked as external" }
        function.parent = internalAbiFile
        internalAbiFile.declarations += function
        context.llvmImports.add(origin)
    }

    /**
     * Generate name for declaration that will be a part of internal ABI.
     */
    fun getMangledNameFor(declarationName: String, parent: IrDeclarationParent): Name {
        val prefix = parent.fqNameForIrSerialization
        return "$prefix.$declarationName".synthesizedName
    }

    /**
     * Adds [function] to a list of publicly available symbols.
     */
    fun declare(function: IrFunction) {
        function.parent = internalAbiFile
        internalAbiFile.declarations += function
    }

    companion object {
        /**
         * Allows to distinguish external declarations to internal ABI.
         */
        val INTERNAL_ABI_ORIGIN = object : IrDeclarationOriginImpl("INTERNAL_ABI") {}
    }
}