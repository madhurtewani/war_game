package co.tewani.war.gamePlay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.tewani.war.dto.GamePlayCard
import co.tewani.war.dto.GamePlayConfig
import co.tewani.war.dto.response.DrawCardsResponse
import co.tewani.war.dto.response.PileResponse
import co.tewani.war.extensions.CardExtensions
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GamePlayViewModel(private val gamePlayRepository: GamePlayRepository = GamePlayRepository()) :
    ViewModel() {

    private val MAX_SCORE: Int = 10

    var gamePlayConfig: MutableLiveData<GamePlayConfig> = MutableLiveData()
        private set

    fun setGamePlayConfig(gamePlayConfig: GamePlayConfig) {
        this.gamePlayConfig.value = gamePlayConfig
        initGamePlayCards()
    }

    private val _isNetworkCallInProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNetworkCallInProgress: LiveData<Boolean> get() = _isNetworkCallInProgress

    private val _networkCallProgressMessage: MutableLiveData<String> = MutableLiveData()
    val networkCallProgressMessage: LiveData<String> get() = _networkCallProgressMessage

    private val _player1Card: MutableLiveData<GamePlayCard> = MutableLiveData()
    val player1Card: LiveData<GamePlayCard> get() = _player1Card
    private val _player2Card: MutableLiveData<GamePlayCard> = MutableLiveData()
    val player2Card: LiveData<GamePlayCard> get() = _player2Card
    private val _player3Card: MutableLiveData<GamePlayCard> = MutableLiveData()
    val player3Card: LiveData<GamePlayCard> get() = _player3Card
    private val _player4Card: MutableLiveData<GamePlayCard> = MutableLiveData()
    val player4Card: LiveData<GamePlayCard> get() = _player4Card

    private var currentCardList = mutableListOf<GamePlayCard>()

    private var deckId: MutableLiveData<String> = MutableLiveData("new")

    private val _isGameOver: MutableLiveData<Boolean> = MutableLiveData(false)
    val isGameOver: LiveData<Boolean> get() = _isGameOver

    private val _roundWinnerId: MutableLiveData<Int> = MutableLiveData(0)
    val roundWinnerId: LiveData<Int> get() = _roundWinnerId

    private val _gameWinner: MutableLiveData<GamePlayCard> = MutableLiveData()
    val gameWinner: LiveData<GamePlayCard> get() = _gameWinner

    fun setNetworkCallProgressMessage(message: String) {
        _networkCallProgressMessage.value = message
    }

    private fun initGamePlayCards() {
        _player1Card.value = GamePlayCard(
            1,
            gamePlayConfig.value!!.player1Name,
            "",
            remainingCards = 0,
            score = 0,
        )
        _player2Card.value = GamePlayCard(
            2,
            gamePlayConfig.value!!.player2Name,
            "",
            remainingCards = 0,
            score = 0,
        )
        _player3Card.value = GamePlayCard(
            3,
            gamePlayConfig.value!!.player3Name,
            "",
            remainingCards = 0,
            score = 0,
        )
        _player4Card.value = GamePlayCard(
            4,
            gamePlayConfig.value!!.player4Name,
            "",
            remainingCards = 0,
            score = 0,
        )
    }

    fun prepareGame() {
        showProgress(true)
        viewModelScope.launch {
            async { shuffleDeck() }.await()
            val countOfCardsForEachPlayer = 52 / gamePlayConfig.value!!.numberOfPlayers
            for (id in 1..gamePlayConfig.value!!.numberOfPlayers) {

                val drawnCards = async { drawCardsForPlayer(countOfCardsForEachPlayer) }.await()
                if (drawnCards.isEmpty()) {
                    showProgress(false)
                    return@launch
                }

                val drawnCardsCodes = drawnCards.joinToString(",") { it.code }

                val pileResponse = async {
                    addCardsToPlayerPile(
                        getGamePlayCardForPlayer(id).playerName,
                        drawnCardsCodes
                    )
                }.await()
                if (pileResponse == null) {
                    showProgress(false)
                    return@launch
                }
            }
            showProgress(false)
        }
    }

    private suspend fun shuffleDeck() {
        val shuffledDeck = gamePlayRepository.shuffleDeck(deckId = deckId.value!!)
        if (shuffledDeck?.success == true) {
            deckId.value = shuffledDeck.deck_id
        }
    }

    private suspend fun drawCardsForPlayer(
        count: Int,
    ): List<DrawCardsResponse.Card> {
        val drawnCards =
            gamePlayRepository.drawCardsFromDeck(deckId = deckId.value!!, count = count)
        return if (drawnCards?.success == true) {
            drawnCards.cards
        } else {
            emptyList()
        }
    }

    private suspend fun addCardsToPlayerPile(
        playerName: String,
        cardCodes: String,
    ): PileResponse? {
        val pileResponse = gamePlayRepository.addCardsToPlayerPile(
            deckId = deckId.value!!,
            pileName = playerName,
            cardCodes = cardCodes
        )
        return if (pileResponse?.success == true) {
            pileResponse
        } else {
            null
        }
    }

    fun drawCard() {
        showProgress(true)
        clearCardCodeForPlayer()
        viewModelScope.launch {
            for (id in 1..gamePlayConfig.value!!.numberOfPlayers) {
                val playerName: String = getGamePlayCardForPlayer(id).playerName
                val drawnCard =
                    async { drawOneCardFromPlayerPile(playerName) }.await()
                if (drawnCard != null) {
                    val remainingCardsInPlayerPile =
                        getRemainingCardsInPlayerPile(playerName, drawnCard.piles)
                    saveCardCodeForPlayer(
                        id,
                        drawnCard.cards.first(),
                        remainingCardsInPlayerPile
                    )
                } else {
                    showProgress(false)
                    return@launch
                }
            }
            val roundWinner = findRoundWinner()
            updateScoreForRoundWinner(roundWinner)
            transferCardsToRoundWinner(roundWinner)
            val gameWinner = checkIfGameOver()
            if (_isGameOver.value == true && gameWinner != null) {
                _gameWinner.postValue(gameWinner!!)
            } else {
                _roundWinnerId.postValue(roundWinner.playerId)
            }
            showProgress(false)
        }
    }

    private suspend fun drawOneCardFromPlayerPile(playerName: String): DrawCardsResponse? {
        val drawnCards = gamePlayRepository.drawCardsFromPlayerPile(
            deckId = deckId.value!!,
            pileName = playerName
        )
        return if (drawnCards?.success == true) {
            drawnCards
        } else {
            null
        }
    }

    private fun getRemainingCardsInPlayerPile(
        playerName: String,
        piles: JsonElement?
    ): Int {
        piles?.let { pile ->
            pile.jsonObject[playerName]?.let { playerPile ->
                playerPile.jsonObject["remaining"]?.let { remaining ->
                    return remaining.jsonPrimitive.int
                }
            }
        }
        return 0
    }

    private fun saveCardCodeForPlayer(
        playerId: Int,
        drawnCard: DrawCardsResponse.Card,
        remainingCards: Int
    ) {
        when (playerId) {
            1 -> {
                _player1Card.value?.let {
                    val updatedPlayerCard = it.copy(
                        cardCode = drawnCard.code,
                        cardUrl = drawnCard.image,
                        cardValue = CardExtensions.getCardValue(drawnCard.code),
                        remainingCards = remainingCards
                    )
                    _player1Card.value = updatedPlayerCard
                }
                currentCardList.add(_player1Card.value!!)
            }

            2 -> {
                _player2Card.value?.let {
                    val updatedPlayerCard = it.copy(
                        cardCode = drawnCard.code,
                        cardUrl = drawnCard.image,
                        cardValue = CardExtensions.getCardValue(drawnCard.code),
                        remainingCards = remainingCards
                    )
                    _player2Card.value = updatedPlayerCard
                }
                currentCardList.add(_player2Card.value!!)
            }

            3 -> {
                _player3Card.value?.let {
                    val updatedPlayerCard = it.copy(
                        cardCode = drawnCard.code,
                        cardUrl = drawnCard.image,
                        cardValue = CardExtensions.getCardValue(drawnCard.code),
                        remainingCards = remainingCards
                    )
                    _player3Card.value = updatedPlayerCard
                }
                currentCardList.add(_player3Card.value!!)
            }

            4 -> {
                _player4Card.value?.let {
                    val updatedPlayerCard = it.copy(
                        cardCode = drawnCard.code,
                        cardUrl = drawnCard.image,
                        cardValue = CardExtensions.getCardValue(drawnCard.code),
                        remainingCards = remainingCards
                    )
                    _player4Card.value = updatedPlayerCard
                }
                currentCardList.add(_player4Card.value!!)
            }
        }
    }

    private fun saveRemainingCardsForPlayer(
        playerId: Int,
        remainingCards: Int
    ) {
        when (playerId) {
            1 -> {
                _player1Card.value?.let {
                    val updatedPlayerCard = it.copy(
                        remainingCards = remainingCards
                    )
                    _player1Card.value = updatedPlayerCard
                }
            }

            2 -> {
                _player2Card.value?.let {
                    val updatedPlayerCard = it.copy(
                        remainingCards = remainingCards
                    )
                    _player2Card.value = updatedPlayerCard
                }
            }

            3 -> {
                _player3Card.value?.let {
                    val updatedPlayerCard = it.copy(
                        remainingCards = remainingCards
                    )
                    _player3Card.value = updatedPlayerCard
                }
            }

            4 -> {
                _player4Card.value?.let {
                    val updatedPlayerCard = it.copy(
                        remainingCards = remainingCards
                    )
                    _player4Card.value = updatedPlayerCard
                }
            }
        }
    }

    private fun clearCardCodeForPlayer() {
        currentCardList.clear()
    }

    private fun findRoundWinner(): GamePlayCard {
        val sortedCardList =
            currentCardList.sortedWith(
                compareByDescending<GamePlayCard> { it.cardValue }
                    .thenByDescending { it.remainingCards }
            )
        return sortedCardList.first()
    }

    private fun updateScoreForRoundWinner(winner: GamePlayCard) {
        when (winner.playerId) {
            1 -> _player1Card.value?.let {
                val updatedPlayerCard = it.copy(score = it.score.plus(1))
                _player1Card.value = updatedPlayerCard
            }
            2 -> _player2Card.value?.let {
                val updatedPlayerCard = it.copy(score = it.score.plus(1))
                _player2Card.value = updatedPlayerCard
            }
            3 -> _player3Card.value?.let {
                val updatedPlayerCard = it.copy(score = it.score.plus(1))
                _player3Card.value = updatedPlayerCard
            }
            4 -> _player4Card.value?.let {
                val updatedPlayerCard = it.copy(score = it.score.plus(1))
                _player4Card.value = updatedPlayerCard
            }
        }
    }

    private suspend fun transferCardsToRoundWinner(roundWinner: GamePlayCard) {
        val returnCards = currentCardList.joinToString(",") { it.cardCode }
        val pileResponse = addCardsToPlayerPile(
            getGamePlayCardForPlayer(roundWinner.playerId).playerName,
            returnCards
        )
        val remainingCardsInPile = getRemainingCardsInPlayerPile(roundWinner.playerName, pileResponse?.piles)
        saveRemainingCardsForPlayer(roundWinner.playerId, remainingCardsInPile)
    }

    private fun checkIfGameOver(): GamePlayCard? {
        if (_player1Card.value!!.score == MAX_SCORE) {
            _isGameOver.value = true
            return _player1Card.value!!
        }
        if (_player2Card.value!!.score == MAX_SCORE) {
            _isGameOver.value = true
            return _player2Card.value!!
        }
        if (_player3Card.value!!.score == MAX_SCORE) {
            _isGameOver.value = true
            return _player3Card.value!!
        }
        if (_player4Card.value!!.score == MAX_SCORE) {
            _isGameOver.value = true
            return _player4Card.value!!
        }
        if (currentCardList.any { it.remainingCards == 0 }) {
            _isGameOver.value = true
            return currentCardList.maxByOrNull { it.remainingCards }!!
        }
        _isGameOver.value = false
        return null
    }

    private fun getGamePlayCardForPlayer(playerId: Int): GamePlayCard {
        return when (playerId) {
            1 -> _player1Card.value!!
            2 -> _player2Card.value!!
            3 -> _player3Card.value!!
            else -> _player4Card.value!!
        }
    }

    private fun showProgress(toShow: Boolean) {
        _isNetworkCallInProgress.value = toShow
    }
}