package org.csu.phdata.persistence

import org.csu.phdata.entity.PublicHealthData
import org.csu.phdata.entity.PublicHealthDatas
import org.springframework.stereotype.Component

@Component
class PHDataDao : BaseDao<PublicHealthData, PublicHealthDatas>(PublicHealthDatas)