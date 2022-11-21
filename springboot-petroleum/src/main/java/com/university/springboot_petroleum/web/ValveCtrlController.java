package com.university.springboot_petroleum.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.university.springboot_petroleum.domain.ValveDomain;

//阀门控制
@Controller
public class ValveCtrlController {
	
	@RequestMapping(value="/api/valveCtrl", method =  RequestMethod.PUT)
    @ResponseBody
	public String valveCtrl(@RequestBody ValveDomain valve) {
		
		return "ok";
	}

}
