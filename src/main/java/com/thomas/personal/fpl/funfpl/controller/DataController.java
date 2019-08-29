package com.thomas.personal.fpl.funfpl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thomas.personal.fpl.funfpl.bl.DataBl;

@Controller
@RequestMapping(path = "/data")
public class DataController {

	private DataBl dataBl;

	@Autowired
	public DataController(DataBl dataBl) {
		this.dataBl = dataBl;
	}

	@GetMapping("/updateData")
	@ResponseBody
	public void updateData() {
		dataBl.updateData();
	}
}
