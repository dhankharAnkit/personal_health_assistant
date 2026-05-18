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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.max

/**
 * Renders the 33 pose landmarks and the standard MediaPipe skeleton connections directly onto
 * a copy of the input bitmap and returns it. We bake the overlay into pixels (rather than
 * drawing in a Compose Canvas) so the same bitmap can be:
 *   1. shown in the UI,
 *   2. saved to the user's gallery, and
 *   3. forwarded as-is to Gemma for visual yoga coaching.
 */
object PoseOverlay {
  fun drawOverlay(source: Bitmap, result: PoseLandmarkerResult): Bitmap {
    val output = source.copy(Bitmap.Config.ARGB_8888, true)
    if (result.landmarks().isEmpty()) return output

    val canvas = Canvas(output)
    val w = output.width.toFloat()
    val h = output.height.toFloat()
    val strokePx = max(4f, minOf(w, h) * 0.006f)
    val pointPx = max(6f, minOf(w, h) * 0.009f)

    val linePaint =
      Paint().apply {
        color = Color.parseColor("#00E5FF")
        strokeWidth = strokePx
        isAntiAlias = true
      }
    val pointPaint =
      Paint().apply {
        color = Color.parseColor("#FFD740")
        style = Paint.Style.FILL
        isAntiAlias = true
      }

    val landmarks = result.landmarks()[0]
    PoseLandmarker.POSE_LANDMARKS.forEach { connection ->
      val start = landmarks[connection.start()]
      val end = landmarks[connection.end()]
      canvas.drawLine(start.x() * w, start.y() * h, end.x() * w, end.y() * h, linePaint)
    }
    landmarks.forEach { lm -> canvas.drawCircle(lm.x() * w, lm.y() * h, pointPx, pointPaint) }
    return output
  }
}
