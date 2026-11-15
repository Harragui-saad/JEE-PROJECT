package com.ticketflow.bookingservice.exceptions;

import feign.FeignException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Maps domain exceptions to RFC 9457 problem responses (validation errors are handled by the parent class). */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BookingRuleException.class)
    ProblemDetail handleRule(BookingRuleException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UpstreamUnavailableException.class)
    ProblemDetail handleUpstream(UpstreamUnavailableException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    //missing resources
    @ExceptionHandler(FeignException.NotFound.class)
    ProblemDetail handleFeignNotFound(FeignException.NotFound ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Resource not found on upstream service");
    }

    // Map timeouts/connection drops and open circuit rejections to 503 ---
    @ExceptionHandler({RetryableException.class, CallNotPermittedException.class})
    ProblemDetail handleServiceUnavailable(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "Upstream service unavailable or circuit breaker open");
    }
}