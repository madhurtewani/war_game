package co.tewani.war.gamePlayConfig

import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import co.tewani.war.dto.GamePlayConfig

class GamePlayConfigViewModel : ViewModel() {

    var numberOfPlayers = ObservableInt(2)
    var playerNames = hashMapOf<Int, String>()

    fun setNumberOfPlayers(numberOfPlayers: Int) {
        this.numberOfPlayers.set(numberOfPlayers)
    }

    fun setPlayerName(playerId: Int, name: String) {
        playerNames[playerId] = name
    }

    fun getGamePlayConfig(): GamePlayConfig {
        return GamePlayConfig(
            numberOfPlayers = numberOfPlayers.get(),
            player1Name = playerNames[1] ?: "",
            player2Name = playerNames[2] ?: "",
            player3Name = if (numberOfPlayers.get() >=3) playerNames[3] ?: "" else "",
            player4Name = if (numberOfPlayers.get() >=4) playerNames[4] ?: "" else ""
        )
    }
}