package stsa.kotlin_htmx.pages

import io.ktor.server.application.*
import kotlinx.html.*
import stsa.kotlin_htmx.models.UserSession

fun FlowContent.renderLoginForm(error: Boolean = false) {
    h2 { +"Iniciar Sesión" }

    form(action = "/login", method = FormMethod.post) {
        p {
            +"Usuario: "
            textInput(name = "username") { required = true }
        }
        p {
            +"Contraseña: "
            passwordInput(name = "password") { required = true }
        }
        p {
            submitInput { value = "Entrar" }
        }

        if (error) {
            p { style = "color:red"; +"Credenciales inválidas" }
        }
    }
}
