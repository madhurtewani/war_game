package co.tewani.war.dto

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import co.tewani.war.extensions.CardExtensions

data class GamePlayCard(
    val playerId: Int,
    val playerName: String,
    var cardCode: String,
    @get:Bindable
    var cardUrl: String = "https://deckofcardsapi.com/static/img/back.png",
    var remainingCards: Int,
    var cardValue: Int = CardExtensions.getCardValue(cardCode),
    @get:Bindable
    var score: Int = 0
): BaseObservable()
