package com.it4all.credit.admon.service.custom;

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
import com.it4all.credit.admon.data.dto.model.CustomDTO;
import com.it4all.credit.admon.data.dto.request.FilterDTO;
import com.it4all.credit.admon.data.dto.request.FilterPageDTO;
import com.it4all.credit.admon.data.dto.request.PageDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;
import com.it4all.credit.admon.data.mapper.GenericMapper;
import com.it4all.credit.admon.data.model.Custom;
import com.it4all.credit.admon.data.model.repository.custom.CustomRepository;
import com.it4all.credit.admon.data.model.specification.EntitySpecifications;
import com.it4all.credit.admon.service.CsvService;
import com.it4all.credit.admon.service.CsvServiceConfig;
import com.it4all.credit.admon.service.AuditoryService;
import com.it4all.credit.admon.service.TokenService;

@Service
public class CustomService implements ICustomService{
	Logger logger = LoggerFactory.getLogger(CustomService.class);
	@Autowired
private KieContainer kieContainer;

	@Autowired
	private CustomRepository repository; 

@Autowired
private AuditoryService auditoryService;

@Autowired
private TokenService tokenService;
	@Autowired
	private BaseCodeResponseConfig baseCodeResponse;

	@Autowired
	GenericMapper<CustomDTO, Custom> mapper;
	private final CsvService<Custom> customCsvService;

	@Autowired
	public CustomService(CsvServiceConfig csvServiceConfig, CustomRepository customRepository) {
	    this.customCsvService = csvServiceConfig
	    		.csvService(customRepository, Custom.class);
    }

	@Override
	public ResponseServiceDTO getById(Long id) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			Custom custom = repository.findById(id).get();
			response.setObject(mapper.entityToDTO(custom));
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
			List<CustomDTO> listCustoms = repository.findAll().stream()
			.map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());
			response.setObject(listCustoms);
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

			List<CustomDTO> listCustoms = repository.getByPage(pageable).stream()			.map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());

			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setObject(listCustoms);
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

	        Specification<Custom> spec = EntitySpecifications.buildSpecification(filters);

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

			List<CustomDTO> listCustoms = repository.findAll(spec, pageable).stream().map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());

			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setObject(listCustoms);
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
   @Transactional
   public ResponseServiceDTO create(CustomDTO customDTO) {
       List<String> resultList = new ArrayList<>();
		ResponseServiceDTO response = new ResponseServiceDTO();

      Custom custom = mapper.dtoToEntity(customDTO);

       try {
	        RuleContext ruleContext = new RuleContext();
	        ruleContext.setRuleToExecute("Custom");
	        KieSession kieSession = kieContainer.newKieSession();
	        kieSession.setGlobal("resultList", resultList);
	        kieSession.insert(custom);
	        kieSession.insert(ruleContext);
	        kieSession.fireAllRules();
	        kieSession.dispose();
			response.setObject(null);
			response.setSuccess(true);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setDescriptionException(null);
	        if (!resultList.isEmpty()) {
	    		response.setObject(resultList);
	    		response.setSuccess(false);
	    		response.setCodeResponse(
	    				this.baseCodeResponse.getDescription(IT4ALL_RULE_VALIDATION_CODE));
	    		response.setDescriptionResponse(this.baseCodeResponse.getDescription(IT4ALL_RULE_VALIDATION_DESCRIPTION));
	    		response.setDescriptionException(resultList.toString());
				throw new Exception(this.baseCodeResponse.getDescription(IT4ALL_RULE_VALIDATION_DESCRIPTION));
	        }
	        repository.save(custom);
		String userName = tokenService.getAuthenticatedUsername();
		auditoryService.create(custom, userName);
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
			response.setDescriptionResponse(this.baseCodeResponse
					.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (RuntimeException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
       		response.setDescriptionResponse(this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {
			response.setSuccess(false);
			if (response.getDescriptionException() == null) {
				response.setObject(null);
				response.setDescriptionException(e.getMessage());
			}
			logger.error(e.getMessage());
		}
       return response;
   }
   @Override
   @Transactional
   public ResponseServiceDTO update(CustomDTO customDTO) {
       List<String> resultList = new ArrayList<>();
		ResponseServiceDTO response = new ResponseServiceDTO();

      Custom custom = mapper.dtoToEntity(customDTO);

       try {
       	Custom aux = null;
       	aux = repository.findById(custom.getId()).get();
       	if (aux == null) {
				throw new Exception(this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
       	}
	        RuleContext ruleContext = new RuleContext();
	        ruleContext.setRuleToExecute("CustomUpdate");
	        KieSession kieSession = kieContainer.newKieSession();
	        kieSession.setGlobal("resultList", resultList);
	        kieSession.insert(custom);
	        kieSession.insert(ruleContext);
	        kieSession.fireAllRules();
	        kieSession.dispose();
			response.setObject(null);
			response.setSuccess(true);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setDescriptionException(null);
	        if (!resultList.isEmpty()) {
	    		response.setObject(resultList);
	    		response.setSuccess(false);
	    		response.setCodeResponse(
	    				this.baseCodeResponse.getDescription(IT4ALL_RULE_VALIDATION_CODE));
	    		response.setDescriptionResponse(this.baseCodeResponse.getDescription(IT4ALL_RULE_VALIDATION_DESCRIPTION));
	    		response.setDescriptionException(resultList.toString());
				throw new Exception(this.baseCodeResponse.getDescription(IT4ALL_RULE_VALIDATION_DESCRIPTION));
	        }
	        repository.save(custom);
		String userName = tokenService.getAuthenticatedUsername();
		auditoryService.update(custom, aux, userName);
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
			response.setDescriptionResponse(this.baseCodeResponse
					.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (RuntimeException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_CODE));
       	response.setDescriptionResponse(this.baseCodeResponse.getDescription(IT4ALL_ID_REGISTER_NULO_NON_EXISTENT_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			logger.error(e.getMessage());
		} catch (Exception e) {
			response.setSuccess(false);
			if (response.getDescriptionException() == null) {
				response.setObject(null);
				response.setDescriptionException(e.getMessage());
			}
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
		List<Custom> customList = new ArrayList<>();
		try {
        for (Long id : idList) {
        	Custom custom = repository.findById(id).get();
        	customList.add(custom);
        	repository.deleteById(id);
        }
		String userName = tokenService.getAuthenticatedUsername();
        auditoryService.delete(customList, userName);
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
			customCsvService.processCsv(tempFile, taskId);
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
			customCsvService.exportCsv(writer);
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