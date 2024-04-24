package org.csu.phdata.service

import org.csu.phdata.common.CommonResponse
import org.csu.phdata.common.Constants
import org.csu.phdata.persistence.PHDataDao
import org.csu.phdata.persistence.phdata.CasesDataDao
import org.csu.phdata.persistence.phdata.CasesRateDao
import org.csu.phdata.persistence.phdata.DeathDataDao
import org.csu.phdata.persistence.phdata.DeathRateDao
import org.csu.phdata.util.DataFieldUtil
import org.csu.phdata.util.DataFieldUtil.Companion.getAgeInterval
import org.ktorm.dsl.*
import org.ktorm.schema.ColumnDeclaring
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate


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

    fun recognizeDao(dataType: String) = when (dataType) {
        Constants.CASESDATA -> casesDataDao
        Constants.DEATHDATA -> deathDataDao
        Constants.CASESRATE -> casesRateDao
        Constants.DEATHRATE -> deathRateDao
        else -> phDataDao
    }

    fun getDataCount(
        disease: String,
        province: String,
        date: String,
        dataType:String
    ): CommonResponse<*> {

        val month = LocalDate.parse(date)
        val nextMonth = month.plusMonths(1)
        val dao = recognizeDao(dataType)

        return CommonResponse.createForSuccess(
            dao.count {
                (it.phData.disease eq disease) and
                (it.phData.province eq province) and
                (it.phData.monthDate between month..nextMonth) and
                (it.phData.dataValue neq "0")
            }
        )
    }

    fun getDataBySpecificCondition(
        disease: String,
        province: String,
        dataType: String,
        age: String,
        date: String
    ): CommonResponse<*> {

        val month = LocalDate.parse(date)
        val nextMonth = month.plusMonths(1)
        val dao = recognizeDao(dataType)

        val publicHealthDataList = dao.findList {
            (it.phData.disease eq disease) and
            (it.phData.province eq province) and
            (it.phData.monthDate between month..nextMonth) and
            (it.phData.dataValue neq "0") and
            (it.phData.age eq age)
        }

        return CommonResponse.createForSuccess(publicHealthDataList)
    }

    fun getDataInProvinces(
        disease: String,
        dataType: String,
        age: String,
        nextAge: String,
        date: String,
        nextDate: String
    ): CommonResponse<*> {
        val month = if (date.isNotEmpty()) LocalDate.parse(date) else null
        val nextMonth = month.let {
            if (nextDate.isNotEmpty()) LocalDate.parse(nextDate) else month?.plusMonths(1)
        }
        val ageInterval = getAgeInterval(age, nextAge)
        val dao = recognizeDao(dataType)

        val publicHealthDataList = dao.findList { publicHealthData ->
            val conditions = ArrayList<ColumnDeclaring<Boolean>>()
            conditions += publicHealthData.phData.dataValue neq "0"
            if (disease.isNotEmpty()) conditions += publicHealthData.phData.disease eq disease
            if (month != null && nextMonth != null) conditions += publicHealthData.phData.monthDate between month..nextMonth
            if (ageInterval.isNotEmpty()) conditions += publicHealthData.phData.age inList ageInterval
            conditions.reduce { a, b -> a and b }
        }

        val provinceValueMap = mutableMapOf<String, Int>()
        for (publicHealthData in publicHealthDataList) {
            val province = DataFieldUtil.convertProvinces(publicHealthData.province)
            val dataValue = publicHealthData.dataValue.toInt()
            provinceValueMap[province] = (provinceValueMap[province]?.plus(dataValue))?:(dataValue)
        }
        data class ResultData (val name:String, val value: Int)
        val resultList = mutableListOf<ResultData>()
        for (key in provinceValueMap.keys) {
            provinceValueMap[key]?.let { ResultData(key, it) }?.let { resultList.add(it) }
        }

        return CommonResponse.createForSuccess(resultList)
    }
}
