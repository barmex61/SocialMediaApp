package com.fatih.instagramapp.util

class Resource <T>(val status:Status, val message:String?, var data:T?) {

    companion object{

        const val GOOGLE_SIGN_IN=100

        fun <T> success(data:T):Resource<T>{
            return Resource(Status.SUCCESS,null,data)
        }
        fun <T> loading(data:T?):Resource<T>{
            return Resource(Status.LOADING,"Loading..",data)
        }
        fun <T> error(data:T?,message: String?):Resource<T>{
            return Resource(Status.ERROR,message,data)
        }

    }
}