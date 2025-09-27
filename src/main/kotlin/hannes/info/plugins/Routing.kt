package hannes.info.plugins

import hannes.info.Customer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*

fun Application.configureRouting() {

    val customerStorage = mutableListOf<Customer>()

    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }

    routing {
        staticResources(
            "/content",
            "mycontent"
        )

        customerRouting()

        listOrdersRoute()
        getOrderRoute()
        totalizeOrderRoute()

        get("/") {
            call.respondText("get:/ Hello World. Try to do a /test1 to see http")
        }
        get("/error-test") {
            throw IllegalStateException("Too Busy")
        }
        get("/test1") {
            val text = "<h1>Hello From Ktor web site</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }

        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val customer = customerStorage.find { it.id == id } ?: return@get call.respondText(
                "No customer with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(customer)
        }
        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerStorage.removeIf { it.id == id }) {
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
