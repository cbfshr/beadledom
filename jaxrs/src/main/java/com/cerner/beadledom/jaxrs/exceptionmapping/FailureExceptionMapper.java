package com.cerner.beadledom.jaxrs.exceptionmapping;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.cerner.beadledom.json.common.model.JsonError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.spi.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ExceptionMapper} for the {@link Failure} family of exceptions.
 *
 * <p>{@link Failure} is a family of exceptions that Resteasy will throw when errors occur
 * internally within the framework. The intention of this exception mapper is to restructure the
 * exceptions into a standard JSON format.
 *
 * @see <a href="http://docs.jboss.org/resteasy/docs/3.1.4.Final/userguide/html_single/index.html#builtinException">Resteasy Built-in Internally-Thrown Exceptions</a>
 *
 * @author Cal Fisher
 * @since 2.6
 */
@Provider
public class FailureExceptionMapper implements ExceptionMapper<Failure> {

  private static final Logger logger = LoggerFactory.getLogger(FailureExceptionMapper.class);

  /**
   * Maps an unhandled {@link Failure} to a {@link Response}.
   *
   * @param exception the {@link Failure} exception that was not handled
   * @return a {@link Response} object with a {@code Status} of the {@link Failure} or 500 if the
   *     exception's response is null, a content-type of 'application/json', and a {@link JsonError}
   *     entity containing details about the unhandled exception in JSON format.
   */
  @Override
  public Response toResponse(Failure exception) {

    int code = exception.getErrorCode();
    Response.Status status = Response.Status.fromStatusCode(code);

    if (status == null) {
      code = INTERNAL_SERVER_ERROR.getStatusCode();
      status = INTERNAL_SERVER_ERROR;
    }

    if (code >= 400 && code < 500) {
      logger.warn("An unhandled exception was thrown.", exception);
    } else if (code >= 500) {
      logger.error("An unhandled exception was thrown.", exception);
    }

    Response response = exception.getResponse();
    if (response != null) {
      return Response
          .fromResponse(response)
          .status(status)
          .entity(
              JsonError.builder()
                  .code(code)
                  .message(status.getReasonPhrase())
                  .build())
          .type(MediaType.APPLICATION_JSON)
          .build();
    } else {
      return Response
          .status(status)
          .entity(
              JsonError.builder()
                  .code(code)
                  .message(status.getReasonPhrase())
                  .build())
          .type(MediaType.APPLICATION_JSON)
          .build();
    }
  }
}
