package com.soundtag.nftsimulator.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NftAvatar(
    val id: String,
    val name: String,
    val color: Long,
    val icon: String,
    val properties: String,
    val imageUrl: String = "",
    val isActive: Boolean = false,
)
