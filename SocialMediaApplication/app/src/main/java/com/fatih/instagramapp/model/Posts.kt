package com.fatih.instagramapp.model

data class Posts(

    var photo:String?=null,
    val date:Long?=null,
    val comment:String?=null,
    val likes:Int?=0,
    val commentCount:Int?=0,
    var postId:String?="",
    val publisher:String?="",
    val title:String?=null

)
