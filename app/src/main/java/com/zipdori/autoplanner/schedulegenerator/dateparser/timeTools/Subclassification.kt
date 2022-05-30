import java.util.*

// 세부 분류 클래스
class Subclassification {
    fun isSubclass_std(word: String): Boolean {
        val regex = Regex("""^\d\d?:\d\d?.*""")

        return isMatch(word, regex)
    }

    fun isSubclass_std_am(word: String): Boolean {
        val regex = Regex("""(오전|AM|am|아침|새벽)? *\d\d?.*:.*\d\d? *(오전|AM|am|아침|새벽)?.*""")

        return isMatch(word, regex)
    }

    fun isSubclass_std_pm(word: String): Boolean {
        val regex = Regex("""(오후|PM|pm|점심|낮|저녁|밤)? *\d\d?.*:.*\d\d? *(오후|PM|pm|점심|낮|저녁|밤)?.*""")

        return isMatch(word, regex)
    }

    fun isSubclass_std_duration(word: String): Boolean {
        val regex = Regex("""(오전|AM|am|아침|점심|새벽|오후|PM|pm|낮|저녁|밤)? *\d\d?:\d\d? *(-|~) *(오전|AM|am|아침|점심|새벽|오후|PM|pm|낮|저녁|밤)? *\d\d?:\d\d?""")

        return isMatch(word, regex)
    }

    fun isSubclass_hm(word: String): Boolean {
        val regex = Regex("""^\d\d?.*시.*(\d\d?.*분|반).*""")

        return isMatch(word, regex)
    }

    fun isSubclass_hm_am(word: String): Boolean {
        val regex = Regex("""(오전|AM|am|아침|새벽)? *\d\d?.*시.*(\d\d?.*분|반) *(오전|AM|am|아침|새벽)?.*""")

        return isMatch(word, regex)
    }

    fun isSubclass_hm_pm(word: String): Boolean {
        val regex = Regex("""(오후|PM|pm|점심|낮|저녁|밤)? *\d\d?.*시.*(\d\d?.*분|반) *(오후|PM|pm|점심|낮|저녁|밤)?.*""")

        return isMatch(word, regex)
    }

    fun isSubclass_hm_duration(word: String): Boolean {
        val regex = Regex("""(오전|AM|am|아침|점심|새벽|오후|PM|pm|낮|저녁|밤)? *\d\d?.*시.*(\d\d?.*분|반)? *(-|~) *(오전|AM|am|아침|점심|새벽|오후|PM|pm|낮|저녁|밤)? *\d\d?.*시.*(\d\d?.*분|반)?""")

        return isMatch(word, regex)
    }

    fun isSubclass_hm_later(word: String): Boolean {
        val regex = Regex("""^\d\d? *시간 *(\d\d *분|반) *(후|뒤).*""")

        return isMatch(word, regex)
    }

    fun isSubclass_hour(word: String): Boolean {
        val regex = Regex("""^\d\d? *시.*""")

        return isMatch(word, regex)
    }

    fun isSubclass_hour_am(word: String): Boolean {
        val regex = Regex("""(오전|AM|am|아침|새벽)? *\d\d? *시.*(오전|AM|am|아침|새벽)?.*""")

        return isMatch(word, regex)
    }

    fun isSubclass_hour_pm(word: String): Boolean {
        val regex = Regex("""(오후|PM|pm|점심|낮|저녁|밤)? *\d\d? *시.*(오후|PM|pm|점심|낮|저녁|밤)?.*""")

        return isMatch(word, regex)
    }

    fun isSubclass_hour_later(word: String): Boolean {
        val regex = Regex("""^\d\d? *시간 *(후|뒤).*""")

        return isMatch(word, regex)
    }

    fun isSubclass_minute(word: String): Boolean {
        val regex = Regex("""^\d\d? *분.*""")

        return isMatch(word, regex)
    }

    fun isSubclass_minute_later(word: String): Boolean {
        val regex = Regex("""^\d\d? *분 *(후|뒤).*""")

        return isMatch(word, regex)
    }

    fun isSubclass_timeZone(word: String): Boolean {
        val regex = Regex(""".*(오전|AM|am|아침|새벽|오후|PM|pm|점심|낮|저녁|밤).*""")

        return isMatch(word, regex)
    }

    // 단어가 정규 표현식과 매치되는가?
    fun isMatch(word: String, regex: Regex): Boolean {
        if (word.matches(regex)) return true
        else return false
    }

//    val calendar: Calendar = Calendar.getInstance()

//    fun calendarIsGood() {
//        calendar.get(Calendar.HOUR)
//        calendar.set(Calendar.HOUR, 23)
//
//        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 2)
//    }
}