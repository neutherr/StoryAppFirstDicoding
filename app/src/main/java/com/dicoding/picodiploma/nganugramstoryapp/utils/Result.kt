package com.dicoding.picodiploma.nganugramstoryapp.utils

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}