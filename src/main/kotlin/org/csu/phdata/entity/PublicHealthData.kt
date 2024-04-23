package org.csu.phdata.entity

import lombok.Data
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
object PublicHealthDatas : Table<PublicHealthData>("public_health_data") {
    val disease = varchar("disease").bindTo { it.disease }
    val specificDisease = varchar("specific_disease").bindTo { it.specificDisease }
    val province = varchar("province").bindTo { it.province }
    val dataType = varchar("type").bindTo { it.dataType }
    val age = int("age").bindTo { it.age }
    val monthDate = date("date").bindTo { it.monthDate }
    val dataValue = varchar("value").bindTo { it.dataValue }
}

object CasesDatas : Table<PublicHealthData>("cases_table") {
    val disease = varchar("disease").bindTo { it.disease }
    val specificDisease = varchar("specific_disease").bindTo { it.specificDisease }
    val province = varchar("province").bindTo { it.province }
    val dataType = varchar("type").bindTo { it.dataType }
    val age = int("age").bindTo { it.age }
    val monthDate = date("date").bindTo { it.monthDate }
    val dataValue = varchar("value").bindTo { it.dataValue }
}

object DeathDatas : Table<PublicHealthData>("deaths_table") {
    val disease = varchar("disease").bindTo { it.disease }
    val specificDisease = varchar("specific_disease").bindTo { it.specificDisease }
    val province = varchar("province").bindTo { it.province }
    val dataType = varchar("type").bindTo { it.dataType }
    val age = int("age").bindTo { it.age }
    val monthDate = date("date").bindTo { it.monthDate }
    val dataValue = varchar("value").bindTo { it.dataValue }
}

object CasesRates : Table<PublicHealthData>("incidence_rate_table") {
    val disease = varchar("disease").bindTo { it.disease }
    val specificDisease = varchar("specificDisease").bindTo { it.specificDisease }
    val province = varchar("province").bindTo { it.province }
    val dataType = varchar("type").bindTo { it.dataType }
    val age = int("age").bindTo { it.age }
    val monthDate = date("date").bindTo { it.monthDate }
    val dataValue = varchar("value").bindTo { it.dataValue }
}

object DeathRates : Table<PublicHealthData>("mortality_rate_table") {
    val disease = varchar("disease").bindTo { it.disease }
    val specificDisease = varchar("specific_disease").bindTo { it.specificDisease }
    val province = varchar("province").bindTo { it.province }
    val dataType = varchar("type").bindTo { it.dataType }
    val age = int("age").bindTo { it.age }
    val monthDate = date("date").bindTo { it.monthDate }
    val dataValue = varchar("value").bindTo { it.dataValue }
}
