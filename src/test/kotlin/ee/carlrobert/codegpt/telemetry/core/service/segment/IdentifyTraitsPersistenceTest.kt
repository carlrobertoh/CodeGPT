package ee.carlrobert.codegpt.telemetry.core.service.segment

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.intellij.util.io.write
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.createTempFile
import kotlin.io.path.readText
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private const val NOT_JSON = "}NOT]:JSON{"

class IdentifyTraitsPersistenceTest {
  private val gson = Gson()
  private val persistence = IdentifyTraitsPersistence.INSTANCE
  private val identifyTraits = IdentifyTraits("locale", "timezone", "os", "version", "distribution")

  @BeforeEach
  fun setUp() {
    persistence.identifyTraits = null
    IdentifyTraitsPersistence.FILE = createTempFile()
  }

  @Test
  fun `get returns null when file does not exist`() {
    IdentifyTraitsPersistence.FILE = Path(" ")
    assertNull(persistence.get())
  }

  @Test
  fun `get throws JsonSyntaxException when file contains malformed JSON`() {
    IdentifyTraitsPersistence.FILE.write(NOT_JSON)
    assertFailsWith<JsonSyntaxException> {
      persistence.get()
    }
  }

  @Test
  fun `set saves the event to the file overwriting it`() {
    IdentifyTraitsPersistence.FILE.write(NOT_JSON)
    persistence.set(identifyTraits)
    assertEquals(IdentifyTraitsPersistence.FILE.readText(), gson.toJson(identifyTraits))
  }

  @Test
  fun `set saves the event to the file when file does not exist`() {
    persistence.set(identifyTraits)
    assertEquals(IdentifyTraitsPersistence.FILE.readText(), gson.toJson(identifyTraits))
  }

  @Test
  fun `get returns the deserialized event`() {
    IdentifyTraitsPersistence.FILE.write(gson.toJson(identifyTraits))
    assertEquals(identifyTraits, persistence.get())
  }

  @Test
  fun `set throws IOException when file cannot be written and returns false`() {
    IdentifyTraitsPersistence.FILE = IdentifyTraitsPersistence.FILE.resolve(" xyz ")
    assertEquals(persistence.set(identifyTraits), false)
  }

}
