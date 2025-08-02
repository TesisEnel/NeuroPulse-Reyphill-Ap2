package io.github.reyx38.neuropulse.presentation.ejerciciosCognitivos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.reyx38.neuropulse.data.remote.Resource
import io.github.reyx38.neuropulse.data.repository.EjerciciosCognitivoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EjerciciosViewModel @Inject constructor(
    private val ejerciciosCognitivosRepository: EjerciciosCognitivoRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(EjerciciosCognitivosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getEjercicios()
    }

    private fun getEjercicios() {
      viewModelScope.launch {
          ejerciciosCognitivosRepository.sincronizarEjercicios().collect { resultado ->
              when(resultado) {
                  is Resource.Error ->  _uiState.update {
                      it.copy(
                          isLoading = false,
                          error = resultado.message ?: "Error desconocido"
                      )
                  }
                  is Resource.Loading -> _uiState.update {
                      it.copy(
                          isLoading = true,
                          error = null
                      )
                  }
                  is Resource.Success -> _uiState.update {
                      it.copy(
                          isLoading = false,
                          ejercicios = resultado.data ?: emptyList(),
                          error = null
                      )
                  }
              }
          }
      }
    }


}