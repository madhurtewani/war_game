package co.tewani.war.gamePlay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.tewani.war.R
import co.tewani.war.databinding.ActivityGamePlayBinding
import co.tewani.war.dto.GamePlayCard
import co.tewani.war.dto.GamePlayConfig
import co.tewani.war.home.HomeActivity

class GamePlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGamePlayBinding
    private val viewModel: GamePlayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGamePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getExtras()
        initListeners()
        onViewReady()
        initObservers()
    }

    private fun getExtras() {
        intent.extras?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable("gamePlayConfig", GamePlayConfig::class.java)
                    ?.let { parcelableGamePlayConfig ->
                        viewModel.setGamePlayConfig(parcelableGamePlayConfig)
                    }
            } else {
                it.getParcelable<GamePlayConfig>("gamePlayConfig")
                    ?.let { parcelableGamePlayConfig ->
                        viewModel.setGamePlayConfig(parcelableGamePlayConfig)
                    }
            }
        }
    }

    private fun initListeners() {
        binding.bDrawCard.setOnClickListener {
            drawCard()
        }
        binding.bPlayAgain.setOnClickListener {
            finish()
        }
        binding.bBack.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }
    }

    private fun onViewReady() {
        viewModel.setNetworkCallProgressMessage(getString(R.string.loading_preparing_your_game))
        viewModel.prepareGame()
    }

    private fun initObservers() {
        viewModel.roundWinnerId.observe(this) { playerId ->
            highlightRoundWinner(playerId)
        }
        viewModel.gameWinner.observe(this) { winnerCard ->
            highlightGameWinner(winnerCard)
        }
    }

    private fun drawCard() {
        viewModel.setNetworkCallProgressMessage(getString(R.string.loading_drawing_card))
        viewModel.drawCard()
    }

    private fun highlightRoundWinner(
        playerId: Int,
        toReverseAnimate: Boolean = true,
        callback: AnimatorListenerAdapter? = null
    ) {
        when (playerId) {
            1 -> animateRoundWinner(
                binding.tvPlayer1Name,
                binding.ivPlayer1Card,
                binding.tvPlayer1Score,
                toReverseAnimate = toReverseAnimate,
                callback = callback
            )

            2 -> animateRoundWinner(
                binding.tvPlayer2Name,
                binding.ivPlayer2Card,
                binding.tvPlayer2Score,
                toReverseAnimate = toReverseAnimate,
                callback = callback
            )

            3 -> animateRoundWinner(
                binding.tvPlayer3Name,
                binding.ivPlayer3Card,
                binding.tvPlayer3Score,
                toReverseAnimate = toReverseAnimate,
                callback = callback
            )

            4 -> animateRoundWinner(
                binding.tvPlayer4Name,
                binding.ivPlayer4Card,
                binding.tvPlayer4Score,
                toReverseAnimate = toReverseAnimate,
                callback = callback
            )
        }
    }

    private fun animateRoundWinner(
        vararg views: View,
        toReverseAnimate: Boolean = true,
        callback: AnimatorListenerAdapter? = null
    ) {
        val scaleStart = 1f
        val scaleEnd = 1.05f
        val animationDuration =
            resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        val animationRepeatMode = ObjectAnimator.REVERSE
        val animationRepeatCount = 1
        val animationItems = mutableListOf<Animator>()
        views.forEach { view ->
            ObjectAnimator.ofFloat(view, "scaleY", scaleStart, scaleEnd).apply {
                if (toReverseAnimate) {
                    repeatMode = animationRepeatMode
                    repeatCount = animationRepeatCount
                }
                duration = animationDuration
                animationItems.add(this)
            }
            ObjectAnimator.ofFloat(view, "scaleX", scaleStart, scaleEnd).apply {
                if (toReverseAnimate) {
                    repeatMode = animationRepeatMode
                    repeatCount = animationRepeatCount
                }
                duration = animationDuration
                animationItems.add(this)
            }
        }
        AnimatorSet().apply {
            playTogether(animationItems)
            start()
            if (callback != null) {
                addListener(callback)
            }
        }
    }

    private fun highlightGameWinner(winnerCard: GamePlayCard) {
        binding.tvCongratulations.text =
            getString(R.string.label_congratulations, winnerCard.playerName)
        binding.tvCongratulationsMessage.text = Html.fromHtml(
            getString(R.string.label_winning_score, winnerCard.score.toString()),
            Html.FROM_HTML_MODE_COMPACT
        )

        highlightRoundWinner(winnerCard.playerId, false, object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.groupWinner.alpha = 0f
                binding.groupWinner.visibility = View.VISIBLE
                animateWinnerCard()
            }
        })
    }

    private fun animateWinnerCard() {
        val animationDuration =
            resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        val animationItems = mutableListOf<Animator>()
        ObjectAnimator.ofFloat(binding.vWinnerBg, "alpha", 0f, 0.5f).apply {
            duration = animationDuration
            animationItems.add(this)
        }
        ObjectAnimator.ofFloat(binding.mcvWinnerCard, "alpha", 0f, 1f).apply {
            duration = animationDuration
            animationItems.add(this)
        }
        AnimatorSet().apply {
            playTogether(animationItems)
            start()
        }
    }
}