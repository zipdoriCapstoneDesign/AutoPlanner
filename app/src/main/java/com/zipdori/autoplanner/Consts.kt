package com.zipdori.autoplanner

class Consts {
    companion object {
        //권한 플래그값 정의
        const val FLAG_PERM_CAMERA = 98
        const val FLAG_PERM_STORAGE_MULTIPICK = 99
        const val FLAG_PERM_STORAGE = 100


        //카메라와 갤러리를 호출하는 플래그
        const val FLAG_REQ_CAMERA = 101
        const val GET_GALLERY_IMAGE = 200
        const val GET_GALLERY_IMAGE_MULTI = 201

        // Calendar permission flag
        const val FLAG_PERM_CALENDAR = 300

        const val RESULT_SCHEDULELIST_REG = 400

    }
}