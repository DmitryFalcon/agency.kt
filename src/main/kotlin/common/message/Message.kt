package common.message

data class Message(
    val senderId: String,
    val receiverId: String,
    val data: Any? = null,
    val code: Int = EMPTY_MESSAGE_CODE,
    val timestamp: Long = System.currentTimeMillis()
) {

    companion object {
        const val EMPTY_MESSAGE_CODE = -1
    }
}