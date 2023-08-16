package com.example.aqisample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aqisample.data.AqiData
import com.example.aqisample.data.AqiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AqiRepository,
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Success : UiState()
        object Fail : UiState()
    }

    data class SearchInfo(val filter: String, val isEnable: Boolean, val isEmptyResult: Boolean)

    private val _aqiFlow = MutableStateFlow<List<AqiData>>(emptyList())
    private val _filterString = MutableStateFlow("")
    private val _searchEnable = MutableStateFlow(false)

    val filterInfo: Flow<SearchInfo> =
        combine(_filterString, _searchEnable, _aqiFlow) { filterString, searchEnable, aqiList ->
            SearchInfo(
                filterString,
                searchEnable,
                !aqiList.any { it.siteName.contains(filterString) })
        }

    val aqiDetailFlow =
        combine(_aqiFlow, _filterString, _searchEnable) { list, filterString, searchEnable ->
            list.filter {
                if (!searchEnable) {
                    it.pm25 > PM25_THRESHOLD
                } else {
                    filterString.isNotEmpty() && it.siteName.contains(filterString)
                }
            }
        }
    val aqiCardFlow = combine(_aqiFlow, _searchEnable) { list, searchEnable ->
        list.filter { !searchEnable && it.pm25 <= PM25_THRESHOLD }
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: Flow<UiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)
            try {
                delay(300) //simulate
                val result = repository.getAqiList()
//                println(result)
                _aqiFlow.emit(result)
                _uiState.emit(UiState.Success)
            } catch (_: Exception) {
                _uiState.emit(UiState.Fail)
            }
        }
    }

    fun setFilterString(filter: String) {
        viewModelScope.launch {
            _filterString.emit(filter)
        }
    }

    fun setSearchEnable(enable: Boolean) {
        viewModelScope.launch {
            _searchEnable.emit(enable)
        }
    }

    fun shouldOpenSearchView(): Boolean {
        return _searchEnable.value
    }

    companion object {
        const val PM25_THRESHOLD = 10
    }
}