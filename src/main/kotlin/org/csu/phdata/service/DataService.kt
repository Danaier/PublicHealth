package org.csu.phdata.service

import org.csu.phdata.common.CommonResponse
import org.csu.phdata.common.Constants
import org.csu.phdata.entity.PublicHealthData
import org.csu.phdata.entity.parameters.ProvinceParameter
import org.csu.phdata.entity.parameters.RangeParameter
import org.csu.phdata.entity.parameters.SpecificParameter
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
import java.io.Serializable
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

    data class DataOfAProvince (
        // 省份
        val name:String,
        // 数据
        val value: Int
    ): Serializable
    data class DataInProvincesOfADate(
        val date: LocalDate,
        val data: List<DataOfAProvince>
    ): Serializable

    fun recognizeDao(dataType: String) = when (dataType) {
        Constants.CASESDATA -> casesDataDao
        Constants.DEATHDATA -> deathDataDao
        Constants.CASESRATE -> casesRateDao
        Constants.DEATHRATE -> deathRateDao
        else -> phDataDao
    }

    fun getRangeDataListFromDao(rangeParameter: RangeParameter): List<PublicHealthData> {
        val (disease, dataType, age, nextAge, date, nextDate) = rangeParameter
        val month = if (date.isNotEmpty()) LocalDate.parse(date) else null
        val nextMonth = month.let {
            if (nextDate.isNotEmpty()) LocalDate.parse(nextDate) else month?.plusMonths(1)
        }
        val ageInterval = getAgeInterval(age, nextAge)
        val dao = recognizeDao(dataType)

         return dao.findList { publicHealthData ->
            val conditions = ArrayList<ColumnDeclaring<Boolean>>()
            conditions += publicHealthData.phData.dataValue neq "0"
            if (disease.isNotEmpty()) conditions += publicHealthData.phData.disease eq disease
            if (month != null && nextMonth != null) conditions += publicHealthData.phData.monthDate between month..nextMonth
            if (ageInterval.isNotEmpty()) conditions += publicHealthData.phData.age inList ageInterval
            conditions.reduce { a, b -> a and b }
        }
    }

    fun getRangeDataListFroAProvinceFromDao(provinceParameter: ProvinceParameter): List<PublicHealthData> {
        var (disease, dataType, age, nextAge, date, nextDate, province) = provinceParameter
        val month = if (date.isNotEmpty()) LocalDate.parse(date) else null
        val nextMonth = month.let {
            if (nextDate.isNotEmpty()) LocalDate.parse(nextDate) else month?.plusMonths(1)
        }
        val ageInterval = getAgeInterval(age, nextAge)
        val dao = recognizeDao(dataType)
        province = "$province%"
        return dao.findList { publicHealthData ->
            val conditions = ArrayList<ColumnDeclaring<Boolean>>()
            conditions += publicHealthData.phData.dataValue neq "0"
            if (disease.isNotEmpty()) conditions += publicHealthData.phData.disease eq disease
            if (month != null && nextMonth != null) conditions += publicHealthData.phData.monthDate between month..nextMonth
            if (ageInterval.isNotEmpty()) conditions += publicHealthData.phData.age inList ageInterval
            conditions += publicHealthData.phData.province like province
            conditions.reduce { a, b -> a and b }
        }
    }

    fun getDataCount(
        specificParameter: SpecificParameter
    ): CommonResponse<*> {

        val month = LocalDate.parse(specificParameter.date)
        val nextMonth = month.plusMonths(1)
        val dao = recognizeDao(specificParameter.dataType)

        return CommonResponse.createForSuccess(
            dao.count {
                (it.phData.disease eq specificParameter.disease) and
                (it.phData.province eq specificParameter.province) and
                (it.phData.monthDate between month..nextMonth) and
                (it.phData.dataValue neq "0") and
                (it.phData.age eq specificParameter.age)
            }
        )
    }

    fun getDataBySpecificCondition(
        specificParameter: SpecificParameter
    ): CommonResponse<*> {

        val month = LocalDate.parse(specificParameter.date)
        val nextMonth = month.plusMonths(1)
        val dao = recognizeDao(specificParameter.dataType)

        val publicHealthDataList = dao.findList {
            (it.phData.disease eq specificParameter.disease) and
            (it.phData.province eq specificParameter.province) and
            (it.phData.monthDate between month..nextMonth) and
            (it.phData.dataValue neq "0") and
            (it.phData.age eq specificParameter.age)
        }

        return CommonResponse.createForSuccess(publicHealthDataList)
    }

    fun getDataInProvinces(
        rangeParameter: RangeParameter
    ): CommonResponse<*> {

        val publicHealthDataList = getRangeDataListFromDao(rangeParameter)
        val provinceValueMap = mutableMapOf<String, Int>()
        for (publicHealthData in publicHealthDataList) {
            val province = DataFieldUtil.convertProvinces(publicHealthData.province)
            val dataValue = publicHealthData.dataValue.toInt()
            // 在Map的对应键中加入dataValue的值
            provinceValueMap[province] = (provinceValueMap[province]?.plus(dataValue))?:(dataValue)
        }
        val resultList = mutableListOf<DataOfAProvince>()
        for (key in provinceValueMap.keys) {
            // provinceValueMap[key]存在时创建ResultData对象，若创建成功则在resultList中加入它
            provinceValueMap[key]?.let { DataOfAProvince(key, it) }?.let { resultList.add(it) }
        }

        return CommonResponse.createForSuccess(resultList)
    }

    fun getDataInProvincesVaryInDates(rangeParameter: RangeParameter): CommonResponse<*> {
        val publicHealthDataList = getRangeDataListFromDao(rangeParameter)

        val datedPHDataLists = publicHealthDataList.groupBy { it.monthDate }
        val dataInProvincesVaryInDates = mutableListOf<DataInProvincesOfADate>()
        for ((monthDate, datedPHDataList) in datedPHDataLists) {
            val provinceValueMap = mutableMapOf<String, Int>()
            for (publicHealthData in datedPHDataList) {
                val province = DataFieldUtil.convertProvinces(publicHealthData.province)
                val dataValue = publicHealthData.dataValue.toInt()
                // 在Map的对应键中加入dataValue的值
                provinceValueMap[province] = (provinceValueMap[province]?.plus(dataValue))?:(dataValue)
            }
            val dataInProvincesOfADate = mutableListOf<DataOfAProvince>()
            for (key in provinceValueMap.keys) {
                // provinceValueMap[key]存在时创建ResultData对象，若创建成功则在resultList中加入它
                provinceValueMap[key]?.let { DataOfAProvince(key, it) }?.let { dataInProvincesOfADate.add(it) }
            }
            dataInProvincesVaryInDates.add(DataInProvincesOfADate(monthDate, dataInProvincesOfADate))
        }

        return CommonResponse.createForSuccess(dataInProvincesVaryInDates.sortedBy { it.date })
    }

    fun getDataForAProvince(provinceParameter: ProvinceParameter): CommonResponse<*> {
        val publicHealthDataList = getRangeDataListFroAProvinceFromDao(provinceParameter)
        // 分日期
        val datedPHDataLists = publicHealthDataList.groupBy { it.monthDate }
        val dateValveMap = mutableMapOf<String, Int>()
        for ((monthDate, datedPHDataList) in datedPHDataLists) {
            val sum = datedPHDataList.sumOf { it.dataValue.toInt() }
            dateValveMap[monthDate.toString()] = sum
        }
        val ageValueMap = mutableMapOf<String, Int>()
        for (publicHealthData in publicHealthDataList) {
            val age = publicHealthData.age
            val dataValue = publicHealthData.dataValue.toInt()
            // 在Map的对应键中加入dataValue的值
            ageValueMap[age] = (ageValueMap[age]?.plus(dataValue))?:(dataValue)
        }
        // 分年龄
        val ageResultList = mutableListOf<DataOfAProvince>()
        for (key in ageValueMap.keys) {
            // provinceValueMap[key]存在时创建ResultData对象，若创建成功则在resultList中加入它
            ageValueMap[key]?.let { DataOfAProvince(key, it) }?.let { ageResultList.add(it) }
        }

        data class Result(val dateResult:MutableMap<String, Int>, val ageResult:MutableList<DataOfAProvince>): Serializable

        return CommonResponse.createForSuccess(Result(dateValveMap, ageResultList))
    }

    fun getDataVaryInAges(rangeParameter: RangeParameter): CommonResponse<*> {

        val publicHealthDataList = getRangeDataListFromDao(rangeParameter)
        val ageValueMap = mutableMapOf<String, Int>()
        for (publicHealthData in publicHealthDataList) {
            val age = publicHealthData.age
            val dataValue = publicHealthData.dataValue.toInt()
            // 在Map的对应键中加入dataValue的值
            ageValueMap[age] = (ageValueMap[age]?.plus(dataValue))?:(dataValue)
        }
        val resultList = mutableListOf<DataOfAProvince>()
        for (key in ageValueMap.keys) {
            // provinceValueMap[key]存在时创建ResultData对象，若创建成功则在resultList中加入它
            ageValueMap[key]?.let { DataOfAProvince(key, it) }?.let { resultList.add(it) }
        }

        return CommonResponse.createForSuccess(resultList)
    }

}
