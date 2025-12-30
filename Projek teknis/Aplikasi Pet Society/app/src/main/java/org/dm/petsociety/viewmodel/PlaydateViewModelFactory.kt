// File: org.dm.petsociety.viewmodel.PlaydateViewModelFactory.kt (BARU)

package org.dm.petsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

// PlaydateViewModel tidak membutuhkan AuthVM, jadi kita bisa menggunakan Factory standar
// (Namun, kita buat factory kosong jika nanti perlu dependensi)
class PlaydateViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaydateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaydateViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}