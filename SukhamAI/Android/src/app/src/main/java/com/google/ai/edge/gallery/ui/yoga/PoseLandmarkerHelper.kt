/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.edge.gallery.ui.yoga

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * Thin wrapper around MediaPipe's PoseLandmarker for one-shot image inference.
 *
 * Loads the bundled `pose_landmarker.task` (the "full" model variant copied from
 * mediapipe-samples) and runs detection on a single Bitmap. Designed to be created and
 * disposed per-screen rather than retained globally — initialization is cheap (<200 ms on
 * the S20+) and the model is tiny relative to Gemma, so there's no benefit to caching it.
 */
class PoseLandmarkerHelper(
  private val context: Context,
  private val modelAssetName: String = "pose_landmarker.task",
  private val minPoseDetectionConfidence: Float = 0.5f,
  private val minPoseTrackingConfidence: Float = 0.5f,
  private val minPosePresenceConfidence: Float = 0.5f,
) {
  private var poseLandmarker: PoseLandmarker? = null

  fun setup(useGpu: Boolean = false) {
    if (poseLandmarker != null) return
    val baseOptions =
      BaseOptions.builder()
        .setDelegate(if (useGpu) Delegate.GPU else Delegate.CPU)
        .setModelAssetPath(modelAssetName)
        .build()
    val options =
      PoseLandmarker.PoseLandmarkerOptions.builder()
        .setBaseOptions(baseOptions)
        .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
        .setMinTrackingConfidence(minPoseTrackingConfidence)
        .setMinPosePresenceConfidence(minPosePresenceConfidence)
        .setRunningMode(RunningMode.IMAGE)
        .setNumPoses(1)
        .build()
    poseLandmarker = PoseLandmarker.createFromOptions(context, options)
    Log.d(TAG, "Pose landmarker initialized (delegate=${if (useGpu) "GPU" else "CPU"})")
  }

  /**
   * Runs pose detection on [bitmap] and returns the result (or null if detection failed).
   * The caller is responsible for calling [close] when finished with the helper.
   */
  fun detect(bitmap: Bitmap): PoseLandmarkerResult? {
    val landmarker = poseLandmarker ?: run {
      setup()
      poseLandmarker
    } ?: return null
    val mpImage = BitmapImageBuilder(bitmap).build()
    return landmarker.detect(mpImage)
  }

  fun close() {
    poseLandmarker?.close()
    poseLandmarker = null
  }

  companion object {
    private const val TAG = "PoseLandmarkerHelper"
  }
}
