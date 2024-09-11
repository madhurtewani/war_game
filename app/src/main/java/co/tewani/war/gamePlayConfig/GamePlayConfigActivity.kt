package co.tewani.war.gamePlayConfig

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.tewani.war.R
import co.tewani.war.databinding.ActivityGamePlayConfigBinding
import co.tewani.war.extensions.isNullOrEmpty
import co.tewani.war.gamePlay.GamePlayActivity
import com.google.android.material.snackbar.Snackbar

class GamePlayConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGamePlayConfigBinding
    private val viewModel: GamePlayConfigViewModel by viewModels()

    private val editorActionListener = TextView.OnEditorActionListener { v, actionId, event ->
        if ((event?.keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_DOWN)
            || actionId == IME_ACTION_DONE
        ) {
            handleImeActionDone(v)
            true
        } else {
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGamePlayConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initListeners()
    }

    private fun initListeners() {
        binding.btgNumberOfPlayers.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                toggleButtonClicked(checkedId)
            }
        }

        binding.bBack.setOnClickListener {
            goBack()
        }
        binding.bPlayGame.setOnClickListener {
            startGame()
        }

        binding.etPlayer2.setOnEditorActionListener(editorActionListener)
        binding.etPlayer3.setOnEditorActionListener(editorActionListener)
        binding.etPlayer4.setOnEditorActionListener(editorActionListener)
    }

    private fun toggleButtonClicked(checkedId: Int) {
        val numberOfPlayers = when (checkedId) {
            binding.b3Players.id -> 3
            binding.b4Players.id -> 4
            else -> 2
        }
        viewModel.setNumberOfPlayers(numberOfPlayers)
    }

    private fun goBack() {
        finish()
    }

    private fun startGame() {
        if (validateInput()) {
            viewModel.setPlayerName(1, binding.etPlayer1.text.toString())
            viewModel.setPlayerName(2, binding.etPlayer2.text.toString())
            if (viewModel.numberOfPlayers.get() >= 3) {
                viewModel.setPlayerName(3, binding.etPlayer3.text.toString())
            }
            if (viewModel.numberOfPlayers.get() >= 4) {
                viewModel.setPlayerName(4, binding.etPlayer4.text.toString())
            }
            val gamePlayActivityIntent = Intent(this, GamePlayActivity::class.java)
            gamePlayActivityIntent.putExtra("gamePlayConfig", viewModel.getGamePlayConfig())
            startActivity(gamePlayActivityIntent)
        }
    }

    private fun validateInput(): Boolean {
        val playerList = mutableListOf<String>()
        if (!binding.tilPlayer1.isNullOrEmpty(getString(R.string.error_please_enter_player_name))) {
            return false
        } else {
            playerList.add(binding.etPlayer1.text?.trim().toString())
        }
        if (!binding.tilPlayer2.isNullOrEmpty(getString(R.string.error_please_enter_player_name))) {
            return false
        } else {
            playerList.add(binding.etPlayer2.text?.trim().toString())
        }
        if (viewModel.numberOfPlayers.get() >= 3) {
            if (!binding.tilPlayer3.isNullOrEmpty(getString(R.string.error_please_enter_player_name))) {
                return false
            } else {
                playerList.add(binding.etPlayer3.text?.trim().toString())
            }
        }
        if (viewModel.numberOfPlayers.get() >= 4) {
            if (!binding.tilPlayer4.isNullOrEmpty(getString(R.string.error_please_enter_player_name))) {
                return false
            } else {
                playerList.add(binding.etPlayer4.text?.trim().toString())
            }
        }
        if (playerList.distinct().size != playerList.size) {
            Snackbar.make(
                this,
                binding.main,
                getString(R.string.error_player_name_should_be_unique),
                Snackbar.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun handleImeActionDone(view: View) {
        when (view) {
            binding.etPlayer2 -> {
                if (viewModel.numberOfPlayers.get() == 2) {
                    startGame()
                } else {
                    binding.etPlayer3.requestFocus()
                }
            }

            binding.etPlayer3 -> {
                if (viewModel.numberOfPlayers.get() == 3) {
                    startGame()
                } else {
                    binding.etPlayer4.requestFocus()
                }
            }

            binding.etPlayer4 -> {
                startGame()
            }
        }
    }
}