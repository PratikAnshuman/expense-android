package com.prasoon.expense.ui.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.prasoon.expense.R
import com.prasoon.expense.utils.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "CameraFragment"
private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

@AndroidEntryPoint
class CameraFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var camera: Camera

    var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    var flashEnabled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera(cameraSelector)

        profileViewModel.navigateToMoveAndScale.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.navigation_move_and_scale)
        })

        profileViewModel.navigateToGallery.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_camera_to_gallery)
        })

        captureIv.setOnClickListener { takePhoto() }

        closeIv.setOnClickListener { findNavController().popBackStack() }

        flipCameraIv.setOnClickListener { toggleFrontBackCamera() }

        flashIv.setOnClickListener { toggleFlash() }

        galleryIv.setOnClickListener { profileViewModel.onGalleryPressed() }
    }

    private fun startCamera(cameraSelector: CameraSelector) {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun toggleFrontBackCamera() {
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera(cameraSelector)
    }

    private fun toggleFlash() {
        flashEnabled = !flashEnabled

        if (flashEnabled) flashIv.setImageResource(R.drawable.ic_flash_on)
        else flashIv.setImageResource(R.drawable.ic_flash_off)

        if (camera.cameraInfo.hasFlashUnit()) {
            camera.cameraControl.enableTorch(flashEnabled)
        }
    }

    private fun takePhoto() {
        Log.d(TAG, "take photo called")
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    profileViewModel.onCameraImageSaved(savedUri)
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    override fun onDetach() {
        super.onDetach()
        cameraExecutor.shutdown()
    }
}