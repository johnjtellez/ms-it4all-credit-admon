package com.it4all.credit.admon.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it4all.credit.admon.data.dto.request.RulesDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;
import com.it4all.credit.admon.service.dynamyc.DynamicSQLService;

@RestController
@RequestMapping("/ms-it4all-credit/dynamic-sql")
public class DynamicController {
	
	static final String origen = "*";
	
	Logger logger = LoggerFactory.getLogger(DynamicController.class);
	
	@Autowired
	private DynamicSQLService dynamicSQLService;
	
	@CrossOrigin(origins = origen)
	@PostMapping("/execute-sql")
	public ResponseEntity<ResponseServiceDTO> executeDynamicQuery(@NonNull @RequestBody RulesDTO rules) {
		return ResponseEntity.ok(dynamicSQLService.executeDynamicQuery(rules));
	}

}
