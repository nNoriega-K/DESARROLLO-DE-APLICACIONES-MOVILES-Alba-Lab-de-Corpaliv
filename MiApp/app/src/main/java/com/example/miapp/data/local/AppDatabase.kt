package com.example.miapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.miapp.data.model.Donacion
import com.example.miapp.data.model.Nota

@Database(entities = [Donacion::class, Nota::class], version = 3, exportSchema = false)
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

        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `donaciones_nueva` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`nombre` TEXT NOT NULL, " +
                            "`correo` TEXT NOT NULL, " +
                            "`monto` INTEGER NOT NULL, " +
                            "`ubicacion` TEXT NOT NULL, " +
                            "`fechaCargo` INTEGER NOT NULL, " +
                            "`creadaEn` INTEGER NOT NULL)"
                )

                db.execSQL(
                    "INSERT INTO `donaciones_nueva` " +
                            "(`id`, `nombre`, `correo`, `monto`, `ubicacion`, `fechaCargo`, `creadaEn`) " +
                            "SELECT `id`, `nombre`, `correo`, `monto`, 'Sin especificar', `fechaCargo`, `creadaEn` " +
                            "FROM `donaciones`"
                )
                db.execSQL("DROP TABLE `donaciones`")
                db.execSQL("ALTER TABLE `donaciones_nueva` RENAME TO `donaciones`")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "miapp.db"
                )
                    .addMigrations(MIGRACION_1_2, MIGRACION_2_3)
                    .build()
                    .also { instancia = it }
            }
    }
}
