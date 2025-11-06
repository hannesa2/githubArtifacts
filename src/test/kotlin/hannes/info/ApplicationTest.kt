package hannes.info

import hannes.info.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(ROOT_RESPONSE, bodyAsText())
        }
    }

    @Test
    fun testHtml() = testApplication {
        application {
            configureRouting()
        }
        client.get("/html-response").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(HTML_RESPONSE, bodyAsText())
        }
    }

    @Test
    fun testError() = testApplication {
        application {
            configureRouting()
        }
        client.get("/error-test").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("$ILLEGAL_STATE '${ERROR_RESPONSE}'", bodyAsText())
        }
    }
}
