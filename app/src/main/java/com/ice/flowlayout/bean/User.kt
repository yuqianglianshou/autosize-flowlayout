package com.ice.flowlayout.bean

/**
 *
 *@author : lq
 *@date   : 2025/9/12
 *@desc   :
 *
 */
data class User(
    var name: String = "",
    var age: Int = 0
) {
    override fun toString(): String {
        return "$name, $age 岁"
    }
}
