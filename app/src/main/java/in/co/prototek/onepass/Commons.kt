package `in`.co.prototek.onepass

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import java.net.URLEncoder

fun String.urlEncode(): String = URLEncoder.encode(this, "UTF-8")
fun getEncryptedFile(context: Context): EncryptedFile {
    val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    return EncryptedFile.Builder(
        context, store(context), masterKey,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
    ).build()
}

fun store(context: Context): File {
    return File(context.filesDir, context.getString(R.string.file_name).urlEncode())
}

fun resetFile(context: Context) {
    val file = store(context)
    if (file.exists()) file.delete()
}

fun readEncryptedFile(context: Context): String {
    return if (store(context).exists()) {
        val encryptedFile = getEncryptedFile(context)
        encryptedFile.openFileInput()
            .use { input -> input.bufferedReader().use { it.readText() } }
    } else ""
}