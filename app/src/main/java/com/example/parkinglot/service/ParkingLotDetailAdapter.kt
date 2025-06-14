//app/src/main/java/com/example/parkinglot/service/ParkingLotDetailAdapter.kt
package com.example.parkinglot.service

import com.example.parkinglot.dto.response.ParkingLotDetail
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * JSON 내 숫자(float) → Long 필드,
 * 숫자/문자열 → String 필드 변환을
 * 안전하게 해 주는 어댑터
 */
class ParkingLotDetailAdapter : TypeAdapter<ParkingLotDetail>() {
    override fun write(out: JsonWriter, value: ParkingLotDetail) {
        // 우리는 read 전용이니, 그냥 delegate 쓰거나 아래처럼 간단히 씁니다.
        out.beginObject()
        out.name("addr").value(value.id)
        out.name("empty").value(value.empty)
        out.name("total").value(value.total)
        out.name("ratio").value(value.ratio)
        out.name("charge").value(value.charge)
        out.endObject()
    }

    override fun read(reader: JsonReader): ParkingLotDetail {
        var id = ""
        var empty = 0L
        var total = 0L
        var ratio = ""
        var charge = ""

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "addr"   -> id = reader.nextString()
                "empty"  -> empty = when (reader.peek()) {
                    JsonToken.STRING -> reader.nextString().toLong()
                    JsonToken.NUMBER -> reader.nextDouble().toLong()
                    else              -> { reader.skipValue(); 0L }
                }
                "total"  -> total = when (reader.peek()) {
                    JsonToken.STRING -> reader.nextString().toLong()
                    JsonToken.NUMBER -> reader.nextDouble().toLong()
                    else              -> { reader.skipValue(); 0L }
                }
                "ratio"  -> ratio = reader.nextString()
                "charge" -> charge = when (reader.peek()) {
                    JsonToken.STRING -> reader.nextString()
                    JsonToken.NUMBER -> reader.nextDouble().toLong().toString()
                    else              -> { reader.skipValue(); "" }
                }
                else     -> reader.skipValue()
            }
        }
        reader.endObject()

        return ParkingLotDetail(
            id     = id,
            empty  = empty,
            total  = total,
            ratio  = ratio,
            charge = charge
        )
    }
}
