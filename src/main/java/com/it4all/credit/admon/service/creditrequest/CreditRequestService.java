package com.it4all.credit.admon.service.creditrequest;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.dao.DataAccessException; 
import org.springframework.data.domain.PageRequest; 
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.multipart.MultipartFile;

import com.it4all.credit.admon.config.BaseCodeResponseConfig;
import com.it4all.credit.admon.data.context.RuleContext;
import com.it4all.credit.admon.data.dto.model.CreditRequestDTO;
import com.it4all.credit.admon.data.dto.request.FilterDTO;
import com.it4all.credit.admon.data.dto.request.FilterPageDTO;
import com.it4all.credit.admon.data.dto.request.PageDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;
import com.it4all.credit.admon.data.mapper.GenericMapper;
import com.it4all.credit.admon.data.model.CreditRequest;
import com.it4all.credit.admon.data.model.repository.creditrequest.CreditRequestRepository;
import com.it4all.credit.admon.data.model.specification.EntitySpecifications;
import com.it4all.credit.admon.service.CsvService;
import com.it4all.credit.admon.service.CsvServiceConfig;
import com.it4all.credit.admon.service.AuditoryService;
import com.it4all.credit.admon.service.TokenService;

@Service
public class CreditRequestService implements ICreditRequestService{
	Logger logger = LoggerFactory.getLogger(CreditRequestService.class);
	@Autowired
private KieContainer kieContainer;

	@Autowired
	private CreditRequestRepository repository; 

@Autowired
private AuditoryService auditoryService;

@Autowired
private TokenService tokenService;
	@Autowired
	private BaseCodeResponseConfig baseCodeResponse;

	@Autowired
	GenericMapper<CreditRequestDTO, CreditRequest> mapper;
	private final CsvService<CreditRequest> creditrequestCsvService;

	@Autowired
	public CreditRequestService(CsvServiceConfig csvServiceConfig, CreditRequestRepository creditrequestRepository) {
	    this.creditrequestCsvService = csvServiceConfig
	    		.csvService(creditrequestRepository, CreditRequest.class);
    }

