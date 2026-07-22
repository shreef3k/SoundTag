package com.soundtag.nftsimulator.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.soundtag.nftsimulator.domain.model.NftAvatar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.nftStore by preferencesDataStore("local_nft_avatars")

class NftLocalDataSource(private val context: Context) {
    private val avatarsKey = stringPreferencesKey("avatars")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    val avatars: Flow<List<NftAvatar>> = context.nftStore.data.map { preferences ->
        preferences[avatarsKey]?.let { runCatching { json.decodeFromString<List<NftAvatar>>(it) }.getOrDefault(emptyList()) }
            ?: seedAvatars()
    }

    suspend fun replaceAll(avatars: List<NftAvatar>) {
        context.nftStore.edit { it[avatarsKey] = json.encodeToString(avatars) }
    }

    private fun seedAvatars() = listOf(
        NftAvatar("default-blue", "Telegram Gem", 0xFF2AABEE, "✈", "rarity=starter; mood=online", isActive = true),
        NftAvatar("default-gold", "Golden Duck", 0xFFFFC107, "🦆", "rarity=rare; chain=local")
    )
}
