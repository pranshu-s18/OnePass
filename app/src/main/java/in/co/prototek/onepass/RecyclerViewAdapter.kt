package `in`.co.prototek.onepass

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import `in`.co.prototek.onepass.data.Credential
import `in`.co.prototek.onepass.databinding.ItemViewBinding
import `in`.co.prototek.onepass.utils.copyToClipboard
import `in`.co.prototek.onepass.utils.handleHttps

class RecyclerViewAdapter(
    private val records: List<Credential>,
    private val functions: RecyclerViewOperations
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    // Interface to access operations inaccessible from RecyclerViewAdapter
    // Ex: Navigation, ViewModel operations
    interface RecyclerViewOperations {
        fun onClick(credential: Credential)
        fun deleteCredential(credential: Credential)
    }

    inner class ViewHolder(val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val context = holder.itemView.context
            with(records[position]) {
                // On click, update selectedCredential (viewModel) and navigate to EditCredentialFragment
                holder.itemView.setOnClickListener {
                    functions.onClick(this)
                }

                binding.service.text = service
                binding.username.text = username

                // Popup menu for options
                binding.options.setOnClickListener {
                    // Show menu at binding.options
                    val popupMenu = PopupMenu(context, binding.options)
                    popupMenu.inflate(R.menu.credential_options)

                    // Handle menu item clicks
                    popupMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            // Open URL in browser (if possible)
                            R.id.menu_open_url -> {
                                try {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(handleHttps(service))
                                    )

                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    e.printStackTrace()
                                    Toast.makeText(
                                        context,
                                        "No application can handle this request. Please install a web browser or check your URL.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                true
                            }

                            // Copy username to clipboard
                            R.id.menu_copy_username -> {
                                copyToClipboard(context, username)
                                true
                            }

                            // Copy password to clipboard
                            R.id.menu_copy_password -> {
                                copyToClipboard(context, password)
                                true
                            }

                            // Delete credential (handled in viewModel)
                            R.id.menu_delete_credential -> {
                                functions.deleteCredential(this)
                                true
                            }

                            // Invalid option
                            else -> false
                        }
                    }

                    popupMenu.show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return records.size
    }
}