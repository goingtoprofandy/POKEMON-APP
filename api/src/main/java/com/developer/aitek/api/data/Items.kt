package com.developer.aitek.api.data

data class ItemPokemon(
    var name: String,
    var url: String,
    var id: String
)

data class ItemMyPokemon(
    var name: String,
    var id: String,
    var pokemon_id: String,
    var device_id: String
)
