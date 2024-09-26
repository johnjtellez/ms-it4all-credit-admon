package com.it4all.credit.admon.service.businessrulesparameters;

import java.io.Writer;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.it4all.credit.admon.data.dto.model.BusinessRulesParameterDTO;
import com.it4all.credit.admon.data.dto.request.FilterPageDTO;
import com.it4all.credit.admon.data.dto.request.PageDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;

public interface IBusinessRulesParameterService extends IBusinessRulesParameterCode {

	/**
	 * @param id
	 * @return
	 */
	public ResponseServiceDTO getById(Long id);

	/**
	 * @return
	 */
	public ResponseServiceDTO getAll();

	/**
	 * @param numberPage
	 * @param registerByPage
	 * @param orderBy
	 * @param ordenAscending
	 * @return
	 */
	public ResponseServiceDTO getByPage(PageDTO pageDto);

	/**
	 * @param List<FilterDTO>
	 * @return ResponseServiceDTO
	 */
	public ResponseServiceDTO getFilters(FilterPageDTO request);

	/**
	 * @param Long id
	 * @return ResponseServiceDTO
	 */
	public ResponseServiceDTO getByBusinessRulesId(Long id);

	/**
	 * @param objeto
	 * @return List<Long>
	 */
	public ResponseServiceDTO create(BusinessRulesParameterDTO businessrulesparameters);

	/**
	 * @param objeto
	 * @return List<Long>
	 */
	public ResponseServiceDTO update(BusinessRulesParameterDTO businessrulesparameters);

	/**
	 * @param id
	 * @return List<Long>
	 */
	public ResponseServiceDTO deleteById(Long id);

	/**
	 * @param id
	 * @return List<Long>
	 */
	public ResponseServiceDTO deleteAnyId(List<Long> idList);

	/**
	 * @param MultipartFile file
	 * @return ResponseServiceDTO
	 */
	public ResponseServiceDTO upload(MultipartFile file);

	/**
	 * @param Writer
	 * @return ResponseServiceDTO
	 */
	public ResponseServiceDTO download(Writer writer);
}
