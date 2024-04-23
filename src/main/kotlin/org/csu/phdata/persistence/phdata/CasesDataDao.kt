package org.csu.phdata.persistence.phdata

import org.csu.phdata.entity.CasesDatas
import org.csu.phdata.entity.PublicHealthData
import org.csu.phdata.persistence.BaseDao
import org.springframework.stereotype.Component

@Component
class CasesDataDao : BaseDao<PublicHealthData, CasesDatas>(CasesDatas)