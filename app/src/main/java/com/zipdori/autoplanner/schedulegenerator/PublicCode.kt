package com.zipdori.autoplanner.schedulegenerator

class Flags{
    companion object{
        //권한 플래그값 정의
        const val FLAG_PERM_CAMERA = 98
        const val FLAG_PERM_STORAGE_FOR_CAMERA = 99
        const val FLAG_PERM_STORAGE = 100

        //카메라와 갤러리를 호출하는 플래그
        const val FLAG_REQ_CAMERA = 101
        const val GET_GALLERY_IMAGE = 200
    }
}

