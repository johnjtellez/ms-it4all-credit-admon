package com.it4all.credit.admon.service.creditrequest;

import java.io.Writer;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.it4all.credit.admon.data.dto.model.CreditRequestDTO;
import com.it4all.credit.admon.data.dto.request.FilterPageDTO;
import com.it4all.credit.admon.data.dto.request.PageDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;

public interface ICreditRequestService extends ICreditRequestCode {

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
	 * @param objeto
	 * @return List<Long>
	 */
	public ResponseServiceDTO create(CreditRequestDTO creditrequest);

	/**
	 * @param objeto
	 * @return List<Long>
	 */
	public ResponseServiceDTO update(CreditRequestDTO creditrequest);

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
