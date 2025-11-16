package com.xly.business.recommend.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xly.R
import com.xly.base.LYBaseActivity
import com.xly.databinding.ActivityFilterBinding
import com.xly.middlelibrary.utils.click

class FilterActivity : LYBaseActivity<ActivityFilterBinding, FilterActivity.FilterViewModel>() {

    private var currentEducation: String = "不限"
    private var currentIncome: String = "不限"

    companion object {
        const val EXTRA_FILTER_OPTIONS = "filter_options"
        
        fun start(context: Context) {
            val intent = Intent(context, FilterActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_up_fade_in, android.R.anim.fade_out)
        setupViews()
        setupClickListeners()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_down_fade_out)
    }

    private fun setupViews() {
        // 设置关闭按钮点击效果
        viewBind.ivClose.click {
            finish()
        }

        // 设置学历标签点击
        setupEducationTags()
        
        // 设置收入标签点击
        setupIncomeTags()
    }

    private fun setupEducationTags() {
        val educationTags = listOf(
            viewBind.tvEducation1 to "不限",
            viewBind.tvEducation2 to "高中",
            viewBind.tvEducation3 to "大专",
            viewBind.tvEducation4 to "本科",
            viewBind.tvEducation5 to "硕士",
            viewBind.tvEducation6 to "博士"
        )

        educationTags.forEach { (view, value) ->
            view.click {
                selectEducationTag(view, educationTags.map { it.first })
                currentEducation = value
            }
        }
    }

    private fun setupIncomeTags() {
        val incomeTags = listOf(
            viewBind.tvIncome1 to "不限",
            viewBind.tvIncome2 to "5k以下",
            viewBind.tvIncome3 to "5k-10k",
            viewBind.tvIncome4 to "10k-20k",
            viewBind.tvIncome5 to "20k-50k",
            viewBind.tvIncome6 to "50k以上"
        )

        incomeTags.forEach { (view, value) ->
            view.click {
                selectIncomeTag(view, incomeTags.map { it.first })
                currentIncome = value
            }
        }
    }

    private fun selectEducationTag(selectedView: TextView, allViews: List<TextView>) {
        allViews.forEach { view ->
            if (view == selectedView) {
                view.background = ContextCompat.getDrawable(this, R.drawable.bg_filter_tag_selected)
                view.setTextColor(ContextCompat.getColor(this, R.color.text_white))
            } else {
                view.background = ContextCompat.getDrawable(this, R.drawable.bg_filter_tag)
                view.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }
        }
    }

    private fun selectIncomeTag(selectedView: TextView, allViews: List<TextView>) {
        allViews.forEach { view ->
            if (view == selectedView) {
                view.background = ContextCompat.getDrawable(this, R.drawable.bg_filter_tag_selected)
                view.setTextColor(ContextCompat.getColor(this, R.color.text_white))
            } else {
                view.background = ContextCompat.getDrawable(this, R.drawable.bg_filter_tag)
                view.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }
        }
    }

    private fun setupClickListeners() {
        viewBind.btnConfirm.click {
            val options = FilterOptions(
                ageMin = viewBind.tvAgeMin.text.toString().toIntOrNull() ?: 18,
                ageMax = viewBind.tvAgeMax.text.toString().toIntOrNull() ?: 50,
                heightMin = viewBind.tvHeightMin.text.toString().toIntOrNull() ?: 150,
                heightMax = viewBind.tvHeightMax.text.toString().toIntOrNull() ?: 200,
                education = currentEducation,
                income = currentIncome
            )
            
            val resultIntent = Intent().apply {
                putExtra("ageMin", options.ageMin)
                putExtra("ageMax", options.ageMax)
                putExtra("heightMin", options.heightMin)
                putExtra("heightMax", options.heightMax)
                putExtra("education", options.education)
                putExtra("income", options.income)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityFilterBinding {
        return ActivityFilterBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): FilterViewModel {
        return ViewModelProvider(this)[FilterViewModel::class.java]
    }

    data class FilterOptions(
        val ageMin: Int = 18,
        val ageMax: Int = 50,
        val heightMin: Int = 150,
        val heightMax: Int = 200,
        val education: String = "不限",
        val income: String = "不限"
    )

    class FilterViewModel : ViewModel()
}