	@Override
	public ResponseServiceDTO getById(Long id) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			CreditRequest creditrequest = repository.findById(id).get();
			response.setObject(mapper.entityToDTO(creditrequest));
			response.setSuccess(true);
			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
			response.setDescriptionResponse(this.baseCodeResponse
					.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseServiceDTO getAll() {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			List<CreditRequestDTO> listCreditRequests = repository.findAll().stream()
			.map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());
			response.setObject(listCreditRequests);
			response.setSuccess(true);
			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
		} catch (Exception e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseServiceDTO getByPage(PageDTO pageDto) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			Pageable pageable = null;

			if (pageDto.getNumberPage() == 0)
				response.setCountTotal(repository.count());

			if (pageDto.getOrdenAscending())
				pageable = PageRequest.of(pageDto.getNumberPage().intValue(), pageDto.getRegisterByPage().intValue(),
						Sort.by(pageDto.getOrderBy()).ascending());
			else
				pageable = PageRequest.of(pageDto.getNumberPage().intValue(), pageDto.getRegisterByPage().intValue(),
						Sort.by(pageDto.getOrderBy()).descending());

			List<CreditRequestDTO> listCreditRequests = repository.getByPage(pageable).stream()			.map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());

			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setObject(listCreditRequests);
			response.setSuccess(true);
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_QUERY_PAGE_SQL_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL__QUERY_PAGE_SQL_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());

		} catch (Exception e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseServiceDTO getFilters(FilterPageDTO request) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			Pageable pageable = null;

			List<FilterDTO> filters = request.getFilters();

	        Specification<CreditRequest> spec = EntitySpecifications.buildSpecification(filters);

			if (request.getPage().getNumberPage() == 0) {
				response.setCountTotal(repository.count(spec));
			}

			if (request.getPage().getOrdenAscending()) {
				pageable = PageRequest.of(request.getPage().getNumberPage().intValue(), request.getPage().getRegisterByPage().intValue(),
						Sort.by(request.getPage().getOrderBy()).ascending());
			}
			else {
				pageable = PageRequest.of(request.getPage().getNumberPage().intValue(), request.getPage().getRegisterByPage().intValue(),
						Sort.by(request.getPage().getOrderBy()).descending());
			}

			List<CreditRequestDTO> listCreditRequests = repository.findAll(spec, pageable).stream().map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());

			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setObject(listCreditRequests);
			response.setSuccess(true);
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_QUERY_PAGE_SQL_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL__QUERY_PAGE_SQL_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());

		} catch (Exception e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseServiceDTO create(CreditRequestDTO creditrequest) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			response = createTX(creditrequest);
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_QUERY_PAGE_SQL_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL__QUERY_PAGE_SQL_DESCRIPTION));
			response.setDescriptionException(e.getMessage() + " - " + e.getRootCause().getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error("Error en creación del registro.");
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}

	@Transactional(rollbackFor = Exception.class)
	private ResponseServiceDTO createTX(CreditRequestDTO creditrequest) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		creditrequest = mapper.entityToDTO(
				repository.save( mapper.dtoToEntity(creditrequest)));
		try {
			String userName = tokenService.getAuthenticatedUsername();
			auditoryService.create(creditrequest, userName);
		response.setObject(creditrequest);
		response.setSuccess(true);
		response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
		response.setDescriptionResponse(
				this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
		} catch (Exception e) {
			logger.error("Incidente actualizando el registro de auditoria.");
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
				this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
				this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseServiceDTO update(CreditRequestDTO creditrequest) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			response = updateTX(creditrequest);
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_QUERY_PAGE_SQL_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL__QUERY_PAGE_SQL_DESCRIPTION));
			response.setDescriptionException(e.getMessage() + " - " + e.getRootCause().getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error("Incidente actualizando el registro.");
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}

	@Transactional(rollbackFor = Exception.class)
	private ResponseServiceDTO updateTX(CreditRequestDTO creditrequest) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		CreditRequest aux = null;
		aux = repository.findById(creditrequest.getId()).get();
		try {
		String userName = tokenService.getAuthenticatedUsername();
		auditoryService.update(creditrequest, aux, userName);
		if (aux != null) {
			creditrequest.setId(aux.getId());
			creditrequest = mapper.entityToDTO(
					repository.save(mapper.dtoToEntity(creditrequest)));
			response.setObject(creditrequest);
			response.setSuccess(true);
			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
		} else {
			response.setObject(aux);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
		}
		} catch (Exception e) {
			logger.error("Incidente actualizando el registro de auditoria.");
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
				this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
				this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseServiceDTO deleteById(Long id) {
		ResponseServiceDTO respuesta = new ResponseServiceDTO();
		try {
			respuesta = deleteTX(id);
		} catch (DataAccessException e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			respuesta.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage() + " - " + e.getRootCause().getMessage());
			logger.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
			respuesta.setDescriptionResponse(this.baseCodeResponse
					.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			respuesta.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return respuesta;
	}

	@Transactional(rollbackFor = Exception.class)
	private ResponseServiceDTO deleteTX(Long id) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		repository.deleteById(id);
		response.setObject(null);
		response.setSuccess(true);
		response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
		response.setDescriptionResponse(
				this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
		return response;
	}

	@Override
	public ResponseServiceDTO deleteAnyId(List<Long> idList) {
		ResponseServiceDTO respuesta = new ResponseServiceDTO();
		try {
			respuesta = deleteTXAny(idList);
		} catch (DataAccessException e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			respuesta.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage() + " - " + e.getRootCause().getMessage());
			logger.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
			respuesta.setDescriptionResponse(this.baseCodeResponse
					.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			respuesta.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return respuesta;
	}

	@Transactional(rollbackFor = Exception.class)
	private ResponseServiceDTO deleteTXAny(List<Long> idList) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		List<CreditRequest> creditrequestList = new ArrayList<>();
		try {
	        for (Long id : idList) {
	        	CreditRequest creditrequest = repository.findById(id).get();
	        	creditrequestList.add(creditrequest);
	        	repository.deleteById(id);
	        }
			String userName = tokenService.getAuthenticatedUsername();
	        auditoryService.delete(creditrequestList, userName);
			response.setObject(null);
			response.setSuccess(true);
			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
				this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage() + " - " + e.getRootCause().getMessage());
			logger.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
			response.setDescriptionResponse(this.baseCodeResponse
					.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {

			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		}
		return response;
	}
	@Override
	public ResponseServiceDTO upload(MultipartFile file) {
		ResponseServiceDTO respuesta = new ResponseServiceDTO();
		try {
			String taskId = UUID.randomUUID().toString();
			// Almacena temporalmente el archivo en el sistema de archivos
			Path tempFile = Files.createTempFile("upload-", ".csv");
			Files.write(tempFile, file.getBytes());

			// Procesa el archivo CSV de forma asincrónica
			creditrequestCsvService.processCsv(tempFile, taskId);
			respuesta.setObject(null);
			respuesta.setSuccess(true);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			respuesta.setDescriptionResponse(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION);
			respuesta.setDescriptionException(null);
		} catch (DataAccessException e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			respuesta.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage() + " - " + e.getRootCause().getMessage());
			logger.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
			respuesta.setDescriptionResponse(this.baseCodeResponse
					.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			respuesta.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} 
		return respuesta;
	}     
          
	@Override
	public ResponseServiceDTO download(Writer writer) {
		ResponseServiceDTO respuesta = new ResponseServiceDTO();
		try {
			creditrequestCsvService.exportCsv(writer);
			respuesta.setObject(null);
			respuesta.setSuccess(true);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			respuesta.setDescriptionResponse(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION);
			respuesta.setDescriptionException(null);
		} catch (DataAccessException e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			respuesta.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage() + " - " + e.getRootCause().getMessage());
			logger.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
			respuesta.setDescriptionResponse(this.baseCodeResponse
					.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {
			respuesta.setObject(null);
			respuesta.setSuccess(false);
			respuesta.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			respuesta.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			respuesta.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} 
		return respuesta;
	}     
}