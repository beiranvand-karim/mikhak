package com.example.roadmaintenance.util

abstract class ResultsCreator {
    abstract fun resultFactory(): Results
}

object OfflineResultsCreator : ResultsCreator() {
    override fun resultFactory(): Results {
        return Results(
            Results.Status.OFFLINE,
            "You are not connected to internet"
        )
    }
}

object UploadFileSuccessCreator : ResultsCreator() {
    override fun resultFactory(): Results {
        return Results(
            Results.Status.UPLOAD_FILE_SUCCESS,
            "File uploaded"
        )
    }
}

object UploadFileErrorCreator : ResultsCreator() {
    override fun resultFactory(): Results {
        return Results(
            Results.Status.UPLOAD_FILE_ERROR,
            "File not uploaded"
        )
    }
}

open class ServerErrorResultsCreator(val message: String) : ResultsCreator() {
    override fun resultFactory(): Results {
        return Results(
            Results.Status.SERVER_ERROR,
            "Something went wrong with the servers $message"
        )
    }
}

object SuccessResultsCreator : ResultsCreator() {
    override fun resultFactory(): Results {
        return Results(
            Results.Status.SUCCESS,
            "Data successfully fetched"
        )
    }
}

object LoadingResultsCreator : ResultsCreator() {
    override fun resultFactory(): Results {
        return Results(
            Results.Status.LOADING,
            "Loading..."
        )
    }
}


