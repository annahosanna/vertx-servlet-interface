package org.collokia.kommon.vertx

import io.vertx.ext.auth.User
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session
import nl.komponents.kovenant.deferred
import org.collokia.kommon.jdk.strings.*
import java.net.URI


public fun Session.putSafely(key: String, value: Any?) {
    if (value == null) {
        remove(key)
    } else {
        put(key, value)
    }
}

public fun Session.removeSafely(key: String) {
    remove<Any?>(key)
}

public fun RoutingContext.fullyQualifiedUrl(fromUrlOnThisServer: String): String {
    val requestUri: URI = URI(request().absoluteURI())

    val requestScheme: String = run {
        request().getHeader("X-Forwarded-Proto") let { scheme: String? ->
            val temp = if (scheme == null || scheme.isEmpty()) {
                requestUri.getScheme()
            } else {
                scheme
            }
            temp
        }
    }
    val requestHost: String = run {
        request().getHeader("X-Forwarded-Host") let { host: String? ->
            val hostWithPossiblePort = if (host == null || host.isEmpty()) {
                requestUri.getHost()
            } else {
                host
            }

            hostWithPossiblePort.substringBefore(':')
        }
    }
    val requestPort: String = run {
        val rawPort = requestUri.getPort()
        val tempPort = if (rawPort == 0) {
            val calculated = if ("https" == requestScheme) 443 else 80
            calculated
        } else {
            rawPort
        }

        request().getHeader("X-Forwarded-Port") let  { port: String? ->
            val tempPort = if (port == null || port.isEmpty()) {
                tempPort
            } else {
                port
            }
            if (requestScheme == "https" && tempPort == "443") {
                ""
            } else if (requestScheme == "http" && tempPort == "80") {
                ""
            } else ":$tempPort"
        }
    }
    return "$requestScheme://$requestHost$requestPort${fromUrlOnThisServer.mustStartWith('/')}"
}
