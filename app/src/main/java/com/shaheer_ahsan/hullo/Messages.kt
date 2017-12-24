package com.shaheer_ahsan.hullo

/**
 * Created by shaheer on 04/12/2017.
 */
class Messages {

    lateinit var message: String
    lateinit var type: String
    var time:Long = 0
    var isSeen: Boolean = false
    lateinit var from: String

    constructor(message: String, type: String, time: Long, seen: Boolean, from: String) {
        this.message = message
        this.type = type
        this.time = time
        this.isSeen = seen
        this.from = from
    }

    constructor()  {
    }

}