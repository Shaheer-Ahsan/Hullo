package com.shaheer_ahsan.hullo


class Users {

    lateinit var name: String
    lateinit var image: String
    lateinit var status: String
    lateinit var thumb_image: String

    constructor() {
    }

    constructor(name: String, image: String, status: String, thumb_image: String) {
        this.name = name
        this.image = image
        this.status = status
        this.thumb_image = thumb_image
    }

}