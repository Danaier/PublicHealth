package org.csu.phdata.controller

import jakarta.annotation.Resource
import org.csu.phdata.entity.parameters.RangeParameter
import org.csu.phdata.entity.parameters.SpecificParameter
import org.csu.phdata.service.DataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Cacheable(cacheNames = ["requests"], keyGenerator = "customKeyGenerator")
@RequestMapping ("/data/")
@RestController
class DataController {

    @Autowired
    lateinit var dataService: DataService

    @PostMapping("getDataCount")
    fun getDataCount(
        @RequestBody specificParameter: SpecificParameter
    ) = dataService.getDataCount(specificParameter)

    @PostMapping("getDataBySpecificCondition")
    fun getDataBySpecificCondition(
        @RequestBody specificParameter: SpecificParameter
    ) = dataService.getDataBySpecificCondition(specificParameter)

    @PostMapping("getDataInProvinces")
    fun getDataInProvinces(
        @RequestBody rangeParameter: RangeParameter
    ) = dataService.getDataInProvinces(rangeParameter)

    @PostMapping("getDataInProvincesVaryInDates")
    fun getDataInProvinceVaryInDates(
        @RequestBody rangeParameter: RangeParameter
    ) = dataService.getDataInProvincesVaryInDates(rangeParameter)

}