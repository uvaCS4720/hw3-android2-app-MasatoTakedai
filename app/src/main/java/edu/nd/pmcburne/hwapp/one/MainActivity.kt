package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HWStarterRepoTheme {
                NcaaScoresApp()
            }
        }
    }

    @Composable
    fun NcaaScoresApp() {
        val context = LocalContext.current.applicationContext

        val viewModel: GameViewModel by viewModels {
            GameViewModelFactory(context)
        }

        GameScreen(viewModel = viewModel)
    }
}
