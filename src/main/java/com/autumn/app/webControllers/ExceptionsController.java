package com.autumn.app.webControllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.Principal;


@ControllerAdvice
public class ExceptionsController {

    @ExceptionHandler(Exception.class)
    public String error(Principal principal, Model model){
        model.addAttribute("principal", principal);
        return "error/genericError";
    }

}
