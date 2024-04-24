package org.csu.phdata.util

class DataFieldUtil {
    companion object {
        fun convertProvinces(province: String): String {
            val suffixes = listOf("省", "自治区", "市")
            var result = province
            for (suffix in suffixes) {
                if (province.endsWith(suffix)) {
                    result = province.removeSuffix(suffix)
                    break
                }
            }
            return result
        }
        fun getAgeInterval(age: String, nextAge: String): List<String> {
            val ageList = listOf(
                "0-",
                "1-",
                "2-",
                "3-",
                "4-",
                "5-",
                "6-",
                "7-",
                "8-",
                "9-",
                "10-",
                "15-",
                "20-",
                "25-",
                "30-",
                "35-",
                "40-",
                "45-",
                "50-",
                "55-",
                "60-",
                "65-",
                "70-",
                "75-",
                "80-",
                "85及以上",
                "不详"
            )
            var index = -1
            var nextIndex = -1
            if ( age.isNotEmpty() ) {
                index = ageList.indexOfFirst { it == age }
            }
            if ( nextAge.isNotEmpty() ){
                nextIndex = ageList.indexOfFirst { it == nextAge }
            }
            if ( index != -1 && nextIndex != -1) {
                return ageList.subList(index, nextIndex+1)
            }
            if ( index != -1 ) {
                return ageList.subList(index, index+1)
            }
            if (nextIndex != -1) {
                return ageList.subList(nextIndex, nextIndex+1)
            }
            return listOf()
        }
    }
}