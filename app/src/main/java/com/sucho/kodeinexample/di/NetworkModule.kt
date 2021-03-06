package com.sucho.kodeinexample.di

import com.sucho.kodeinexample.BuildConfig
import com.sucho.kodeinexample.data.services.ApiService
import com.sucho.kodeinexample.data.services.GithubApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein.Module
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val MODULE_NAME = "Network Module"

val networkModule = Module(MODULE_NAME, false) {
  bind<OkHttpClient>() with singleton { getMockOkHttpClient() }
  bind<Retrofit>() with singleton { getMockRetrofit(instance()) }
  bind<ApiService>() with singleton { getMockApiService(instance()) }
  bind<GithubApiService>() with singleton { getMockeGithubApiService(instance()) }
}

private fun getMockOkHttpClient(): OkHttpClient {
  val httpBuilder = OkHttpClient.Builder()
  if (BuildConfig.ENABLE_LOGGING) {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    httpBuilder.interceptors()
        .add(httpLoggingInterceptor)
  }
  return httpBuilder.build()
}

private fun getMockRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .client(okHttpClient)
    .build()

private fun getMockApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

private fun getMockeGithubApiService(retrofit: Retrofit): GithubApiService = retrofit.newBuilder()
        .baseUrl("https://api.github.com").build().create(GithubApiService::class.java)