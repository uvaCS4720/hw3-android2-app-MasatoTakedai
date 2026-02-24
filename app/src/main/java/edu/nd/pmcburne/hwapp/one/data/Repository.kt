package edu.nd.pmcburne.hwapp.one.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GameRepository(
    private val api: NcaaApiService,
    private val dao: GameDao
) {

    fun observeGames(
        date: String,
        gender: String
    ): Flow<List<GameEntity>> {
        return dao.getGames(date, gender)
    }

    suspend fun refreshGames(
        date: LocalDate,
        gender: String
    ) {
        val response = api.getScoreboard(
            gender = gender,
            year = date.year.toString(),
            month = "%02d".format(date.monthValue),
            day = "%02d".format(date.dayOfMonth)
        )
        Log.d("API", "Games returned: ${response.games.size}")

        val entities = response.games.map {
            it.game.toEntity(date.toString(), gender)
        }

        dao.insertGames(entities)
    }
}


