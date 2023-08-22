package com.example.traintrack

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traintrack.SharedPreferencesManager.loadLine
import com.example.traintrack.SharedPreferencesManager.loadStop
import com.example.traintrack.data.lineToStations
import com.example.traintrack.data.listOfLines
import com.example.traintrack.data.stationToCode
import com.example.traintrack.model.Line
import com.example.traintrack.model.Metadata
import com.example.traintrack.model.NextService
import kotlinx.coroutines.launch

class TripViewModel(application: Application) : AndroidViewModel(application) {
    // --------------------------------- MAIN SCREEN RELATED -----------------------------

    // trip repository that holds instance of retrofit
    private val repository = TripRepository()

    // mutable live data of line information
    private val _lines = MutableLiveData<List<Line>>()
    val lines: LiveData<List<Line>> = _lines

    // mutable live data of metadata information (time, errors)
    private val _metadata = MutableLiveData<Metadata>()
    val metadata: LiveData<Metadata> = _metadata

    // mutable live data of next service (needed for case when no lines are running)
    private val _nextService = MutableLiveData<NextService>()
    val nextService: LiveData<NextService> = _nextService

    // mutable live data to indicate if loading has finished or not
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // fetching line info and time
    fun fetchTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getTripsCustom(stationToCode[loadStop(getApplication<Application>().applicationContext)]!!)
                val lines = response.NextService.Lines
                _lines.value = lines

                val metadata = response.Metadata
                _metadata.value = metadata

                val nextService = response.NextService
                _nextService.value = nextService

            } catch (e: Exception) {
                Log.e(e.message, "error getting trips")
            } finally {
                _isLoading.value = false
            }
        }
    }
    // ------------------------- SETTINGS RELATED ---------------------------
    private val lineOptions = listOfLines

    private val _selectedLineText = MutableLiveData(loadLine(getApplication<Application>().applicationContext) ?: lineOptions[0])
    val selectedLineText: LiveData<String> = _selectedLineText

    private val _selectedStopText = MutableLiveData(loadStop(getApplication<Application>().applicationContext) ?: "")
    val selectedStopText: LiveData<String> = _selectedStopText

    fun onLineSelected(line: String) {
        _selectedLineText.value = line
        _selectedStopText.value = "" // clear value so user can select
    }

    fun onStopSelected(stop: String) {
        _selectedStopText.value = stop
    }


}