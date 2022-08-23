package org.dhis2.android.rtsm.data.models.sdk

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class RTSProgram(
    val programUid: String,
    val itemCode: String,
    val itemDescription: String,
    val stockOnHand: String,
    val transactions: List<Transaction>
)

enum class TransactionType {
    DISTRIBUTED,
    CORRECTED,
    DISCARDED
}

@Serializable(with = ModuleSerializer::class)
sealed class Transaction {
    abstract val order: Int
    abstract val transactionType: TransactionType

    @Serializable
    data class Distributed(
        override val order: Int,
        override val transactionType: TransactionType,
        val distributedTo: String,
        val stockDistributed: String
    ) : Transaction()

    @Serializable
    data class Discarded(
        override val order: Int,
        override val transactionType: TransactionType,
        val stockDiscarded: String
    ) : Transaction()

    @Serializable
    data class Correction(
        override val order: Int,
        override val transactionType: TransactionType,
        val stockCorrected: String
    ) : Transaction()
}

object ModuleSerializer : JsonContentPolymorphicSerializer<Transaction>(Transaction::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Transaction> {
        return when (element.jsonObject["transactionType"]?.jsonPrimitive?.content) {
            "DISTRIBUTED" -> Transaction.Distributed.serializer()
            "CORRECTED" -> Transaction.Correction.serializer()
            "DISCARDED" -> Transaction.Discarded.serializer()
            else -> throw Exception("Unknown Transaction: key 'transactionType' not found")
        }
    }
}
