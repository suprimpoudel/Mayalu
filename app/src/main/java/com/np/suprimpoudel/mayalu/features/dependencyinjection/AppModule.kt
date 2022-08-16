package com.np.suprimpoudel.mayalu.features.dependencyinjection

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.features.application.MyApplication
import com.np.suprimpoudel.mayalu.features.shared.widgets.CustomLoadingDialog
import com.np.suprimpoudel.mayalu.network.FirebaseService
import com.np.suprimpoudel.mayalu.network.RetrofitBuilder
import com.np.suprimpoudel.mayalu.utils.constants.DATA_POSTS
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesMyApplicationInstance(@ApplicationContext context: Context): MyApplication {
        return context as MyApplication
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseService.getFirebaseAuthInstance()

    @Singleton
    @Provides
    @Named("userDatabase")
    fun provideUserDatabase() = FirebaseService.getFirebaseDatabaseReference().reference.child(
        FirebaseConstant.DATA_USERS
    )

    @Singleton
    @Provides
    @Named("postDatabase")
    fun providePostDatabase() = FirebaseService.getFirebaseDatabaseReference().reference.child(
        DATA_POSTS
    )

    @Singleton
    @Provides
    @Named("chatDatabase")
    fun provideChatDatabase() = FirebaseService.getFirebaseDatabaseReference().reference.child(
        FirebaseConstant.DATA_CHATS
    )

    @Singleton
    @Provides
    @Named("firebaseDatabase")
    fun provideFirebaseDatabase() = FirebaseService.getFirebaseDatabaseReference()

    @Singleton
    @Provides
    fun provideFirebaseStorage() = FirebaseService.getFirebaseStorageReference()

    @Provides
    @Singleton
    fun providesGlideInstance(
        @ApplicationContext context: Context,
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.mayalu_app_icon)
            .error(R.drawable.mayalu_app_icon)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Singleton
    @Provides
    fun providesLoadingDialogInstance() = CustomLoadingDialog()

    @Singleton
    @Provides
    fun providesAPIService() = RetrofitBuilder.getApiService()
}