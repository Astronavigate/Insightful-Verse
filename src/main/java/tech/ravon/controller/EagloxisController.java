package tech.ravon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EagloxisController {
    @RequestMapping("/index")
    public ModelAndView Index() {
        ModelAndView mv = new ModelAndView("index");
        return  mv;
    }

    @RequestMapping("/about")
    public ModelAndView About() {
        ModelAndView mv = new ModelAndView("About");
        return  mv;
    }

    @RequestMapping("/products")
    public ModelAndView Products() {
        ModelAndView mv = new ModelAndView("Products");
        return  mv;
    }

    @RequestMapping("/support")
    public ModelAndView Support() {
        ModelAndView mv = new ModelAndView("Support");
        return  mv;
    }

    @RequestMapping("/myInfo")
    public ModelAndView MyInfo() {
        ModelAndView mv = new ModelAndView("MyInfo");
        return mv;
    }

    @RequestMapping("/login")
    public ModelAndView Login() {
        ModelAndView mv = new ModelAndView("Login");
        mv.addObject("finUrl", "index");
        return mv;
    }

    @RequestMapping("/register")
    public ModelAndView Register() {
        ModelAndView mv = new ModelAndView("Register");
        mv.addObject("finUrl", "index");
        return mv;
    }

    @RequestMapping("/unregister")
    public ModelAndView Unregister() {
        ModelAndView mv = new ModelAndView("Unregister");
        mv.addObject("finUrl", "index");
        return mv;
    }

    @RequestMapping("/updateInfo")
    public ModelAndView UpdateInfo() {
        ModelAndView mv = new ModelAndView("UpdateInfo");
        mv.addObject("finUrl", "myInfo");
        return mv;
    }
}
