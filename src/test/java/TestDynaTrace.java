package com.dynatrace.vertx.aspects;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.eventbus.impl.BaseMessage;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.impl.DefaultHttpClient;
import org.vertx.java.core.http.impl.DefaultHttpClientRequest;
import org.vertx.java.core.http.impl.DefaultHttpServerRequest;
import org.vertx.java.core.http.impl.DefaultHttpServerResponse;
import org.vertx.java.core.impl.DefaultContext;
import org.vertx.java.core.net.impl.ServerID;

/**
 * Ensures that calls to Vert-x also performing the necessary calls in order
 * for the Dynatrace Servlet Sensor to pick up these calls out of the box
 * without having to define custom entry points.<br />
 * <br />
 * Furthermore, since the nature of request handling is being dealt with when
 * using Vert-x asynchronously, the Dynatrace Agent SDK is being used to create
 * proper sub paths.
 *
 * @author reinhard.pilz@dynatrace.com
 *
 */
@SuppressWarnings("rawtypes")
public privileged aspect VertxAspect {

	private static final Logger LOGGER =
			Logger.getLogger(VertxAspect.class.getName());

	private static final Level LEVEL = Level.FINEST;

	static {
		LOGGER.setLevel(Level.INFO);
	}

	/**
	 * Around executions of {@link ServerConnection.handleRequest} we are
	 * creating an artificial call to a Servlet, which allows the
	 * Dynatrace Servlet Sensor to pick up the request as a Pure Path
	 *
	 * @param req the request object
	 * @param resp the response object
	 */
	void around(
		final DefaultHttpServerRequest req,
		final DefaultHttpServerResponse resp
	):
		execution(
			void org.vertx.java.core.http.impl.ServerConnection.handleRequest(
				DefaultHttpServerRequest,
				DefaultHttpServerResponse
			)
		)
		&&
		args(req, resp)
	{
		final Runnable proceedRunnable = new Runnable() {
			@Override
			public void run() {
				proceed(req, resp);
			}
		};
		try {
			new VertxServlet(req, resp, proceedRunnable).execute();
		} catch (final Throwable t) {
			LOGGER.log(Level.WARNING, "Servlet Invocation failed",	t);
		}
	}

}
