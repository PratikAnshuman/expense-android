package com.prasoon.expense.ui.profile.gallery

import kotlin.collections.ArrayList

data class GalleryAlbums(
    var id: Int = 0,
    var name: String = "",
    var coverUri: String = "",
    var albumPhotos: ArrayList<GalleryData> = ArrayList()
)