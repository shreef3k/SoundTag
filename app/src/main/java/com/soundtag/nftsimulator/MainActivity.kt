package com.soundtag.nftsimulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.soundtag.nftsimulator.ui.NftSimulatorRoot
import com.soundtag.nftsimulator.ui.theme.NftSimulatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NftSimulatorTheme { NftSimulatorRoot() } }
    }
}
