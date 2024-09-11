package co.tewani.war.dto

import android.os.Parcel
import android.os.Parcelable

data class GamePlayConfig(
    val numberOfPlayers: Int,
    val player1Name: String,
    val player2Name: String,
    val player3Name: String,
    val player4Name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(numberOfPlayers)
        parcel.writeString(player1Name)
        parcel.writeString(player2Name)
        parcel.writeString(player3Name)
        parcel.writeString(player4Name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GamePlayConfig> {
        override fun createFromParcel(parcel: Parcel): GamePlayConfig {
            return GamePlayConfig(parcel)
        }

        override fun newArray(size: Int): Array<GamePlayConfig?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "Number of Players: $numberOfPlayers, " +
                "Player 1 Name: $player1Name, " +
                "Player 2 Name: $player2Name, " +
                "Player 3 Name: $player3Name, " +
                "Player 4 Name: $player4Name"
    }

    fun getPlayerName(playerId: Int): String {
        return when (playerId) {
            1 -> player1Name
            2 -> player2Name
            3 -> player3Name
            4 -> player4Name
            else -> ""
        }
    }
}
