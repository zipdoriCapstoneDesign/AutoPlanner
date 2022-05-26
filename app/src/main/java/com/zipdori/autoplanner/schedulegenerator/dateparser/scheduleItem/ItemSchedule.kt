package scheduleItem

data class ItemSchedule(val from:ItemSide, var to:ItemSide? = null, var range:IntRange)
