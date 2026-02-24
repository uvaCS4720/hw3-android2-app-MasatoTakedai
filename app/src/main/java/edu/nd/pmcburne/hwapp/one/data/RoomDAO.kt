package edu.nd.pmcburne.hwapp.one.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val date: String,
    val gender: String, // "men" or "women"

    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int?,
    val awayScore: Int?,

    val status: String,
    val period: String?,
    val clock: String?,
    val startTime: String?
)

fun GameEntity.statusText(): String {
    return when (status) {
        "pre" -> "Starts at $startTime"
        "live" -> "$period • $clock"
        "final" -> "Final"
        else -> ""
    }
}

fun GameEntity.winner(): String? {
    if (status != "final") return null

    return when {
        homeScore!! > awayScore!! -> homeTeam
        awayScore!! > homeScore!! -> awayTeam
        else -> null
    }
}

@Dao
interface GameDao {
    @Query("""
        SELECT * FROM games
        WHERE date = :date AND gender = :gender
    """)
    fun getGames(date: String, gender: String): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)
}

@Database(entities = [GameEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}

