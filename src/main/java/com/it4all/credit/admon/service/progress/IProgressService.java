package com.it4all.credit.admon.service.progress;

import com.it4all.credit.admon.data.dto.request.FilterPageDTO;
import com.it4all.credit.admon.data.dto.request.PageDTO;
import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;

public interface IProgressService extends IProgressCode {

	/**
	 * @param String taskId
	 * @return ResponseServiceDTO
	 */
    public ResponseServiceDTO getProgress(String taskId);

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
}
