package edu.nd.pmcburne.hwapp.one.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScoreboardResponse(
    val games: List<GameWrapper>
)

@Serializable
data class GameWrapper(
    val game: GameDto
)

@Serializable
data class GameDto(
    @SerialName("gameID") val gameId: String,
    val gameState: String,
    val startTime: String?,
    val currentPeriod: String?,
    val contestClock: String?,
    val home: TeamDto,
    val away: TeamDto
)

@Serializable
data class TeamDto(
    val score: String?,
    val winner: Boolean,
    val names: TeamNamesDto
)

@Serializable
data class TeamNamesDto(
    val short: String
)

fun GameDto.toEntity(
    date: String,
    gender: String
): GameEntity {

    return GameEntity(
        id = gameId,
        date = date,
        gender = gender,

        homeTeam = home.names.short,
        awayTeam = away.names.short,

        homeScore = home.score?.toIntOrNull(),
        awayScore = away.score?.toIntOrNull(),

        status = gameState,
        period = currentPeriod,
        clock = contestClock,
        startTime = startTime
    )
}