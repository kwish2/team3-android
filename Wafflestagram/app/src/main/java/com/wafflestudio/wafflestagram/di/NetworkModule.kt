package com.wafflestudio.wafflestagram.di

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.wafflestagram.BuildConfig
import com.wafflestudio.wafflestagram.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(LocalDateTime::class.java, LocalDateTimeAdapter().nullSafe())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
        val builder = OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
            }
        )
            builder.addInterceptor { chain ->
                val newRequestBuilder = chain.request().newBuilder()
                val token = sharedPreferences.getString("token", "")
                if(token != "") {
                    newRequestBuilder.addHeader(
                        "Authentication", "$token"
                    )
                }
                val newRequest = newRequestBuilder.build()
                chain.proceed(newRequest)
            }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object PreferenceModule {
        @Provides
        @Singleton
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("sharedpreferences", Context.MODE_PRIVATE)
        }
    }

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideSignUpService(retrofit: Retrofit): SignUpService {
        return retrofit.create(SignUpService::class.java)
    }

    @Provides
    @Singleton
    fun provideFeedService(retrofit: Retrofit): FeedService {
        return retrofit.create(FeedService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideCommentService(retrofit: Retrofit): CommentService {
        return retrofit.create(CommentService::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchService(retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }


    class LocalDateTimeAdapter : JsonAdapter<LocalDateTime>(){
        override fun toJson(writer: JsonWriter, value: LocalDateTime?) {
            value?.let { writer?.value(it.format(formatter)) }

        }

        override fun fromJson(reader: JsonReader): LocalDateTime? {
            return if (reader.peek() != JsonReader.Token.NULL) {
                fromNonNullString(reader.nextString())
            } else {
                reader.nextNull<Any>()
                null
            }    }
        private val formatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .toFormatter()

        private fun fromNonNullString(nextString: String) : LocalDateTime = LocalDateTime.parse(nextString, formatter)

    }
}