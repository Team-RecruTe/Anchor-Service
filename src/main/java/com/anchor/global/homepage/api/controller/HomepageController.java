package com.anchor.global.homepage.api.controller;

import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

  @GetMapping({"","/"})
  public String viewHomepage(Model model, HttpSession httpSession){
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    if(user != null) {
      model.addAttribute("user", user);
    }
    return "홈페이지";
  }

}
