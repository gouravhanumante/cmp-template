package com.boundless.template.app.desktop

import java.nio.file.Files
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Arjun double-clicks the app while it is already open on his PC.
class SingleInstanceTest {
    @Test
    fun aSecondCopyDoesNotStartAndBringsTheFirstToTheFront() {
        val dir = Files.createTempDirectory("single-instance").toFile()
        val activated = CountDownLatch(1)
        val first = assertNotNull(SingleInstance.claim(dir) { activated.countDown() })
        first.use {
            assertNull(SingleInstance.claim(dir) { error("the second copy must not become the running one") })
            assertTrue(activated.await(5, TimeUnit.SECONDS))
        }
    }

    @Test
    fun onceTheFirstCopyHasQuitTheNextLaunchRunsNormally() {
        val dir = Files.createTempDirectory("single-instance").toFile()
        SingleInstance.claim(dir) {}!!.close()
        assertNotNull(SingleInstance.claim(dir) {}).close()
    }
}
