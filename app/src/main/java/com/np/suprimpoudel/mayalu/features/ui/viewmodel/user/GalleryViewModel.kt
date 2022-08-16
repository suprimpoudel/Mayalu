package com.np.suprimpoudel.mayalu.features.ui.viewmodel.user

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.np.suprimpoudel.mayalu.features.shared.model.ChatData
import com.np.suprimpoudel.mayalu.features.shared.model.Photo
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
) : ViewModel() {
    val listOfPhotos: MutableLiveData<List<Photo>> = MutableLiveData()

    fun getAllPictures(contentResolver: ContentResolver) {
        val allPicturesList = mutableListOf<Photo>()
        val imageProjection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media._ID
        )
        val imageSortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            imageSortOrder
        )

        cursor.use {
            it?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val size = it.getString(sizeColumn)
                    val date = it.getString(dateColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    allPicturesList.add(
                        Photo(
                            id = id,
                            name = name,
                            size = size,
                            dateTaken = date,
                            contentUri = contentUri
                        )
                    )
                }
                listOfPhotos.postValue(allPicturesList)
            } ?: kotlin.run {
                Log.e("TAG", "Cursor is null!")
            }
        }
    }
}