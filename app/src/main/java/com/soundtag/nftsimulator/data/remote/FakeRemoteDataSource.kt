package com.soundtag.nftsimulator.data.remote

import com.soundtag.nftsimulator.domain.model.NftAvatar

class FakeRemoteDataSource {
    suspend fun syncAvatars(avatars: List<NftAvatar>) = Result.success(avatars)
    suspend fun fetchTelegramImageUrl(nftId: String): Result<String> = Result.success("")
}
