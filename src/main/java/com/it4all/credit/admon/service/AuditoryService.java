package com.it4all.credit.admon.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.it4all.credit.admon.data.model.Auditory;
import com.it4all.credit.admon.data.model.repository.AuditoryRepository;

@Service
public class AuditoryService {

	Logger logger = LoggerFactory.getLogger(AuditoryService.class);

    @Autowired
	private AuditoryRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    public <T> void create(T entity, String userName) throws Exception {
        Auditory audit = new Auditory();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonString = objectMapper.writeValueAsString(entity);
        String entityName = entity.getClass().getSimpleName();
        Long entityId = getEntityId(entity);
        audit.setEntityName(entityName);
        audit.setEntityId(entityId);
        audit.setAction("INSERT");
        audit.setModifiedBy(userName);
        audit.setModifiedDate(LocalDateTime.now());
        audit.setNewValue(jsonString);
        repository.save(audit);
    }

    @Async
    public <T> void update(T entity, T entityBefore, String userName) throws Exception {
        Auditory audit = new Auditory();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonString = objectMapper.writeValueAsString(entity);
        String jsonStringBefore = objectMapper.writeValueAsString(entityBefore);
        String entityName = entity.getClass().getSimpleName();
        Long entityId = getEntityId(entity);
        audit.setEntityName(entityName);
        audit.setEntityId(entityId);
        audit.setAction("UPDATE");
        audit.setModifiedBy(userName);
        audit.setModifiedDate(LocalDateTime.now());
        audit.setNewValue(jsonString);
        audit.setPreviousValue(jsonStringBefore);
        repository.save(audit);
    }

    @Async
    public <T> void delete(List<T> entityBeforeList, String userName) throws Exception {
        List<Auditory> audit = new ArrayList<>();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonString = null;

        for (int i = 0; i < entityBeforeList.size(); i++) {

            T entityBefore = entityBeforeList.get(i);
	        String jsonStringBefore = objectMapper.writeValueAsString(entityBefore);
	        String entityName = entityBeforeList.getClass().getSimpleName();
	        Long entityId = getEntityId(entityBefore);
	        Auditory auditItem = new Auditory();
	        auditItem.setEntityName(entityName);
	        auditItem.setEntityId(entityId);
	        auditItem.setAction("DELETE");
	        auditItem.setModifiedBy(userName);
	        auditItem.setModifiedDate(LocalDateTime.now());
	        auditItem.setNewValue(jsonString);
	        auditItem.setPreviousValue(jsonStringBefore);
	        audit.add(auditItem);
        }
        repository.saveAll(audit);
    }

    private <T> Long getEntityId(T entity) throws Exception {
        // Todas las entidades tienen un método getId() que devuelve un Long
        try {
            return (Long) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            logger.error("Error al obtener el ID de la entidad", e);
            throw new Exception("No se pudo obtener el ID de la entidad", e);
        }
    }
}
