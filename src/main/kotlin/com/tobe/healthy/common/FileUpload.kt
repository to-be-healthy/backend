package com.tobe.healthy.common

enum class FileUpload(
    val description: Int
) {
    FILE_MAXIMUM_UPLOAD_SIZE(3),
    FILE_TEMP_UPLOAD_TIMEOUT(30 * 60 * 1000)
}