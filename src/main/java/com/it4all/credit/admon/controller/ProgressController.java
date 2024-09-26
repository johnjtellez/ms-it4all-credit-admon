package com.it4all.credit.admon.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.it4all.credit.admon.data.dto.request.FilterPageDTO;
import com.it4all.credit.admon.data.dto.request.PageDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;
import com.it4all.credit.admon.service.progress.ProgressService;

@RestController
@RequestMapping("/ms-it4all-credit/progress")
public class ProgressController {

	static final String origen = "*";

    private final ProgressService progressService;

    @Autowired
    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

	@CrossOrigin
	@GetMapping("/consulta")
	public ResponseEntity<ResponseServiceDTO> consultarBodega(
			@RequestParam("taskId") String taskId)  {
		return ResponseEntity.ok(progressService.getProgress(taskId));
	}
	
	@CrossOrigin(origins = origen)
	@RequestMapping(value="/getById", method=RequestMethod.GET)
	public ResponseEntity<ResponseServiceDTO> getById(Long id) {

		return ResponseEntity.ok(progressService.getById(id));
	}

	@CrossOrigin(origins = origen)
	//@RequestMapping(value="/getAll", method=RequestMethod.GET)
	@GetMapping(value="/getAll")
	public ResponseEntity<ResponseServiceDTO> getAll() {

		return ResponseEntity.ok(progressService.getAll());
	}

	@CrossOrigin(origins = origen)
	@RequestMapping(value="/getByPage", method=RequestMethod.POST)
	public ResponseEntity<ResponseServiceDTO> getByPage(@NonNull @RequestBody PageDTO pageDto) {

		return ResponseEntity.ok(progressService.getByPage(pageDto));
	}

	@CrossOrigin(origins = origen)
	@PostMapping("/getFilters")
	public ResponseEntity<ResponseServiceDTO> getFilters(@NonNull @RequestBody FilterPageDTO request) {
		return ResponseEntity.ok(progressService.getFilters(request));
	}

}
