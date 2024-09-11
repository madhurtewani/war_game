package co.tewani.war.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PreloadCardImageWorker(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        for (cardCode in generateCardCodes()) {
            invokeGlidePreload(cardCode)
        }

        return Result.success()
    }

    private fun invokeGlidePreload(cardCode: String) {
        Glide.with(applicationContext)
            .load("https://deckofcardsapi.com/static/img/$cardCode.png")
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .preload()
    }

    private fun generateCardCodes(): List<String> {
        val suits = listOf("H", "D", "C", "S")
        val numbers = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "0", "J", "Q", "K")

        val cardCodes = mutableListOf<String>()

        for (suit in suits) {
            for (number in numbers) {
                val cardCode = "$number$suit"
                cardCodes.add(cardCode)
            }
        }

        return cardCodes
    }

}