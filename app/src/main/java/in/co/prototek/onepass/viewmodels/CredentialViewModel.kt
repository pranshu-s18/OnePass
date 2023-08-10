package `in`.co.prototek.onepass.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import `in`.co.prototek.onepass.data.Credential
import `in`.co.prototek.onepass.utils.readEncryptedFile
import `in`.co.prototek.onepass.utils.writeEncryptedFile

class CredentialViewModel : ViewModel() {
    // List of credentials
    private val _credentials = MutableLiveData<List<Credential>>()
    val credentials: LiveData<List<Credential>> = _credentials

    // Stores the credential that is selected for editing
    private val _selectedCredential = MutableLiveData<Credential?>()
    val selectedCredential: LiveData<Credential?> = _selectedCredential

    // Initialize the list of credentials
    init {
        _credentials.value = listOf()
    }

    // Read the encrypted file and initialize the list of credentials
    // This function is called from MainActivity
    fun initialize(context: Context) {
        // Decrypted data is in CSV format
        val data = readEncryptedFile(context)

        // Convert CSV to list of credentials
        val temp = mutableListOf<Credential>()
        data.lines().forEach { record ->
            val cred = record.split(',')
            if (record.isNotEmpty()) temp.add(Credential(cred[0], cred[1], cred[2]))
        }

        // Update the list of credentials (LiveData)
        _credentials.value = temp
    }

    // Utility function to add a credential, called from AddCredentialFragment
    // Add the credential to the list of credentials and write the updated list to the encrypted file
    // If something goes wrong, don't update the list of credentials
    fun addCredential(service: String, username: String, password: String, context: Context) {
        if (service.isBlank() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        // New credential
        val credential = Credential(service, username, password)

        // Add the new credential to the list of credentials
        val list = _credentials.value?.toMutableList()
        list?.add(credential)

        // Try to write the updated list to the encrypted file
        val res = writeEncryptedFile(context, list)

        // If something goes wrong, reset the list of credentials and return
        if (!res) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            list?.remove(credential)
            return
        }

        // Update the list of credentials (LiveData) if successful
        _credentials.value = list!!
    }

    // Utility function to edit a credential, called from EditCredentialFragment
    // Edit the credential in the list of credentials and write the updated list to the encrypted file
    // If something goes wrong, don't update the list of credentials
    fun editCredential(service: String, username: String, password: String, context: Context) {
        if (service.isBlank() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        // New credential
        val credential = Credential(service, username, password)

        // If the credential is not changed, don't update the list of credentials
        if (credential == selectedCredential.value!!) return

        // Get the index of the credential to be edited
        val list = _credentials.value?.toMutableList()!!
        val index = list.indexOf(selectedCredential.value!!)

        // To update the list of credentials, remove the old credential and add the new credential
        list.removeAt(index)
        list.add(index, credential)

        // Try to write the updated list to the encrypted file
        val res = writeEncryptedFile(context, list)

        // If something goes wrong, reset the list of credentials and return
        if (!res) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            list.removeAt(index)
            list.add(index, selectedCredential.value!!)
            return
        }

        // Update the list of credentials (LiveData) if successful
        _credentials.value = list
    }

    // Utility function to delete a credential, called from HomeFragment
    // Delete the credential from the list of credentials and write the updated list to the encrypted file
    // If something goes wrong, don't update the list of credentials
    fun deleteCredential(credential: Credential, context: Context) {
        // Remove the credential from the list of credentials
        val list = _credentials.value?.toMutableList()!!
        list.remove(credential)

        // Try to write the updated list to the encrypted file
        val res = writeEncryptedFile(context, list)

        // If something goes wrong, reset the list of credentials and return
        if (!res) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            list.add(credential)
            return
        }

        // Update the list of credentials (LiveData) if successful
        _credentials.value = list
    }

    // Utility function to set the selected credential, called from HomeFragment
    fun setSelectedCredential(credential: Credential) {
        _selectedCredential.value = credential
    }
}