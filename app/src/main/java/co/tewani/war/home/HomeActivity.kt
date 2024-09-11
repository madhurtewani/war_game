package co.tewani.war.home

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.tewani.war.R
import co.tewani.war.databinding.ActivityHomeBinding
import co.tewani.war.gamePlayConfig.GamePlayConfigActivity
import co.tewani.war.services.PreloadCardImageWorker
import co.tewani.war.utils.PreferenceManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        initListeners()
        preloadCardImages()
    }

    private fun initView() {
        val gameRulesArrayAdapter = ArrayAdapter(
            this,
            R.layout.item_game_rules,
            R.id.tv_game_rules,
            resources.getStringArray(R.array.label_game_rules)
        )
        binding.lvGameRules.setAdapter(gameRulesArrayAdapter)
    }

    private fun initListeners() {
        binding.bPlayGame.setOnClickListener {
            showGamePlayConfig()
        }
    }

    private fun showGamePlayConfig() {
        startActivity(Intent(this, GamePlayConfigActivity::class.java))
    }

    private fun preloadCardImages() {
        val preferenceManager = PreferenceManager(this)
        if (!preferenceManager.isPreloadImagesCompleted()) {
            val preloadCardImageRequest =
                OneTimeWorkRequestBuilder<PreloadCardImageWorker>().build()
            WorkManager.getInstance(this).enqueue(preloadCardImageRequest)
            preferenceManager.setPreloadImagesCompleted(true)
        }
    }
}