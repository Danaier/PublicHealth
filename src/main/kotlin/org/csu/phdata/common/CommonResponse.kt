package org.csu.phdata.common


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable


@JsonInclude(JsonInclude.Include.NON_NULL)
data class CommonResponse<T> (
    val code: Int,
    val message: String,
    val data: T
) : Serializable {
    /**
     * 用于判断请求是否为成功请求
     */
    @get:JsonIgnore
    val isSuccess: Boolean get() = this.code == success.code

    companion object {

        private val success = ResponseCode.SUCCESS
        private val error = ResponseCode.ERROR

        /**
         * 请求成功，无数据返回
         */
        fun <T> createForSuccess() = CommonResponse(success.code, success.description, null)


        /**
         * 请求成功，并返回响应数据
         */
        fun <T> createForSuccess(data: T) = CommonResponse(success.code, success.description, data)


        /**
         * 请求错误，默认错误信息
         */
        fun <T> createForError() = CommonResponse(error.code, error.description, null)


        /**
         * 请求错误，指定错误信息
         */
        fun <T> createForError(errorMessage: String) = CommonResponse(error.code, errorMessage, null)


        /**
         * 请求错误，指定错误码和错误信息
         */
        fun <T> createForError(code: Int, errorMessage: String) = CommonResponse(code, errorMessage, null)

    }
}


