package com.it4all.credit.admon.service.dynamyc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.it4all.credit.admon.config.BaseCodeResponseConfig;
import com.it4all.credit.admon.data.dto.request.RulesDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;
import com.it4all.credit.admon.data.dto.response.RulesResultDTO;
import com.it4all.credit.admon.data.model.BusinessRules;
import com.it4all.credit.admon.data.model.BusinessRulesParameter;
import com.it4all.credit.admon.data.model.repository.businessrules.BusinessRulesRepository;
import com.it4all.credit.admon.data.model.repository.businessrulesparameters.BusinessRulesParameterRepository;

@Service
public class DynamicSQLService implements IDynamicSQLCode{

	Logger logger = LoggerFactory.getLogger(DynamicSQLService.class);

	@Autowired
    private JdbcTemplate jdbcTemplate;

	@Autowired
	private BaseCodeResponseConfig baseCodeResponse;

	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	BusinessRulesRepository _businessRulesRepository;

	@Autowired
	BusinessRulesParameterRepository _businessRulesParametersRepository;

    public ResponseServiceDTO executeDynamicQuery(RulesDTO rules) {
        ResponseServiceDTO response = new ResponseServiceDTO();
        List<RulesResultDTO> listResult = new ArrayList<>();
		try {
			// Crear un Map con los parámetros
		    Map<String, Object> params = new HashMap<>();
			//Obtiene la lista de las posibles reglas

			List<BusinessRules> listRules = _businessRulesRepository
						.getRuleByCodeAndField(rules.getCode(), rules.getFieldName());

			for (BusinessRules rule : listRules) {
				logger.info(rule.getQuery());
				List<BusinessRulesParameter> listParams = _businessRulesParametersRepository.findByBusinessRulesId(rule.getId());
				for (BusinessRulesParameter parametro : listParams) {
					Object valor = null;
					logger.info(parametro.getName());
					if (parametro.getIsValueInField()) {
						valor = findValueInHasMap(rules.getObjetoField(), parametro.getName());
					}else {
						valor = findValueInHasMap(rules.getObjetoForm(), parametro.getName());
					}
					params.put(parametro.getName(), valor);
				}

				MapSqlParameterSource paramSource = new MapSqlParameterSource(params);

				List<Object> results = namedParameterJdbcTemplate.query(
						rule.getQuery(), paramSource, (rs, rowNum) -> rs.getBoolean("result"));
				results.forEach(System.out::println);
				Boolean validation = (Boolean)results.get(0);
				RulesResultDTO result = new RulesResultDTO();
				if (validation) {
					result.setRuleName(rule.getName());
					result.setResult(validation);
				}else {
					result.setRuleName(rule.getName());
					result.setResult(validation);
					result.setMessageError(rule.getMessageError());
				}
				listResult.add(result);
			}

			response.setObject(listResult);
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

    public Object findValueInHasMap(java.util.LinkedHashMap<String, Object> dato, String key) {
    	Object response = null;

        if (dato.containsKey(key)) {
        	response =  dato.get(key);
        }
        return response; // O puedes devolver un valor predeterminado si no se encuentra la clave
    }

}
