/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package kotlin.native.internal

import kotlinx.cinterop.*

public interface Cleaner

@NoReorderFields
@ExportTypeInfo("theCleanerImplTypeInfo")
private class CleanerImpl(
    private val obj: Any?,
    private val cleanObj: CPointer<CFunction<*>>,
): Cleaner {}

@ExportForCompiler
private fun createCleanerImpl(argument: Any?, block: CPointer<CFunction<*>>): Cleaner {
    return CleanerImpl(argument, block)
}

/**
 * If [block] throws an exception, the entire program terminates.
 */
@TypedIntrinsic(IntrinsicType.CREATE_CLEANER)
external fun <T> createCleaner(argument: T, @VolatileLambda block: (T) -> Unit): Cleaner
