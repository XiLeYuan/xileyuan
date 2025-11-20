package com.xly.business.login.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xly.R
import com.xly.databinding.DialogBottomPickerBinding

class BottomPickerDialog(
    context: Context,
    private val title: String,
    private val options: List<String>,
    private val onItemSelected: (String) -> Unit
) : BottomSheetDialog(context) {
    
    private lateinit var binding: DialogBottomPickerBinding
    private var selectedPosition = -1

    init {
        binding = DialogBottomPickerBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.tvTitle.text = title
        val adapter = PickerAdapter(options, selectedPosition) { position ->
            selectedPosition = position
        }
        binding.rvOptions.layoutManager = LinearLayoutManager(context)
        binding.rvOptions.adapter = adapter

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvConfirm.setOnClickListener {
            if (selectedPosition >= 0 && selectedPosition < options.size) {
                onItemSelected(options[selectedPosition])
                dismiss()
            }
        }
    }
    
    fun setSelectedItem(selectedItem: String?) {
        selectedPosition = options.indexOf(selectedItem ?: "")
    }

    private class PickerAdapter(
        private val options: List<String>,
        private var selectedPosition: Int,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<PickerAdapter.ViewHolder>() {

        fun updateSelectedPosition(position: Int) {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_picker_option, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val isSelected = position == selectedPosition
            holder.bind(options[position], isSelected)
            holder.itemView.setOnClickListener {
                updateSelectedPosition(position)
                onItemClick(position)
            }
        }

        override fun getItemCount() = options.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView as TextView

            fun bind(option: String, isSelected: Boolean) {
                textView.text = option
                textView.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        if (isSelected) R.color.brand_primary else R.color.text_primary
                    )
                )
            }
        }
    }
}

