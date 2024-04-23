package org.csu.phdata.persistence.phdata

import org.csu.phdata.entity.CasesRates
import org.csu.phdata.entity.PublicHealthData
import org.csu.phdata.persistence.BaseDao
import org.springframework.stereotype.Component


@Component
class CasesRateDao : BaseDao<PublicHealthData, CasesRates>(CasesRates)