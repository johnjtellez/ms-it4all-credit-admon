package com.it4all.credit.admon.service.dynamyc;

import com.it4all.credit.admon.data.dto.response.ResponseServiceDTO;

public interface IDynamicSQLService extends IDynamicSQLCode{

/**
 * @param sql
* @return ResponseServiceDTO
 */
public ResponseServiceDTO executeDynamicQuery(String sql);

}
