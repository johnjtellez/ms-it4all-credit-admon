package com.it4all.credit.admon.controller;

import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.it4all.credit.admon.data.dto.model.BusinessRulesDTO;
import com.it4all.credit.admon.data.dto.request.FilterPageDTO;
import com.it4all.credit.admon.data.dto.request.PageDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;
import com.it4all.credit.admon.service.businessrules.BusinessRulesService;

@RestController
@RequestMapping("/ms-it4all-credit/businessrules")
public class BusinessRulesController {

	static final String origen = "*";

	Logger logger = LoggerFactory.getLogger(BusinessRulesController.class);

	@Autowired
	private BusinessRulesService businessrulesSvr;

	@CrossOrigin(origins = origen)
	@RequestMapping(value="/getById", method=RequestMethod.GET)
	public ResponseEntity<ResponseServiceDTO> getById(Long id) {
		return ResponseEntity.ok(businessrulesSvr.getById(id));
	}

@CrossOrigin(origins = origen)
	@GetMapping(value="/getAll")
	public ResponseEntity<ResponseServiceDTO> getAll() {
		return ResponseEntity.ok(businessrulesSvr.getAll());
	}

	@CrossOrigin(origins = origen)
	@RequestMapping(value="/getByPage", method=RequestMethod.POST)
	public ResponseEntity<ResponseServiceDTO> getByPage(@NonNull @RequestBody PageDTO pageDto) {
		return ResponseEntity.ok(businessrulesSvr.getByPage(pageDto));
	}

	@CrossOrigin(origins = origen)
	@PostMapping("/getFilters")
	public ResponseEntity<ResponseServiceDTO> getFilters(@NonNull @RequestBody FilterPageDTO request) {
		return ResponseEntity.ok(businessrulesSvr.getFilters(request));
	}

	@CrossOrigin(origins = origen)
	@PostMapping("/create")
	public ResponseEntity<ResponseServiceDTO> create(@NonNull @RequestBody BusinessRulesDTO businessrules) {
		return ResponseEntity.status(HttpStatus.OK).body(businessrulesSvr.create(businessrules));
	}

	@CrossOrigin(origins = origen)
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public ResponseEntity<ResponseServiceDTO> update(@NonNull @RequestBody BusinessRulesDTO businessrules) {
		return ResponseEntity.status(HttpStatus.OK).body(businessrulesSvr.update(businessrules));
	}

	@CrossOrigin(origins = origen)
	@RequestMapping(value="/deleteById", method=RequestMethod.GET)
	public ResponseEntity<ResponseServiceDTO> eliminarById(Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(businessrulesSvr.deleteById(id));
	}

	@CrossOrigin(origins = origen)
	@PostMapping(value="/deleteAnyById")
	public ResponseEntity<ResponseServiceDTO> eliminarAnyById(@NonNull @RequestBody List<Long> idList) {
		return ResponseEntity.status(HttpStatus.OK).body(businessrulesSvr.deleteAnyId(idList));
	}
    @CrossOrigin(origins = origen)
	@PostMapping("/upload")
	public ResponseEntity<ResponseServiceDTO> uploadCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(businessrulesSvr.upload(file));
	}
	
    @CrossOrigin(origins = origen)
    @GetMapping("/download")
    public ResponseEntity<String> downloadToCsv() {
        StringWriter writer = new StringWriter();
        ResponseServiceDTO response = businessrulesSvr.download(writer);

        String csvContent = writer.toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "businessrules.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
    }
}
