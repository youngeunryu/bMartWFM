package wfm.shared.excel

import org.mapstruct.Mapper
import java.time.LocalDate

class SampleData(
    val fc: String,
    val fcId: Long,
    val date: LocalDate,
    val hour: Int,
    val orderQuantityRate: Double,
    val inboundQuantityRate: Double
)

class SampleEntity(
    val fc: String,
    val fcId: Long,
    val date: LocalDate,
    val hour: Int,
    val orderQuantityRate: Double,
    val inboundQuantityRate: Double
)

interface EntityMapper<E, D> {
    fun toEntity(d: D): E
    fun toData(e: E): D
}

@Mapper(componentModel = "spring")
interface SampleMapper : EntityMapper<SampleEntity, SampleData> {
//    override fun toEntity(d: SampleData): SampleEntity
//    override fun toData(e: SampleEntity): SampleData
}
