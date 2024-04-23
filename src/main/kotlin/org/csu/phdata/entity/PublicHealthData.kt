package org.csu.phdata.entity

import lombok.Data
import org.csu.phdata.entity.PublicHealthDatas.bindTo
import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDate

@Data
interface PublicHealthData: Entity<PublicHealthData>{
    companion object : Entity.Factory<PublicHealthData>()

    val disease: String
    val specificDisease:String
    val province: String
    val dataType: String
    val age: Int
    val monthDate: LocalDate
    val dataValue:String

}

 data class PHData (
     val disease: Column<String>,
     val specificDisease: Column<String>,
     val province: Column<String>,
     val dataType: Column<String>,
     val age: Column<Int>,
     val monthDate: Column<LocalDate>,
     val dataValue: Column<String>
 )

val cons = {it: DataTable ->
    val dataTable = when (it) {
        is PublicHealthDatas ->   PublicHealthDatas
        is CasesDatas ->  CasesDatas
        is DeathDatas ->  DeathDatas
        is CasesRates ->  CasesRates
        is DeathRates ->  DeathRates
        else -> {it}
    }
    PHData(
        dataTable.varchar("disease").bindTo { it.disease },
        dataTable.varchar("specific_disease").bindTo { it.specificDisease },
        dataTable.varchar("province").bindTo { it.province },
        dataTable.varchar("type").bindTo { it.dataType },
        dataTable.int("age").bindTo { it.age },
        dataTable.date("date").bindTo { it.monthDate },
        dataTable.varchar("value").bindTo { it.dataValue }
    )
}
open class DataTable(tableName: String) : Table<PublicHealthData>(tableName) {
    open lateinit var phData: PHData
}


object PublicHealthDatas : DataTable("public_health_data") {
    override var phData = PHData(
        varchar("disease").bindTo { it.disease },
        varchar("specific_disease").bindTo { it.specificDisease },
        varchar("province").bindTo { it.province },
        varchar("type").bindTo { it.dataType },
        int("age").bindTo { it.age },
        date("date").bindTo { it.monthDate },
        varchar("value").bindTo { it.dataValue }
    )
}

object CasesDatas : DataTable("cases_table") {
    override var phData = PHData(
        varchar("disease").bindTo { it.disease },
        varchar("specific_disease").bindTo { it.specificDisease },
        varchar("province").bindTo { it.province },
        varchar("type").bindTo { it.dataType },
        int("age").bindTo { it.age },
        date("date").bindTo { it.monthDate },
        varchar("value").bindTo { it.dataValue }
    )
}

object DeathDatas : DataTable("deaths_table") {
    override var phData = PHData(
        varchar("disease").bindTo { it.disease },
        varchar("specific_disease").bindTo { it.specificDisease },
        varchar("province").bindTo { it.province },
        varchar("type").bindTo { it.dataType },
        int("age").bindTo { it.age },
        date("date").bindTo { it.monthDate },
        varchar("value").bindTo { it.dataValue }
    )
}

object CasesRates : DataTable("incidence_rate_table") {
    override var phData = PHData(
        varchar("disease").bindTo { it.disease },
        varchar("specific_disease").bindTo { it.specificDisease },
        varchar("province").bindTo { it.province },
        varchar("type").bindTo { it.dataType },
        int("age").bindTo { it.age },
        date("date").bindTo { it.monthDate },
        varchar("value").bindTo { it.dataValue }
    )
}

object DeathRates : DataTable("mortality_rate_table") {
    override var phData = PHData(
        varchar("disease").bindTo { it.disease },
        varchar("specific_disease").bindTo { it.specificDisease },
        varchar("province").bindTo { it.province },
        varchar("type").bindTo { it.dataType },
        int("age").bindTo { it.age },
        date("date").bindTo { it.monthDate },
        varchar("value").bindTo { it.dataValue }
    )
}
