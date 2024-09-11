package co.tewani.war.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PileResponse(
    val success: Boolean,
    val deck_id: String,
    val remaining: Int,
    val piles: JsonElement? = null
) {

    override fun toString(): String {
        return "AddToPileResponse(success=$success, deck_id='$deck_id', remaining=$remaining)"
    }
}
