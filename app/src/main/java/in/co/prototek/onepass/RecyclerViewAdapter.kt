package `in`.co.prototek.onepass

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val records: ArrayList<Credential>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cur = records[position]
        val context = holder.itemView.context
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        holder.service.text = cur.service
        holder.userName.text = context.getString(R.string.username, cur.username)
        holder.userName.setOnClickListener {
            val clip = ClipData.newPlainText("username", cur.username)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Username copied to Clipboard", Toast.LENGTH_SHORT).show()
        }

        holder.password.text = context.getString(R.string.password, "*".repeat(cur.password.length))
        holder.password.setOnClickListener {
            val clip = ClipData.newPlainText("password", cur.password)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Password copied to Clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return records.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val service: TextView = itemView.findViewById(R.id.service)
        val userName: TextView = itemView.findViewById(R.id.username)
        val password: TextView = itemView.findViewById(R.id.password)
    }
}