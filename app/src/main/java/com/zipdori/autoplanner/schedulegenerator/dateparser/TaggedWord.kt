data class TaggedWord (var word:String, var tag:Int)

class Tags {
    companion object{
        const val DT_YEAR = 1
        const val DT_MONTH = 2
        const val DT_DAY = 3
        const val DT_OTHERS = 4
        const val DT_DURATION = 5

        const val DTI_RESERVATION = 6
        const val DT_UNDEFINED = 7
        const val DT_DIVIDE = 8

        const val TI_HOUR = 11
        const val TI_MINUTE = 12
        const val TI_OTHERS = 13
        const val TI_DURATION = 14

        const val O = 0

        const val MARK_DURATION = 21
    }
}