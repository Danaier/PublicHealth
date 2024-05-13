package org.csu.phdata.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.csu.phdata.entity.parameters.ProvinceParameter
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
@Tag(name = "数据获取接口")
class DataController {

    @Autowired
    lateinit var dataService: DataService

    @Operation(summary = "根据条件获取数据量")
    @PostMapping("getDataCount")
    fun getDataCount(
        @RequestBody specificParameter: SpecificParameter
    ) = dataService.getDataCount(specificParameter)

    @Operation(summary = "根据特定条件获取数据")
    @PostMapping("getDataBySpecificCondition")
    fun getDataBySpecificCondition(
        @RequestBody specificParameter: SpecificParameter
    ) = dataService.getDataBySpecificCondition(specificParameter)

    @Operation(summary = "分省份查询数据")
    @PostMapping("getDataInProvinces")
    fun getDataInProvinces(
        @RequestBody rangeParameter: RangeParameter
    ) = dataService.getDataInProvinces(rangeParameter)

    @Operation(summary = "分省份分月份查询数据")
    @PostMapping("getDataInProvincesVaryInDates")
    fun getDataInProvinceVaryInDates(
        @RequestBody rangeParameter: RangeParameter
    ) = dataService.getDataInProvincesVaryInDates(rangeParameter)

    @Operation(summary = "单省份分月份查询数据")
    @PostMapping("getDataVaryInDates")
    fun getDataVaryInDates(
        @RequestBody provinceParameter: ProvinceParameter
    ) = dataService.getDataVaryInDates(provinceParameter)

}