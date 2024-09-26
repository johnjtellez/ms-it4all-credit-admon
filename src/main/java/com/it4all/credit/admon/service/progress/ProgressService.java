package com.it4all.credit.admon.service.progress;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.it4all.credit.admon.config.BaseCodeResponseConfig;
import com.it4all.credit.admon.data.dto.model.TaskProgressDTO;
import com.it4all.credit.admon.data.dto.request.FilterDTO;
import com.it4all.credit.admon.data.dto.request.FilterPageDTO;
import com.it4all.credit.admon.data.dto.request.PageDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;
import com.it4all.credit.admon.data.mapper.GenericMapper;
import com.it4all.credit.admon.data.model.TaskProgress;
import com.it4all.credit.admon.data.model.repository.TaskProgressRepository;
import com.it4all.credit.admon.data.model.specification.EntitySpecifications;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProgressService implements IProgressService {

	@Autowired
	private BaseCodeResponseConfig baseCodeResponse;

    private final TaskProgressRepository repository;

	@Autowired
	GenericMapper<TaskProgressDTO, TaskProgress> mapper;

    @Autowired
    public ProgressService(TaskProgressRepository repository) {
        this.repository = repository;
    }

    public void start(String taskId) {
        TaskProgress taskProgress = new TaskProgress(taskId, 0);
        repository.save(taskProgress);
    }

    public void update(String taskId, int progress) {
        TaskProgress taskProgress = repository.findByTaskId(taskId);
        if (taskProgress != null) {
            taskProgress.setProgress(progress);
            repository.save(taskProgress);
        }
    }

    @Override
    public ResponseServiceDTO getProgress(String taskId) {

		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
	        TaskProgress taskProgress = repository.findByTaskId(taskId);

			response.setObject(((taskProgress != null) ? taskProgress.getProgress() : 0));
			response.setSuccess(true);
			response.setCodeResponse(
				this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
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
			log.error(e.getMessage());
		} catch (Exception e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
				this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
				this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			log.error(e.getMessage());
		}
		return response;
    }

    public void finish(String taskId) {
        update(taskId, 100);
    }

	@Override
	public ResponseServiceDTO getById(Long id) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			TaskProgress taskProgress = repository.findById(id).get();
			response.setObject(mapper.entityToDTO(taskProgress));

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
			log.error(e.getMessage());
		} catch (Exception e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			log.error(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseServiceDTO getAll() {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			List<TaskProgressDTO> listTaskProgresss = repository.findAll().stream().map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());
			response.setObject(listTaskProgresss);
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
			log.error(e.getMessage());
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

			List<TaskProgressDTO> listTaskProgress = repository.getByPage(pageable).stream().map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());

			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setObject(listTaskProgress);
			response.setSuccess(true);
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_QUERY_PAGE_SQL_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL__QUERY_PAGE_SQL_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			log.error(e.getMessage());

		} catch (Exception e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			log.error(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseServiceDTO getFilters(FilterPageDTO request) {
		ResponseServiceDTO response = new ResponseServiceDTO();
		try {
			Pageable pageable = null;

			List<FilterDTO> filters = request.getFilters();

	        Specification<TaskProgress> spec = EntitySpecifications.buildSpecification(filters);

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

			List<TaskProgressDTO> listTaskProgresss = repository.findAll(spec, pageable).stream().map(entity -> {
				return mapper.entityToDTO(entity);
			}).collect(Collectors.toList());

			response.setCodeResponse(this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_TRANSACCION_SUCCESS_DESCRIPTION));
			response.setObject(listTaskProgresss);
			response.setSuccess(true);
		} catch (DataAccessException e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_QUERY_PAGE_SQL_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL__QUERY_PAGE_SQL_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			log.error(e.getMessage());

		} catch (Exception e) {
			response.setObject(null);
			response.setSuccess(false);
			response.setCodeResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_CODE));
			response.setDescriptionResponse(
					this.baseCodeResponse.getDescription(IT4ALL_EXCEPCION_UNEXPECTED_RUNTIME_DESCRIPTION));
			response.setDescriptionException(e.getMessage());
			log.error(e.getMessage());
		}
		return response;
	}

}
