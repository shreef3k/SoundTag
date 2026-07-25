package com.soundtag.nftsimulator.domain.repository

import com.soundtag.nftsimulator.domain.model.NftAvatar
import kotlinx.coroutines.flow.Flow

interface NftRepository {
    val avatars: Flow<List<NftAvatar>>
    suspend fun save(avatar: NftAvatar)
    suspend fun delete(id: String)
    suspend fun setActive(id: String)
}
