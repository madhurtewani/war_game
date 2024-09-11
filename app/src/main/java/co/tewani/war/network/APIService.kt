package co.tewani.war.network

import co.tewani.war.dto.response.DrawCardsResponse
import co.tewani.war.dto.response.PileResponse
import co.tewani.war.dto.response.ShuffleCardsResponse
import co.tewani.war.network.NetworkClient.httpClientAndroid
import io.ktor.client.call.body
import io.ktor.client.request.get

object APIService {

    suspend fun openShuffledDeck(deckId: String): ShuffleCardsResponse {
        return httpClientAndroid.get("$deckId/shuffle/?deck_count=1").body()
    }

    suspend fun drawCardsFromDeck(deckId: String, count: Int = 1): DrawCardsResponse {
        return httpClientAndroid.get("$deckId/draw/?count=$count").body()
    }

    suspend fun addCardsToPile(deckId: String, pileName: String, cardCodes: String): PileResponse {
        return httpClientAndroid.get("$deckId/pile/$pileName/add/?cards=$cardCodes").body()
    }

    suspend fun drawCardsFromPile(deckId: String, pileName: String, count: Int = 1): DrawCardsResponse {
        return httpClientAndroid.get("$deckId/pile/$pileName/draw/random/?count=$count").body()
    }
}