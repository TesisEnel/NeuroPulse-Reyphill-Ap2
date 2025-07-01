package io.github.reyx38.neuropulse.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.reyx38.neuropulse.data.local.database.NeuroPulseDb
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideNeuroPulseBd(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            NeuroPulseDb::class.java,
            "NeuroPulse.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideUsuarioDao(neuroPulseDb: NeuroPulseDb) = neuroPulseDb.usuarioDao()
}