package `in`.co.prototek.onepass.utils

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import `in`.co.prototek.onepass.R
import `in`.co.prototek.onepass.data.Credential
import java.io.File
import java.net.URLEncoder

fun String.urlEncode(): String = URLEncoder.encode(this, "UTF-8")

// https://developer.android.com/topic/security/data
fun getEncryptedFile(context: Context): EncryptedFile {
    val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    return EncryptedFile.Builder(
        context, store(context), masterKey,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
}

// Wrapper function to get file from storage
fun store(context: Context): File {
    return File(context.filesDir, context.getString(R.string.file_name).urlEncode())
}

// Utility function to clear all data (delete file)
fun clearAllData(context: Context) {
    val file = store(context)
    if (file.exists()) file.delete()
}

// Decrypt and read file
fun readEncryptedFile(context: Context): String {
    return if (store(context).exists()) {
        val encryptedFile = getEncryptedFile(context)
        encryptedFile.openFileInput()
            .use { input -> input.bufferedReader().use { it.readText() } }
    } else ""
}

// Updating an encrypted file is not supported
// WORKAROUND: Delete the file and create a new one
fun writeEncryptedFile(context: Context, credentials: List<Credential>?): Boolean {
    if (credentials == null) return false
    try {
        // Delete file and create new one
        clearAllData(context)

        // List of credentials to CSV
        val data = credentialsToCSV(credentials)

        // Create encrypted file and write data
        val encryptedFile = getEncryptedFile(context)
        encryptedFile.openFileOutput().use { output -> output.write(data.toByteArray()) }

        // Return true if successful
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}