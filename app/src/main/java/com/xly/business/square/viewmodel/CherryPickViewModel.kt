package com.xly.business.square.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xly.business.square.model.TodaySelectionUser
import com.xly.middlelibrary.utils.TodaySelectionMockData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CherryPickViewModel : ViewModel() {

    private val _selectionUsersLiveData = MutableLiveData<List<TodaySelectionUser>>()
    val selectionUsersLiveData: LiveData<List<TodaySelectionUser>> = _selectionUsersLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadTodaySelection()
    }

    fun loadTodaySelection() {
        _isLoading.value = true

        viewModelScope.launch {
            delay(1000) // 模拟网络延迟

            val data = TodaySelectionMockData.generateTodaySelection()
            _selectionUsersLiveData.value = data
            _isLoading.value = false
        }
    }

    fun refreshTodaySelection() {
        _isLoading.value = true

        viewModelScope.launch {
            delay(800)

            val data = TodaySelectionMockData.generateTodaySelection()
            _selectionUsersLiveData.value = data
            _isLoading.value = false
        }
    }

    fun likeUser(userId: String) {
        // 处理喜欢逻辑
        viewModelScope.launch {
            // 调用 API 或更新本地数据
            delay(300)
        }
    }
}