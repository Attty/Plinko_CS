package com.example.plinkocs.presentation.menu.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plinkocs.presentation.menu.state.MenuViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val _viewState = mutableStateOf(MenuViewState())
    var viewState: State<MenuViewState> = _viewState

    init {
        viewModelScope.launch {
            delay(5000L)
            _viewState.value = _viewState.value.copy(
                isLoading = false
            )
        }
    }
}