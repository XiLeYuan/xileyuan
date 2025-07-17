package com.xly.business.find.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout
import com.xly.business.find.model.Moment
import com.xly.R
import com.xly.business.find.view.MomentImageDetailActivity

class MomentAdapter(
    private val list: List<Moment>,
    private val activity: Activity
) : RecyclerView.Adapter<MomentAdapter.MomentViewHolder>() {

    class MomentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.avatar)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val content: TextView = itemView.findViewById(R.id.content)
        val imageContainer: FlexboxLayout = itemView.findViewById(R.id.imageContainer)
        val time: TextView = itemView.findViewById(R.id.time)
        val btnLike: ImageView = itemView.findViewById(R.id.btnLike)
        val btnComment: ImageView = itemView.findViewById(R.id.btnComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_moment, parent, false)
        return MomentViewHolder(view)
    }

    override fun onBindViewHolder(holder: MomentViewHolder, position: Int) {
        val moment = list[position]
        holder.userName.text = moment.userName
        holder.content.text = moment.content
        holder.time.text = moment.time
        Glide.with(holder.avatar).load(moment.userAvatar).into(holder.avatar)

        // 动态添加图片
        holder.imageContainer.removeAllViews()
        moment.images.forEachIndexed { idx, url ->
            val img = ImageView(holder.itemView.context)
            val size = holder.itemView.resources.displayMetrics.widthPixels / 4
            val lp = FlexboxLayout.LayoutParams(size, size)
            lp.setMargins(4, 4, 4, 4)
            img.layoutParams = lp
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            img.transitionName = "moment_image_${moment.id}_$idx"
            Glide.with(img).load(url).into(img)
            img.setOnClickListener {
                // 转场动画跳转
                val intent = Intent(holder.itemView.context, MomentImageDetailActivity::class.java)
                intent.putExtra("imageList", moment.images.toIntArray())
                intent.putExtra("index", idx)
                intent.putExtra("momentId", moment.id)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    Pair.create(img as View, img.transitionName)
                )
                activity.startActivity(intent, options.toBundle())
            }
            holder.imageContainer.addView(img)
        }
    }

    override fun getItemCount(): Int = list.size
}