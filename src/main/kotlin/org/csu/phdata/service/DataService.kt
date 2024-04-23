package org.csu.phdata.service

import org.csu.phdata.common.CommonResponse
import org.csu.phdata.common.Constants
import org.csu.phdata.entity.PublicHealthData
import org.csu.phdata.entity.PublicHealthDatas
import org.csu.phdata.persistence.BaseDao
import org.csu.phdata.persistence.PHDataDao
import org.csu.phdata.persistence.phdata.CasesDataDao
import org.csu.phdata.persistence.phdata.CasesRateDao
import org.csu.phdata.persistence.phdata.DeathDataDao
import org.csu.phdata.persistence.phdata.DeathRateDao
import org.ktorm.dsl.*
import org.ktorm.schema.Table
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class DataService {

    @Autowired
    lateinit var phDataDao: PHDataDao
    @Autowired
    lateinit var casesDataDao: CasesDataDao
    @Autowired
    lateinit var casesRateDao: CasesRateDao
    @Autowired
    lateinit var deathDataDao: DeathDataDao
    @Autowired
    lateinit var deathRateDao: DeathRateDao
    fun getDataCount(
        disease: String,
        province: String,
        date: String,
        dataType:String
        ): CommonResponse<*> {
        val month = LocalDate.parse(date)
        val nextMonth = month.plusMonths(1)

        val dao = when (dataType) {
            Constants.CASESDATA -> casesDataDao
            Constants.DEATHDATA -> deathDataDao
            Constants.CASESRATE -> casesRateDao
            Constants.DEATHRATE -> deathRateDao
            else -> phDataDao
        }


        val count = dao.count {
            (it.phData.disease eq disease) and
            (it.phData.province eq province) and
            (it.phData.monthDate between month..nextMonth) and
            (it.phData.dataValue neq "0")
        }


        return CommonResponse.createForSuccess(count)
    }
}
