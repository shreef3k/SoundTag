package com.soundtag.nftsimulator.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soundtag.nftsimulator.data.local.NftLocalDataSource
import com.soundtag.nftsimulator.data.remote.FakeRemoteDataSource
import com.soundtag.nftsimulator.data.repository.LocalNftRepository
import com.soundtag.nftsimulator.domain.model.NftAvatar
import com.soundtag.nftsimulator.domain.repository.NftRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class NftViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NftRepository = LocalNftRepository(NftLocalDataSource(application), FakeRemoteDataSource())
    val avatars = repository.avatars.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun save(id: String?, name: String, color: Long, icon: String, properties: String, imageUrl: String) = viewModelScope.launch {
        repository.save(NftAvatar(id ?: UUID.randomUUID().toString(), name, color, icon, properties, imageUrl))
    }

    fun setActive(id: String) = viewModelScope.launch { repository.setActive(id) }
    fun delete(id: String) = viewModelScope.launch { repository.delete(id) }
}
