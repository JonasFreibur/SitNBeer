package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class BarController{

    @RequestMapping("/bar")
	public String index() {
		return "bar";
	}

}