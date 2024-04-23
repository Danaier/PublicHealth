package org.csu.phdata.persistence.phdata

import org.csu.phdata.entity.DeathDatas
import org.csu.phdata.entity.PublicHealthData
import org.csu.phdata.persistence.BaseDao
import org.springframework.stereotype.Component

@Component
class DeathDataDao : BaseDao<PublicHealthData, DeathDatas>(DeathDatas)