package com.example.parkinglot.dto.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "GetParkingInfo", strict = false)
data class NearByParkinglotResponse(
    @field:Element(name = "list_total_count", required = false)
    var listTotalCount: Int? = null,

    @field:Element(name = "RESULT", required = false)
    var result: ApiResult? = null,

    @field:ElementList(name = "row", inline = true, required = false)
    var rows: List<ParkingLotDetail>? = null
)

@Root(name = "RESULT", strict = false)
data class ApiResult(
    @field:Element(name = "CODE", required = false)
    var code: String? = null,

    @field:Element(name = "MESSAGE", required = false)
    var message: String? = null
)
