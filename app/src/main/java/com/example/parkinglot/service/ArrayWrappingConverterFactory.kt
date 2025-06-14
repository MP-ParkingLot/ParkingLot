// app/src/main/java/com/example/parkinglot/service/ArrayWrappingAdapterFactory.kt
package com.example.parkinglot.service

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * 서버가 root 로 배열만 던져줄 때
 *   [ {…}, {…} ]
 * Gson 이 바로 wrapper DTO 로 못 읽으니,
 * 강제로 { "parkingLot": [ … ] } 형태로 감싸 주는 어댑터입니다.
 */
class ArrayWrappingAdapterFactory<T>(
    private val wrapperType: Class<T>,
    private val arrayFieldName: String
) : TypeAdapterFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <R> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        if (type.rawType != wrapperType) return null

        val delegate: TypeAdapter<R> = gson.getDelegateAdapter(this, type)
        val elementAdapter: TypeAdapter<JsonElement> = gson.getAdapter(JsonElement::class.java)

        return object : TypeAdapter<R>() {
            override fun write(out: JsonWriter, value: R) {
                delegate.write(out, value)
            }

            override fun read(reader: JsonReader): R {
                val json = elementAdapter.read(reader)
                val wrapped = when {
                    json.isJsonArray -> JsonObject().apply {
                        add(arrayFieldName, json.asJsonArray)
                    }
                    else -> json.asJsonObject
                }
                return delegate.fromJsonTree(wrapped)
            }
        }
    }
}
