package org.csu.phdata.entity.parameters

import io.swagger.v3.oas.annotations.media.Schema

data class SpecificParameter (

    @Schema(description = "病种", example = "包虫病", required = true)
    val disease: String,

    @Schema(description = "省份", example = "湖南省", required = true)
    val province: String,

    @Schema(description = "数据类型", example = "cases_data", required = true)
    val dataType: String,

    @Schema(description = "年龄", example = "30-", required = true)
    val age: String,

    @Schema(description = "月份", example = "2005-01-01", required = true)
    val date: String
)