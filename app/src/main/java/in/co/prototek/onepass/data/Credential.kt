package `in`.co.prototek.onepass.data

data class Credential(val service: String, val username: String, val password: String) {
    override fun toString(): String {
        return "$service,$username,$password"
    }
}
