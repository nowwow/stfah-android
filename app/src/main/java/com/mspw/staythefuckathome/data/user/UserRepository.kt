package com.mspw.staythefuckathome.data.user

import com.mspw.staythefuckathome.data.ListResponse
import retrofit2.Call

class UserRepository(private val userService: UserService) {

    fun getUserExist(id: String): Call<ListResponse<User>> {
        return userService.getUserList(id)
    }

    fun registerUser(firebaseToken: String, userName: String, userProfile: String):Call<Any>{
        return userService.registerUser(firebaseToken,SignUp(userName, userProfile))
    }

    fun getUserData(firebaseToken: String):Call<User>{
        return userService.getMyUser(firebaseToken)
    }
}