package com.it4all.credit.admon.service.businessrules;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.it4all.credit.admon.config.BaseCodeResponseConfig;
import com.it4all.credit.admon.data.dto.model.BusinessRulesDTO;
import com.it4all.credit.admon.data.dto.request.FilterDTO;
import com.it4all.credit.admon.data.dto.request.FilterPageDTO;
import com.it4all.credit.admon.data.dto.request.PageDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;
import com.it4all.credit.admon.data.mapper.GenericMapper;
import com.it4all.credit.admon.data.model.BusinessRules;
import com.it4all.credit.admon.data.model.repository.businessrules.BusinessRulesRepository;
import com.it4all.credit.admon.data.model.specification.EntitySpecifications;
import com.it4all.credit.admon.service.CsvService;
import com.it4all.credit.admon.service.CsvServiceConfig;

@Service
public class BusinessRulesService implements IBusinessRulesService{
	Logger logger = LoggerFactory.getLogger(BusinessRulesService.class);
	
	@Autowired
	private KieContainer kieContainer;

	@Autowired
	private BusinessRulesRepository repository;

	@Autowired
	private BaseCodeResponseConfig baseCodeResponse;

	@Autowired
	GenericMapper<BusinessRulesDTO, BusinessRules> mapper;
	private final CsvService<BusinessRules> businessrulesCsvService;

	@Autowired
	public BusinessRulesService(CsvServiceConfig csvServiceConfig, BusinessRulesRepository businessrulesRepository) {
	    this.businessrulesCsvService = csvServiceConfig
	    		.csvService(businessrulesRepository, BusinessRules.class);
    }

	@Override
	public ResponseServiceDTO getById(Long id) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			BusinessRules businessrules = repository.findById(id).get();
			response.setObject(mapper.entityToDTO(businessrules));
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
			List<BusinessRulesDTO> listBusinessRuless = repository.findAll().stream()
			.map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());
			response.setObject(listBusinessRuless);
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

			List<BusinessRulesDTO> listBusinessRuless = repository.getByPage(pageable).stream()			.map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());

			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setObject(listBusinessRuless);
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

	        Specification<BusinessRules> spec = EntitySpecifications.buildSpecification(filters);

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

			List<BusinessRulesDTO> listBusinessRuless = repository.findAll(spec, pageable).stream().map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());

			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setObject(listBusinessRuless);
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
	public ResponseServiceDTO create(BusinessRulesDTO businessrules) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			response = createTX(businessrules);
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
	private ResponseServiceDTO createTX(BusinessRulesDTO businessrules) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		businessrules = mapper.entityToDTO(
				repository.save( mapper.dtoToEntity(businessrules)));
		response.setObject(businessrules);
		response.setSuccess(true);
		response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
		response.setDescriptionResponse(
				this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
		return response;
	}

	@Override
	public ResponseServiceDTO update(BusinessRulesDTO businessrules) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			response = updateTX(businessrules);
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
	private ResponseServiceDTO updateTX(BusinessRulesDTO businessrules) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		BusinessRules aux = null;
		aux = repository.findById(businessrules.getId()).get();
		if (aux != null) {
			businessrules.setId(aux.getId());
			businessrules = mapper.entityToDTO(
					repository.save(mapper.dtoToEntity(businessrules)));
			response.setObject(businessrules);
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
		try {
        for (Long id : idList) {
        	repository.deleteById(id);
        }
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
			businessrulesCsvService.processCsv(tempFile, taskId);
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
			businessrulesCsvService.exportCsv(writer);
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
