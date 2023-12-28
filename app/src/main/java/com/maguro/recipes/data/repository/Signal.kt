package com.maguro.recipes.data.repository

sealed interface Signal {
    object InitialLoad: Signal
    object ReloadAll: Signal
    data class ReloadWithId(val id: String): Signal
}
