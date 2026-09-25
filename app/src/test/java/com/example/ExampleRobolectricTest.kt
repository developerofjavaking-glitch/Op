package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("QR Scanner", appName)
  }

  @Test
  fun `generate valid qr code bitmap`() {
    val bitmap = com.example.util.QrCodeGenerator.generateBitmap("https://example.com", size = 200)
    org.junit.Assert.assertNotNull(bitmap)
    assertEquals(200, bitmap!!.width)
    assertEquals(200, bitmap.height)
  }
}
