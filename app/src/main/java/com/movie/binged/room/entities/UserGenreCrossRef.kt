package com.movie.binged.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    tableName = "user_genre_cross_ref",
    primaryKeys = ["userId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GenreEntity::class,
            parentColumns = ["id"],
            childColumns = ["genreId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("genreId")
    ]
)
data class UserGenreCrossRef(
    val userId: Long,
    val genreId: Long
)


data class UserWithGenres(
    @Embedded val user: UserEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = UserGenreCrossRef::class,
            parentColumn = "userId",
            entityColumn = "genreId"
        )
    )
    val genres: List<GenreEntity>
)
