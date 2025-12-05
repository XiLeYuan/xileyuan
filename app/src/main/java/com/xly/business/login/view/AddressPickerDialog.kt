package com.xly.business.login.view

import android.content.Context
import android.graphics.Outline
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xly.R
import com.xly.databinding.DialogAddressPickerBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

data class AddressItem(
    val id: String,
    val name: String,
    val indexTitle: String? = null
)

data class Province(
    val id: String,
    val name: String,
    val cities: List<City>
)

data class City(
    val id: String,
    val name: String,
    val districts: List<District>
)

data class District(
    val id: String,
    val name: String
)

class AddressPickerDialog(
    context: Context,
    private val title: String,
    private val onResult: (province: String, city: String, district: String) -> Unit
) : BottomSheetDialog(context) {

    private lateinit var binding: DialogAddressPickerBinding
    private var provinces: List<Province> = emptyList()
    private var cities: List<City> = emptyList()
    private var districts: List<District> = emptyList()

    private var selectedProvinceIndex = -1
    private var selectedCityIndex = -1
    private var selectedDistrictIndex = -1

    private lateinit var provinceAdapter: AddressAdapter
    private lateinit var cityAdapter: AddressAdapter
    private lateinit var districtAdapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddressPickerBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        loadAddressData()
        setupViews()
    }

    override fun onStart() {
        super.onStart()
        // 设置底部弹窗的圆角
        val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            it.background = null
            it.setBackgroundDrawable(null)
            
            // 设置弹窗高度为屏幕的一半
            val displayMetrics = context.resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels
            val dialogHeight = screenHeight / 2
            
            val layoutParams = it.layoutParams
            layoutParams?.height = dialogHeight
            it.layoutParams = layoutParams
            
            // 设置BottomSheetBehavior
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false  // 禁用拖拽功能
            behavior.peekHeight = dialogHeight
            
            it.post {
                val cornerRadius = 24 * context.resources.displayMetrics.density
                it.clipToOutline = true
                it.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        if (view.width > 0 && view.height > 0) {
                            outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
                        }
                    }
                }
                
                // 确保RecyclerView有正确的高度和可以滑动
                binding.rvProvince.post {
                    binding.rvProvince.requestLayout()
                }
                binding.rvCity.post {
                    binding.rvCity.requestLayout()
                }
                binding.rvDistrict.post {
                    binding.rvDistrict.requestLayout()
                }
            }
        }
        // 设置窗口背景为透明
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun loadAddressData() {
        try {
            val input: InputStream = context.assets.open("address.json")
            val json = input.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)
            
            provinces = (0 until jsonArray.length()).map { index ->
                val provinceObj = jsonArray.getJSONObject(index)
                val provinceId = provinceObj.optString("id", "")
                val provinceName = provinceObj.optString("name", "")
                val citiesArray = provinceObj.optJSONArray("cities") ?: JSONArray()
                
                val citiesList = (0 until citiesArray.length()).map { cityIndex ->
                    val cityObj = citiesArray.getJSONObject(cityIndex)
                    val cityId = cityObj.optString("id", "")
                    val cityName = cityObj.optString("name", "")
                    val districtsArray = cityObj.optJSONArray("district") ?: JSONArray()
                    
                    val districtsList = (0 until districtsArray.length()).map { districtIndex ->
                        val districtObj = districtsArray.getJSONObject(districtIndex)
                        District(
                            id = districtObj.optString("id", ""),
                            name = districtObj.optString("name", "")
                        )
                    }
                    
                    City(
                        id = cityId,
                        name = cityName,
                        districts = districtsList
                    )
                }
                
                Province(
                    id = provinceId,
                    name = provinceName,
                    cities = citiesList
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupViews() {
        binding.tvTitle.text = title

        // 设置省份列表
        setupProvinceList()

        // 设置城市列表（初始为空）
        setupCityList(emptyList())

        // 设置区县列表（初始为空）
        setupDistrictList(emptyList())

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvConfirm.setOnClickListener {
            if (selectedProvinceIndex >= 0 && selectedCityIndex >= 0 && selectedDistrictIndex >= 0) {
                val province = provinces[selectedProvinceIndex].name
                val city = cities[selectedCityIndex].name
                val district = districts[selectedDistrictIndex].name
            onResult(province, city, district)
            dismiss()
            }
        }
    }

    private fun setupProvinceList() {
        val provinceNames = provinces.map { it.name }
        provinceAdapter = AddressAdapter(provinceNames) { position ->
            selectedProvinceIndex = position
            // 更新城市列表
            if (position >= 0 && position < provinces.size) {
                val selectedProvince = provinces[position]
                cities = selectedProvince.cities
                setupCityList(cities.map { it.name })
                // 重置城市和区县选择
                selectedCityIndex = -1
                selectedDistrictIndex = -1
                districts = emptyList()
                setupDistrictList(emptyList())
            }
            // 滚动到选中位置
            scrollToPosition(binding.rvProvince, position)
        }
        val provinceLayoutManager = LinearLayoutManager(context)
        binding.rvProvince.layoutManager = provinceLayoutManager
        binding.rvProvince.adapter = provinceAdapter
        binding.rvProvince.isNestedScrollingEnabled = true
        binding.rvProvince.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    private fun setupCityList(cityNames: List<String>) {
        cityAdapter = AddressAdapter(cityNames) { position ->
            selectedCityIndex = position
            // 更新区县列表
            if (position >= 0 && position < cities.size) {
                val selectedCity = cities[position]
                districts = selectedCity.districts
                setupDistrictList(districts.map { it.name })
                // 重置区县选择
                selectedDistrictIndex = -1
            }
            // 滚动到选中位置
            scrollToPosition(binding.rvCity, position)
        }
        val cityLayoutManager = LinearLayoutManager(context)
        binding.rvCity.layoutManager = cityLayoutManager
        binding.rvCity.adapter = cityAdapter
        binding.rvCity.isNestedScrollingEnabled = true
        binding.rvCity.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        // 确保RecyclerView可以滑动
        binding.rvCity.post {
            binding.rvCity.requestLayout()
        }
    }

    private fun setupDistrictList(districtNames: List<String>) {
        districtAdapter = AddressAdapter(districtNames) { position ->
            selectedDistrictIndex = position
            // 滚动到选中位置
            scrollToPosition(binding.rvDistrict, position)
        }
        val districtLayoutManager = LinearLayoutManager(context)
        binding.rvDistrict.layoutManager = districtLayoutManager
        binding.rvDistrict.adapter = districtAdapter
        binding.rvDistrict.isNestedScrollingEnabled = true
        binding.rvDistrict.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        // 确保RecyclerView可以滑动
        binding.rvDistrict.post {
            binding.rvDistrict.requestLayout()
        }
    }

    private fun scrollToPosition(recyclerView: RecyclerView, position: Int) {
        if (position >= 0) {
            recyclerView.post {
                (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 0)
            }
        }
    }

    /**
     * 设置已选中的地址（用于回显）
     */
    fun setSelectedAddress(province: String?, city: String?, district: String?) {
        // 查找省份
        province?.let {
            val provinceIndex = provinces.indexOfFirst { p -> p.name == it }
            if (provinceIndex >= 0) {
                selectedProvinceIndex = provinceIndex
                provinceAdapter.setSelectedPosition(provinceIndex)
                
                // 更新城市列表
                val selectedProvince = provinces[provinceIndex]
                cities = selectedProvince.cities
                setupCityList(cities.map { c -> c.name })
                
                // 查找城市
                city?.let { cityName ->
                    val cityIndex = cities.indexOfFirst { c -> c.name == cityName }
                    if (cityIndex >= 0) {
                        selectedCityIndex = cityIndex
                        cityAdapter.setSelectedPosition(cityIndex)
                        scrollToPosition(binding.rvCity, cityIndex)
                        
                        // 更新区县列表
                        val selectedCity = cities[cityIndex]
                        districts = selectedCity.districts
                        setupDistrictList(districts.map { d -> d.name })
                        
                        // 查找区县
                        district?.let { districtName ->
                            val districtIndex = districts.indexOfFirst { d -> d.name == districtName }
                            if (districtIndex >= 0) {
                                selectedDistrictIndex = districtIndex
                                districtAdapter.setSelectedPosition(districtIndex)
                                scrollToPosition(binding.rvDistrict, districtIndex)
                            }
                        }
                    }
                }
                scrollToPosition(binding.rvProvince, provinceIndex)
            }
        }
    }

    private class AddressAdapter(
        private val items: List<String>,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

        private var selectedPosition = -1

        fun setSelectedPosition(position: Int) {
            val oldPosition = selectedPosition
            selectedPosition = position
            if (oldPosition >= 0) notifyItemChanged(oldPosition)
            if (position >= 0) notifyItemChanged(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picker_option, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val isSelected = position == selectedPosition
            holder.bind(items[position], isSelected)
            holder.itemView.setOnClickListener {
                setSelectedPosition(position)
                onItemClick(position)
            }
        }

        override fun getItemCount() = items.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView as TextView

            fun bind(item: String, isSelected: Boolean) {
                textView.text = item
                textView.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        if (isSelected) R.color.brand_primary else R.color.text_primary
                    )
                )
                textView.textSize = if (isSelected) 16f else 15f
            }
        }
    }
}
