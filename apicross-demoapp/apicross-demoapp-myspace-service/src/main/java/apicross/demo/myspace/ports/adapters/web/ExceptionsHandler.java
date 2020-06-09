package apicross.demo.myspace.ports.adapters.web;

import apicross.demo.common.models.ProblemDescription;
import apicross.demo.myspace.domain.CompetitionNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionsHandler extends apicross.demo.common.utils.ExceptionsHandler {
    @ExceptionHandler({CompetitionNotFoundException.class})
    public ResponseEntity<ProblemDescription> handle(CompetitionNotFoundException e, HttpServletRequest request) {
        return resourceNotFoundResponse(request);
    }
}
