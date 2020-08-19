package com.prasoon.expense.ui.profile

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prasoon.expense.ui.profile.gallery.GalleryAlbums
import com.prasoon.expense.ui.profile.gallery.GalleryData
import com.prasoon.expense.utils.event
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Singleton

@Singleton
class ProfileViewModel @ViewModelInject constructor(
    @ApplicationContext val application: Context
) : ViewModel() {

    private val allPhotos = arrayListOf<GalleryData>()
    private val albumPhotos = arrayListOf<GalleryData>()
    private val _allPhotoList = MutableLiveData<ArrayList<GalleryData>>()
    val allPhotoList = _allPhotoList.event()

    private val _albumName = MutableLiveData<String>()
    val albumName = _albumName.event()

    private val albums = arrayListOf<GalleryAlbums>()
    private val _albumList = MutableLiveData<ArrayList<GalleryAlbums>>()
    val albumList = _albumList.event()

    private val _navigateToMoveAndScale = MutableLiveData<Unit>()
    val navigateToMoveAndScale = _navigateToMoveAndScale.event()

    private val _navigateToCamera = MutableLiveData<Unit>()
    val navigateToCamera = _navigateToCamera.event()

    private val _photoUri = MutableLiveData<String>()
    val photoUri = _photoUri.event()
    lateinit var profileUri: String

    private val _navigateToGallery = MutableLiveData<Unit>()
    val navigateToGallery = _navigateToGallery.event()

    private val _navigateToProfile = MutableLiveData<String>()
    val navigateToProfile = _navigateToProfile.event()


    fun getPhoneAlbums() {
        allPhotos.clear()
        albums.clear()
        val galleryAlbums: ArrayList<GalleryAlbums> = ArrayList()
        val albumsNames: ArrayList<String> = ArrayList()

        val imagesProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.TITLE
        )
        val imagesQueryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imagescursor = application.contentResolver.query(
            imagesQueryUri,
            imagesProjection,
            null,
            null,
            null
        )

        Log.i("IMAGES", imagescursor?.count.toString())

        try {
            if (imagescursor != null && imagescursor.count > 0) {
                if (imagescursor.moveToFirst()) {
                    val idColumn = imagescursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val dataColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val dateAddedColumn =
                        imagescursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                    val titleColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.TITLE)
                    do {
                        val id = imagescursor.getString(idColumn)
                        val data = imagescursor.getString(dataColumn)
                        val dateAdded = imagescursor.getString(dateAddedColumn)
                        val title = imagescursor.getString(titleColumn)
                        val galleryData = GalleryData()
                        galleryData.albumName = File(data).parentFile?.name.toString()
                        galleryData.photoUri = data
                        galleryData.id = Integer.valueOf(id)
                        galleryData.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        galleryData.dateAdded = dateAdded
                        if (albumsNames.contains(galleryData.albumName)) {
                            for (album in galleryAlbums) {
                                if (album.name == galleryData.albumName) {
                                    galleryData.albumId = album.id
                                    album.albumPhotos.add(galleryData)
                                    allPhotos.add(galleryData)
                                    break
                                }
                            }
                        } else {
                            val album = GalleryAlbums()
                            album.id = galleryData.id
                            galleryData.albumId = galleryData.id
                            album.name = galleryData.albumName
                            album.coverUri = galleryData.photoUri
                            album.albumPhotos.add(galleryData)
                            allPhotos.add(galleryData)
                            galleryAlbums.add(album)
                            albumsNames.add(galleryData.albumName)
                        }
                    } while (imagescursor.moveToNext())
                }
                imagescursor.close()
            }
        } catch (e: Exception) {
            Log.e("IMAGE PICKER", e.toString())
        } finally {

//            photos.sortWith(compareByDescending { File(it.photoUri).lastModified() })
            if (allPhotos[0].photoUri != "camera_icon") {
                allPhotos.add(0, GalleryData(photoUri = "camera_icon"))
            }
            _allPhotoList.value = allPhotos

            prepareAlbumList(galleryAlbums)
        }
    }

    private fun prepareAlbumList(galleryAlbums: ArrayList<GalleryAlbums>) {
        galleryAlbums.sortWith(compareBy { it.name })
        for (album in galleryAlbums) {
            albums.add(album)
        }
        albums.add(0, GalleryAlbums(0, "All Photos", albumPhotos = allPhotos))
    }

    fun onAllPhotosClicked() {
        _albumList.value = albums
    }

    fun onImagePressed(photoUri: String) {
        if (photoUri == "camera_icon") {
            _navigateToCamera.value = Unit
        } else {
            _navigateToMoveAndScale.value = Unit
            _photoUri.value = photoUri
        }
    }

    fun onCameraImageSaved(savedUri: Uri?) {
        savedUri?.let {
            _navigateToMoveAndScale.value = Unit
            _photoUri.value = savedUri.toString()
        }
    }

    fun onGalleryPressed() {
        _navigateToGallery.value = Unit
    }

    fun onChooseImagePressed() {
        _navigateToProfile.value = _photoUri.value
        profileUri = _photoUri.value!!
    }

    fun onAlbumClicked(galleryAlbums: GalleryAlbums) {
        albumPhotos.clear()
        albumPhotos.addAll(galleryAlbums.albumPhotos)
        if (albumPhotos[0].photoUri != "camera_icon") {
            albumPhotos.add(0, GalleryData(photoUri = "camera_icon"))
        }
        _allPhotoList.value = albumPhotos
        _albumName.value = galleryAlbums.name
    }

}