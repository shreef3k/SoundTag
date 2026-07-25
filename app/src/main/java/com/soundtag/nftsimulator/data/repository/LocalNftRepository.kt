package com.soundtag.nftsimulator.data.repository

import com.soundtag.nftsimulator.data.local.NftLocalDataSource
import com.soundtag.nftsimulator.data.remote.FakeRemoteDataSource
import com.soundtag.nftsimulator.domain.model.NftAvatar
import com.soundtag.nftsimulator.domain.repository.NftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LocalNftRepository(
    private val local: NftLocalDataSource,
    private val remote: FakeRemoteDataSource,
) : NftRepository {
    override val avatars: Flow<List<NftAvatar>> = local.avatars

    override suspend fun save(avatar: NftAvatar) {
        val current = local.avatars.first()
        val existing = current.firstOrNull { it.id == avatar.id }
        val saved = avatar.copy(isActive = avatar.isActive || existing?.isActive == true)
        val next = current.filterNot { it.id == avatar.id } + saved
        local.replaceAll(next.normalizeActive())
        remote.syncAvatars(next)
    }

    override suspend fun delete(id: String) {
        val next = local.avatars.first().filterNot { it.id == id }.normalizeActive()
        local.replaceAll(next)
    }

    override suspend fun setActive(id: String) {
        local.replaceAll(local.avatars.first().map { it.copy(isActive = it.id == id) })
    }

    private fun List<NftAvatar>.normalizeActive(): List<NftAvatar> =
        if (none { it.isActive } && isNotEmpty()) mapIndexed { index, nft -> nft.copy(isActive = index == 0) } else this
}
