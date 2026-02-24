package edu.nd.pmcburne.hwapp.one

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import edu.nd.pmcburne.hwapp.one.data.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.GameRepository
import edu.nd.pmcburne.hwapp.one.data.RetrofitInstance

class GameViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "games"
        ).build()

        val repository = GameRepository(
            api = RetrofitInstance.api,
            dao = database.gameDao()
        )

        @Suppress("UNCHECKED_CAST")
        return GameViewModel(repository) as T
    }
}
