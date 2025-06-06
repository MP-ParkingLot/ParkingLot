package com.example.parkinglot.dto.response

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import com.example.parkinglot.dto.response.ParkingLotDetail

@Root(name = "GetParkingInfo", strict = false)
data class ParkingInfoResponse(
    @field:ElementList(name = "row", inline = true)
    var rows: List<ParkingLotDetail>? = null
)
