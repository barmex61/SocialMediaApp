package com.fatih.instagramapp.model

import java.io.Serializable

data class Users(

    val userName:String?=null,
    val searchUserName:String?=userName?.lowercase(),
    val password:String?=null,
    val date:Long?=null,
    val email:String?=null,
    val name:String?=null,
    val searchName:String?=name?.lowercase(),
    val phone:String?=null,
    val website:String?=null,
    val photo:String?=null,
    val uid:String?=null,
    val status:String?=null,
    val bio:String?=null

):Serializable
