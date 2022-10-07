package eu.sesma.devcalc.editor

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import eu.sesma.devcalc.CalcState
import java.io.InputStream
import java.io.OutputStream

object CalcStateSerializer : Serializer<CalcState> {

    override val defaultValue: CalcState
        get() = CalcState.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CalcState {
        try {
            return CalcState.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: CalcState, output: OutputStream) = t.writeTo(output)
}

val Context.calcStateDataStore: DataStore<CalcState> by dataStore(
    fileName = "calc.pb",
    serializer = CalcStateSerializer
)