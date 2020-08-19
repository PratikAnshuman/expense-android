package com.prasoon.expense.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.prasoon.expense.R
import com.prasoon.expense.adapter.AlbumAdapter
import com.prasoon.expense.adapter.ImageGridAdapter
import com.prasoon.expense.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.util.*

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            profileViewModel.getPhoneAlbums()
        }

        profileViewModel.allPhotoList.observe(
            viewLifecycleOwner,
            EventObserver { galleryDataArrayList ->
                photoRv.visibility = View.VISIBLE
                albumRv.visibility = View.GONE

                photoRv.layoutManager = GridLayoutManager(requireContext(), 3)
                photoRv.adapter = ImageGridAdapter(galleryDataArrayList) {
                    profileViewModel.onImagePressed(it)
                }
            })

        profileViewModel.albumList.observe(viewLifecycleOwner, EventObserver { albumList ->
            photoRv.visibility = View.GONE
            albumRv.visibility = View.VISIBLE

            albumRv.layoutManager = LinearLayoutManager(requireContext())
            albumRv.adapter = AlbumAdapter(albumList) { galleryAlbums ->
                profileViewModel.onAlbumClicked(galleryAlbums)
            }
        })

        profileViewModel.albumName.observe(viewLifecycleOwner, EventObserver {
            albumNameTv.text = it.capitalize(Locale.ROOT)
        })

        profileViewModel.navigateToMoveAndScale.observe(viewLifecycleOwner, EventObserver {
//            val extras: Navigator.Extras = FragmentNavigatorExtras(image to "photo_uri")
            findNavController().navigate(R.id.navigation_move_and_scale)
        })

        profileViewModel.navigateToCamera.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.navigation_camera)
        })

        allPhotosIv.setOnClickListener {
            profileViewModel.onAllPhotosClicked()
        }

        cancelTv.setOnClickListener {
            if (albumRv.isVisible) {
                photoRv.visibility = View.VISIBLE
                albumRv.visibility = View.GONE
            } else {
                findNavController().popBackStack()
            }
        }
    }

}