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
import com.google.android.material.imageview.ShapeableImageView
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
        // 在 MomentAdapter 的 onBindViewHolder 中
        moment.images.forEachIndexed { idx, imageResId ->
            val img = ShapeableImageView(holder.itemView.context)
            val size = holder.itemView.resources.displayMetrics.widthPixels / 4
            val lp = FlexboxLayout.LayoutParams(size, size)
            lp.setMargins(4, 4, 4, 4)
            img.layoutParams = lp
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            img.transitionName = "moment_image_${moment.id}_$idx"

            img.shapeAppearanceModel = img.shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(18f) // 16f px, 也可以用 TypedValue.applyDimension
                .build()
            Glide.with(img).load(imageResId).into(img)
            img.setOnClickListener {
                // 只传递单张图片
                val intent = Intent(holder.itemView.context, MomentImageDetailActivity::class.java)
                intent.putExtra("imageResId", imageResId) // 只传一张图片的 res id
                intent.putExtra("momentId", moment.id)
                intent.putExtra("imageIndex", idx)

                // 创建共享元素动画
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