package org.pageflow.base.exception.controller;

import org.pageflow.base.exception.data.WebNoSuchEntityException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : sechan
 */
@ControllerAdvice
public class WebExceptionController {
    
    @ExceptionHandler(WebNoSuchEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String HandleWebNoSuchEntityException(WebNoSuchEntityException e, Model model) {
        model.addAttribute("entityName", e.getEntityClass().getSimpleName());
        return "/error/404";
    }
}
