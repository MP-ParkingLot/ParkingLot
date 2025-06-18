//app/src/main/java/com/example/parkinglot/dto/network/converter/WrappingTypeAdapterFactory.kt

package com.example.parkinglot.data.network.converter

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

import kotlin.Suppress;
/**
 * JSON root 가 배열일 때
 * { "parkingLot": [ ... ] }
 * 처럼 감싸주고 파싱해주는 TypeAdapterFactory
 */
class WrappingTypeAdapterFactory<T>(
    private val wrapperType: Class<T>,
    private val arrayPropName: String
) : TypeAdapterFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <R> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        // wrapperType 이 아니면 패스
        if (type.rawType != wrapperType) return null

        // 기본(Delegate) 어댑터
        val delegate: TypeAdapter<R> = gson.getDelegateAdapter(this, type)
        val elementAdapter: TypeAdapter<JsonElement> = gson.getAdapter(JsonElement::class.java)

        return object : TypeAdapter<R>() {
            override fun write(out: JsonWriter, value: R) {
                delegate.write(out, value)
            }

            override fun read(reader: JsonReader): R {
                // 먼저 JsonElement 로 읽어들이고
                val element = elementAdapter.read(reader)
                val wrapped = when {
                    element.isJsonArray -> {
                        // root 가 배열이면 새 JsonObject 에 담아서
                        JsonObject().also { it.add(arrayPropName, element.asJsonArray) }
                    }
                    element.isJsonObject -> {
                        // 이미 객체라면 그대로
                        element.asJsonObject
                    }
                    else -> throw JsonParseException("Unexpected JSON type: $element")
                }
                // 그 다음 Delegate 어댑터로 trree->Object 로 변환
                return delegate.fromJsonTree(wrapped)
            }
        }
    }
}
