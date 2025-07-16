import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xly.R

data class Person(val name: String, val desc: String, val avatarRes: Int)

class CardAdapter(private val list: List<Person>) :
    RecyclerView.Adapter<CardAdapter.PersonViewHolder>() {

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.avatar)
        val name: TextView = itemView.findViewById(R.id.name)
        val desc: TextView = itemView.findViewById(R.id.desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_recommend_item_card, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = list[position]
        holder.avatar.setImageResource(person.avatarRes)
        holder.name.text = person.name
        holder.desc.text = person.desc
    }

    override fun getItemCount(): Int = list.size
}