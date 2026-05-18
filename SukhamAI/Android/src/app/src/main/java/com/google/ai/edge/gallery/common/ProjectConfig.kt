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

package com.google.ai.edge.gallery.common

import androidx.core.net.toUri
import net.openid.appauth.AuthorizationServiceConfiguration

object ProjectConfig {
  // OAuth app: https://huggingface.co/settings/applications/6a08b6c66fa89bccd42b83fc
  // Set false to use manual hf_ token in Settings instead.
  const val useHuggingFaceOAuthSignIn = false

  // Client ID from that application's settings page (must match exactly).
  const val clientId = "a7cf32ed-7943-4504-84f4-da9415abfbf8"

  // Add this exact string under "Redirect URLs" on the HF application page.
  const val redirectUri = "com.sukham.ai:/oauth2redirect"

  // OAuth 2.0 Endpoints (Authorization + Token Exchange)
  private const val authEndpoint = "https://huggingface.co/oauth/authorize"
  private const val tokenEndpoint = "https://huggingface.co/oauth/token"

  // OAuth service configuration (AppAuth library requires this)
  val authServiceConfig =
    AuthorizationServiceConfiguration(
      authEndpoint.toUri(), // Authorization endpoint
      tokenEndpoint.toUri(), // Token exchange endpoint
    )
}
