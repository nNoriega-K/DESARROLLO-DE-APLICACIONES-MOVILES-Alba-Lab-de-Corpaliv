package com.example.miapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.miapp.data.model.Donacion
import com.example.miapp.data.model.Nota

@Database(entities = [Donacion::class, Nota::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun donacionDao(): DonacionDao
    abstract fun notaDao(): NotaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `notas` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`titulo` TEXT NOT NULL, " +
                            "`contenido` TEXT NOT NULL, " +
                            "`creadaEn` INTEGER NOT NULL)"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "miapp.db"
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()
                    .also { instancia = it }
            }
    }
}
