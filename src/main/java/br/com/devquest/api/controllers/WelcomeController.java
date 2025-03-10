package br.com.devquest.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/welcome")
public class WelcomeController {

  @GetMapping()
  public String welcome() {
    return "Bem vindo ao devquest!";
  }

}
