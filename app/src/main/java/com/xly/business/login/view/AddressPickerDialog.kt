package com.xly.business.login.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xly.databinding.DialogAddressPickerBinding
import org.json.JSONArray
import java.io.InputStream

class AddressPickerDialog(
    context: Context,
    private val addressJson: JSONArray,
    private val onResult: (province: String, city: String, district: String) -> Unit
) : BottomSheetDialog(context) {
    private lateinit var binding: DialogAddressPickerBinding
    private var provinceList = listOf<String>()
    private var cityList = listOf<String>()
    private var districtList = listOf<String>()
    private var currentProvince = 0
    private var currentCity = 0
    private var currentDistrict = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddressPickerBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        initPickers()
        binding.tvCancel.setOnClickListener { dismiss() }
        binding.tvConfirm.setOnClickListener {
            val province = provinceList.getOrNull(currentProvince) ?: ""
            val city = cityList.getOrNull(currentCity) ?: ""
            val district = districtList.getOrNull(currentDistrict) ?: ""
            onResult(province, city, district)
            dismiss()
        }
    }

    private fun initPickers() {
        provinceList = (0 until addressJson.length()).map { addressJson.getJSONObject(it).getString("name") }
        binding.npProvince.displayedValues = null
        binding.npProvince.minValue = 0
        binding.npProvince.maxValue = provinceList.size - 1
        binding.npProvince.displayedValues = provinceList.toTypedArray()
        binding.npProvince.setOnValueChangedListener { _, _, newVal ->
            currentProvince = newVal
            updateCityPicker()
        }
        updateCityPicker()
    }

    private fun updateCityPicker() {
        val provinceObj = addressJson.getJSONObject(currentProvince)
        val cities = provinceObj.optJSONArray("cities") ?: JSONArray()
        cityList = (0 until cities.length()).map { cities.getJSONObject(it).getString("name") }
        binding.npCity.displayedValues = null
        binding.npCity.minValue = 0
        binding.npCity.maxValue = if (cityList.isNotEmpty()) cityList.size - 1 else 0
        binding.npCity.displayedValues = if (cityList.isNotEmpty()) cityList.toTypedArray() else arrayOf("")
        binding.npCity.setOnValueChangedListener { _, _, newVal ->
            currentCity = newVal
            updateDistrictPicker()
        }
        currentCity = 0
        updateDistrictPicker()
    }

    private fun updateDistrictPicker() {
        val provinceObj = addressJson.getJSONObject(currentProvince)
        val cities = provinceObj.optJSONArray("cities") ?: JSONArray()
        val cityObj = if (cities.length() > 0) cities.getJSONObject(currentCity) else null
        val districts = cityObj?.optJSONArray("district") ?: JSONArray()
        districtList = (0 until districts.length()).map { districts.getJSONObject(it).getString("name") }
        binding.npDistrict.displayedValues = null
        binding.npDistrict.minValue = 0
        binding.npDistrict.maxValue = if (districtList.isNotEmpty()) districtList.size - 1 else 0
        binding.npDistrict.displayedValues = if (districtList.isNotEmpty()) districtList.toTypedArray() else arrayOf("")
        binding.npDistrict.setOnValueChangedListener { _, _, newVal ->
            currentDistrict = newVal
        }
        currentDistrict = 0
    }

    companion object {
        fun loadAddressJson(context: Context): JSONArray {
            val input: InputStream = context.assets.open("address.json")
            val json = input.bufferedReader().use { it.readText() }
            return JSONArray(json)
        }
    }
} 