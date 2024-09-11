package co.tewani.war.gamePlay

import co.tewani.war.dto.response.DrawCardsResponse
import co.tewani.war.dto.response.PileResponse
import co.tewani.war.dto.response.ShuffleCardsResponse
import co.tewani.war.network.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GamePlayRepository {

    suspend fun shuffleDeck(deckId: String): ShuffleCardsResponse? {
        return try {
            withContext(Dispatchers.IO) {
                APIService.openShuffledDeck(deckId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun drawCardsFromDeck(deckId: String, count: Int = 1): DrawCardsResponse? {
        return try {
            withContext(Dispatchers.IO) {
                APIService.drawCardsFromDeck(deckId, count)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addCardsToPlayerPile(deckId: String, pileName: String, cardCodes: String): PileResponse? {
        return try {
            withContext(Dispatchers.IO) {
                APIService.addCardsToPile(deckId, pileName, cardCodes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun drawCardsFromPlayerPile(deckId: String, pileName: String, count: Int = 1): DrawCardsResponse? {
        return try {
            withContext(Dispatchers.IO) {
                APIService.drawCardsFromPile(deckId, pileName, count)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}