package com.example.roadmaintenance.util

class Results(
    val status: Status,
    val message: String
) {

    enum class Status {
        SUCCESS,
        LOADING,
        UPLOAD_FILE_SUCCESS,
        UPLOAD_FILE_ERROR,
        OFFLINE,
        SERVER_ERROR,
        Access_Denied,
        User_Checked,
        First_Login,
        User_Verified,
        Success
    }
}