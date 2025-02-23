package io.hangout.guide.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.hangout.guide.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GenerativeAIModule {
    @Provides
    @Singleton
    fun provideGenerationConfig(): GenerationConfig {
        return generationConfig {
            temperature = 0.7f
        }
    }

    @Provides
    @Singleton
    fun provideGenerativeModel(config: GenerationConfig): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-2.0-pro-exp-02-05",
            apiKey = BuildConfig.GOOGLE_GEMINI_API_KEY,
            generationConfig = config
        )
    }
}