package tools

class PromisedExpressionSet {
    var amount:Int = -1
    var expression:String = ""
    var roleTag:Int = 0

    fun decodeRole(){
        if(Regex("년").find(expression) != null) {
            roleTag = PromisedTags.YEAR
            return
        }
        if(Regex("달|개월").find(expression) != null) {
            roleTag = PromisedTags.MONTH
            return
        }
        if(Regex("일").find(expression) != null) {
            roleTag = PromisedTags.DAY
            return
        }
        if(Regex("시간").find(expression) != null) {
            roleTag = PromisedTags.HOUR
            return
        }
        if(Regex("분").find(expression) != null) {
            roleTag = PromisedTags.MINUTE
            return
        }
    }
    fun print(){
        var s:String = ""
        when(roleTag){
            PromisedTags.YEAR -> s = "YEAR"
            PromisedTags.MONTH -> s = "MONTH"
            PromisedTags.DAY -> s = "DAY"
            PromisedTags.HOUR -> s = "HOUR"
            PromisedTags.MINUTE -> s = "MINUTE"
        }

        println("$amount | $s")
    }
}

class PromisedTags{
    companion object{
        const val YEAR = 1
        const val MONTH = 2
        const val DAY = 3
        const val HOUR = 4
        const val MINUTE = 5
    }
}
