package com.example.animaly.activities.util

object UserSession{

    init {
        println("UserSession invoked.")
    }
    var id = ""
    var nom = ""
    var prenom = ""
    var image = "default-profile.png"
    var email = ""

    fun fullName ():String{
        return "$prenom $nom"
    }

    fun reset()
    {
         id = ""
         nom = ""
         prenom = ""
         image = "default-profile.png"
         email = ""

    }

}