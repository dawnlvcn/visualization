package com.citi.vdict.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

	@RequestMapping(value = { "/", "/index" })
	public String index() {
		return "index";
	}

	@RequestMapping(value = { "/svg"})
	public String pack() {
		return "pack";
	}
	
	
	@RequestMapping(value = { "/force"})
	public String force() {
		return "force";
	}
}