package org.csu.phdata.common

import lombok.Getter


@Getter
enum class ResponseCode(val code: Int, val description: String) {
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    ARGUMENT_ILLEGAL(10, "ARGUMENT_ILLEGAL"),
    NEED_LOGIN(11, "NEED_LOGIN"),
    FILE_SERVICE_ERROR(20, "FILE_SERVICE_ERROR"),
    FILE_NOT_EXISTS(21, "FILE_NOT_EXISTS"),
    FILE_NO_PERMISSION(22, "FILE_NO_PERMISSION");
}
