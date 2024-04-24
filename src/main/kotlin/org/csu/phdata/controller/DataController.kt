package org.csu.phdata.controller

import org.csu.phdata.service.DataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.DataType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping ("/data/")
@RestController
class DataController {

    @Autowired
    lateinit var dataService: DataService

    @PostMapping("getDataCount")
    fun getDataCount(
        disease: String,
        province: String,
        date: String,
        dataType: String
    ) = dataService.getDataCount(disease, province, date, dataType)

    @PostMapping("getDataBySpecificCondition")
    fun getDataBySpecificCondition(
        disease: String,
        province: String,
        dataType: String,
        age: String,
        date: String
    ) = dataService.getDataBySpecificCondition(disease, province, dataType, age, date)

    @PostMapping("getDataInProvinces")
    fun getDataInProvinces(
        disease: String,
        dataType: String,
        age: String,
        nextAge: String,
        date: String,
        nextDate: String
    ) = dataService.getDataInProvinces(disease, dataType, age, nextAge, date, nextDate)

}