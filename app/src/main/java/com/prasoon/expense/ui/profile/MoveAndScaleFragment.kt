package com.prasoon.expense.ui.profile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.prasoon.expense.R
import com.prasoon.expense.ui.profile.gallery.RunOnUiThread
import com.prasoon.expense.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_move_and_scale.*
import org.jetbrains.anko.doAsync


@AndroidEntryPoint
class MoveAndScaleFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_move_and_scale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.photoUri.observe(viewLifecycleOwner, EventObserver { photoUri ->
            Log.i("photoUri", photoUri)
            doAsync {
                RunOnUiThread(context).safely {
                    try {
                        val requestListener: RequestListener<Drawable> =
                            object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    selectedIv.alpha = 0.3f
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }
                            }
                        Glide.with(requireContext()).load(photoUri)
                            .apply(RequestOptions().fitCenter().override(Target.SIZE_ORIGINAL))
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .listener(requestListener).into(selectedIv)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })

        profileViewModel.navigateToProfile.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.navigation_profile)
        })

        cancelTv.setOnClickListener { findNavController().popBackStack() }
        chooseTv.setOnClickListener { profileViewModel.onChooseImagePressed() }
    }
}