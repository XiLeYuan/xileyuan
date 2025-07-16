import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.xly.business.recommend.model.Person
import com.xly.R
import com.xly.middlelibrary.utils.LYUtils.blurBitmap
import com.xly.middlelibrary.utils.LYUtils.createColorBitmap
import com.xly.middlelibrary.utils.LYUtils.getRandomColor
import com.xly.middlelibrary.widget.LYRoundImageView

class CardAdapter(private val list: List<Person>) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemLayout: ConstraintLayout = itemView.findViewById(R.id.itemLayout)
        val avatar: LYRoundImageView = itemView.findViewById(R.id.avatar)
        val topBgImg: ImageView = itemView.findViewById(R.id.topBgImg)
        /*val name: TextView = itemView.findViewById(R.id.name)
        val age: TextView = itemView.findViewById(R.id.age)
        val desc: TextView = itemView.findViewById(R.id.desc)*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_recommend_item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val person = list[position]
        holder.avatar.setImageResource(person.avatarRes)
        if (position == 0 || position == 2 || position == 4) {
            holder.topBgImg.setImageResource(R.mipmap.stylemax_11)
        } else {
            holder.topBgImg.setImageResource(R.mipmap.card_bg)
        }
        /*holder.name.text = person.name
        holder.age.text = "${person.age}岁"
        holder.desc.text = person.desc*/

        // 随机颜色并缓存
        /*val color = getRandomColor()
        val width = holder.itemView.width.takeIf { it > 0 } ?: 600
        val height = holder.itemView.height.takeIf { it > 0 } ?: 400
        val bmp = createColorBitmap(color, width, height)
        val blurred = blurBitmap(holder.itemView.context, bmp)
        holder.itemLayout.background = BitmapDrawable(holder.itemLayout.resources, blurred)*/

    }

    override fun getItemCount(): Int = list.size
}