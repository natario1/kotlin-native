/*
 * Copyright 2010-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "Cleaner.h"

#include "Memory.h"

namespace {

struct CleanerImpl {
  ObjHeader header;
  KRef obj;
  KNativePtr cleanObj;
};

}  // namespace

RUNTIME_NOTHROW void DisposeCleaner(KRef thiz) {
#if KONAN_NO_EXCEPTIONS
    Kotlin_CleanerImpl_clean(thiz);
#else
    try {
        auto* cleaner = reinterpret_cast<CleanerImpl*>(thiz);
        auto* cleanObj = reinterpret_cast<KRef (*)(KRef, ObjHeader**)>(cleaner->cleanObj);
        ObjHolder retValue;
        cleanObj(cleaner->obj, retValue.slot());
    } catch (...) {
        // A trick to terminate with unhandled exception. This will print a stack trace
        // and write to iOS crash log.
        std::terminate();
    }
#endif
}
