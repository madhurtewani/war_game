package co.tewani.war.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ShuffleCardsResponse(
    val success: Boolean,
    val deck_id: String,
    val shuffled: Boolean,
    val remaining: Int
) {
    override fun toString(): String {
        return "ShuffleCardsResponse(success=$success, deck_id='$deck_id', shuffled=$shuffled, remaining=$remaining)"
    }
}
