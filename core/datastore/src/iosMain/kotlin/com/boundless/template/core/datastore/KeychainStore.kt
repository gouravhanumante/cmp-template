package com.boundless.template.core.datastore

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * Thin wrapper over the iOS keychain (`kSecClassGenericPassword`).
 * Items are `AfterFirstUnlockThisDeviceOnly`, so they survive relaunch but never sync to iCloud or a backup restore.
 */
@OptIn(ExperimentalForeignApi::class)
internal class KeychainStore(
    private val service: String,
) {
    fun read(account: String): String? =
        memScoped {
            val query =
                cfDictionary(
                    kSecClass to kSecClassGenericPassword,
                    kSecAttrService to cfString(service),
                    kSecAttrAccount to cfString(account),
                    kSecReturnData to kCFBooleanTrue,
                    kSecMatchLimit to kSecMatchLimitOne,
                )
            val result = alloc<CFTypeRefVar>()
            if (SecItemCopyMatching(query, result.ptr) != errSecSuccess) return@memScoped null

            val data = CFBridgingRelease(result.value) as? NSData ?: return@memScoped null
            NSString.create(data = data, encoding = NSUTF8StringEncoding) as String?
        }

    fun write(
        account: String,
        value: String,
    ) {
        delete(account)
        memScoped {
            val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return@memScoped
            val attributes =
                cfDictionary(
                    kSecClass to kSecClassGenericPassword,
                    kSecAttrService to cfString(service),
                    kSecAttrAccount to cfString(account),
                    kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly,
                    kSecValueData to cfRetain(data),
                )
            SecItemAdd(attributes, null)
        }
    }

    fun delete(account: String) {
        memScoped {
            val query =
                cfDictionary(
                    kSecClass to kSecClassGenericPassword,
                    kSecAttrService to cfString(service),
                    kSecAttrAccount to cfString(account),
                )
            SecItemDelete(query)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun MemScope.cfDictionary(vararg entries: Pair<CFStringRef?, CFTypeRef?>): CFDictionaryRef {
    val dictionary =
        CFDictionaryCreateMutable(
            null,
            entries.size.convert(),
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr,
        )!!
    entries.forEach { (key, value) -> CFDictionaryAddValue(dictionary, key, value) }
    defer { CFRelease(dictionary) }
    return dictionary
}

@OptIn(ExperimentalForeignApi::class)
private fun MemScope.cfString(value: String): CFStringRef? = cfRetain(value as NSString) as CFStringRef?

@OptIn(ExperimentalForeignApi::class)
private fun MemScope.cfRetain(value: Any): CFTypeRef? {
    val retained: CFTypeRef? = CFBridgingRetain(value)
    defer { retained?.let { CFRelease(it) } }
    return retained
}
