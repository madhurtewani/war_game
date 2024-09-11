package co.tewani.war.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class DrawCardsResponse(
    val success: Boolean,
    val deck_id: String,
    val remaining: Int = 0,
    val cards: List<Card>,
    val piles: JsonElement? = null
) {

    @Serializable
    data class Card(
        val code: String,
        val image: String,
        val images: CardImages,
        val value: String,
        val suit: String
    ) {

        @Serializable
        data class CardImages(
            val svg: String,
            val png: String
        ) {

            override fun toString(): String {
                return "CardImages(svg='$svg', png='$png')"
            }
        }

        override fun toString(): String {
            return "Card(code='$code', image='$image', images=$images, value='$value', suit='$suit')"
        }
    }

    override fun toString(): String {
        return "DrawCardsResponse(success=$success, deck_id='$deck_id', remaining=$remaining, cards=$cards, piles=$piles)"
    }
}
