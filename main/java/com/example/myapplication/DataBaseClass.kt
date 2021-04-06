package com.example.myapplication

import android.net.Uri
import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

@Entity
data class Photo(
    @PrimaryKey val ph_id: Int,
    @ColumnInfo(name = "point") val point: String,
    @ColumnInfo(name = "path") val path: String
)

@Entity
data class Point(
    @PrimaryKey val p_id: Int,
    @ColumnInfo(name = "pos") val pos: String,
    @ColumnInfo(name = "path") val path: String
)

@Dao
interface PDao {
    @Query("SELECT * FROM point")
    fun getAllP(): List<Point>

    @Query("SELECT * FROM photo")
    fun getAllPh(): List<Photo>

    @Query("SELECT * FROM photo WHERE point = :pos")
    fun loadAllByPointPh(pos: String): List<Photo>

    @Insert
    fun insertP(point: Point)

    @Insert
    fun insertPh(photo: Photo)

}

@Database(entities = arrayOf(Point::class, Photo::class) , version =1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pDao(): PDao
}

