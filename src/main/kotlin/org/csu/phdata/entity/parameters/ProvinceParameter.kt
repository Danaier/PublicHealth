package org.csu.phdata.entity.parameters

import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "单省份范围条件参数")
data class ProvinceParameter (

    @Schema(description = "病种", example = "包虫病", required = true)
    val disease: String,

    @Schema(description = "数据类型", example = "cases_data", required = true)
    val dataType: String,

    @Schema(description = "年龄最小值", example = "0-", required = false)
    val age: String,

    @Schema(description = "年龄最大值", example = "30-", required = false)
    val nextAge: String,

    @Schema(description = "月份最小值", example = "2005-01-01", required = false)
    val date: String,

    @Schema(description = "月份最大值", example = "2008-01-01", required = false)
    val nextDate: String,

    @Schema(description = "省份", example = "湖南", required = true)
    val province: String

)