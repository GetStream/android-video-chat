/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.android.video.chat.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.android.video.chat.compose.ui.AppNavHost
import io.getstream.android.video.chat.compose.ui.AppScreens
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStore: StreamUserDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isLoggedIn = dataStore.user.firstOrNull() != null

            setContent {
                VideoTheme {
                    AppNavHost(
                        startDestination = if (!isLoggedIn) {
                            AppScreens.Login.routeWithArg(true) // Pass true for autoLogIn
                        } else {
                            AppScreens.CallJoin.route
                        },
                    )
                }
            }
        }
    }
}
