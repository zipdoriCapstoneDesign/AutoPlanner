package scheduleItem

data class ItemTime(var hour:Int?=null, var minute:Int?=null, var range:IntRange? = null, var inserted:Boolean = false)
