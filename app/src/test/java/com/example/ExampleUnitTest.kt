package com.example

import com.example.data.model.QrType
import com.example.util.QrCodeGenerator
import com.example.util.QrParser
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun qrParser_parsesUrls() {
    val result1 = QrParser.parse("https://github.com/google")
    assertEquals(QrType.URL, result1.type)
    assertEquals("github.com", result1.title)
    assertEquals("https://github.com/google", result1.url)

    val result2 = QrParser.parse("www.example.com")
    assertEquals(QrType.URL, result2.type)
    assertEquals("https://www.example.com", result2.url)
  }

  @Test
  fun qrParser_parsesWifi() {
    val wifiText = "WIFI:T:WPA;S:HomeNetwork;P:SuperSecret123;H:false;;"
    val result = QrParser.parse(wifiText)
    assertEquals(QrType.WIFI, result.type)
    assertEquals("HomeNetwork", result.wifiSsid)
    assertEquals("SuperSecret123", result.wifiPassword)
    assertEquals("WPA", result.wifiAuthType)
  }

  @Test
  fun qrParser_parsesPhoneAndEmail() {
    val phoneResult = QrParser.parse("tel:+1234567890")
    assertEquals(QrType.PHONE, phoneResult.type)
    assertEquals("+1234567890", phoneResult.phoneNumber)

    val emailResult = QrParser.parse("mailto:support@example.com?subject=Help")
    assertEquals(QrType.EMAIL, emailResult.type)
    assertEquals("support@example.com", emailResult.emailAddress)
    assertEquals("Help", emailResult.emailSubject)
  }

  @Test
  fun qrParser_parsesContact() {
    val vCard = "BEGIN:VCARD\nVERSION:3.0\nFN:Alice Smith\nTEL:555-1234\nEMAIL:alice@example.com\nEND:VCARD"
    val result = QrParser.parse(vCard)
    assertEquals(QrType.CONTACT, result.type)
    assertEquals("Alice Smith", result.contactName)
    assertEquals("555-1234", result.contactPhone)
    assertEquals("alice@example.com", result.contactEmail)
  }
}
