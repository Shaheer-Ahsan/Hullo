package com.shaheer_ahsan.hullo

/**
 * Created by Shaheer on 08/12/2017.
 */
class Conv {

    var isSeen: Boolean = false
    var timestamp: Long = 0

    constructor() {

    }

    constructor(seen: Boolean, timestamp: Long) {
        this.isSeen = seen
        this.timestamp = timestamp
    }
}