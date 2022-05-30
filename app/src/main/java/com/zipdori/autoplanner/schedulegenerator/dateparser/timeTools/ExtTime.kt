import scheduleItem.ItemTime

// 시간 추출 클래스
class ExtTime {

    fun extTime_std(word: String, type: String = "NONE"): ItemTime {
        val regex = Regex("""[^0-9|^:]""")
        var replacedWord: String
        var time_str: List<String>
        var timeObject: ItemTime = ItemTime()

        replacedWord = word.replace(regex, "")
        time_str = replacedWord.split(":")
        timeObject.hour = time_str[0].toInt()
        timeObject.minute = time_str[1].toInt()

        if (type.uppercase() == "AM") {
            if (timeObject.hour == 12) timeObject.hour = timeObject.hour?.minus(12)
        }
        else if (type.uppercase() == "PM") {
            if (timeObject.hour != 12) timeObject.hour = timeObject.hour?.plus(12)
        }
        else if (type.uppercase() == "LATER") {

        }

        return timeObject
    }

    fun extTime_hm(word: String, type: String = "NONE"): ItemTime {
        val regex = Regex("""[^0-9|^시|^반]""")
        var replacedWord: String
        var time_str: List<String>
        var timeObject: ItemTime = ItemTime()

        replacedWord = word.replace(regex, "")
        time_str = replacedWord.split("시")

        timeObject.hour = time_str[0].toInt()
        if (time_str[1] == "반") timeObject.minute = 30
        else timeObject.minute = time_str[1].toInt()

        if (type.uppercase() == "AM") {
            if (timeObject.hour == 12) timeObject.hour = timeObject.hour?.minus(12)
        }
        else if (type.uppercase() == "PM") {
            if (timeObject.hour != 12) timeObject.hour = timeObject.hour?.plus(12)
        }
        else if (type.uppercase() == "LATER") {

        }

        return timeObject
    }

    fun extTime_hour(word: String, type: String = "NONE"): ItemTime {
        val regex = Regex("""[^0-9]""")
        var replacedWord: String
        var timeObject: ItemTime = ItemTime()

        replacedWord = word.replace(regex, "")
        timeObject.hour = replacedWord.toInt()

        if (type.uppercase() == "AM") {
            if (timeObject.hour == 12) timeObject.hour = timeObject.hour?.minus(12)
        }
        else if (type.uppercase() == "PM") {
            if (timeObject.hour != 12) timeObject.hour = timeObject.hour?.plus(12)
        }
        else if (type.uppercase() == "LATER") {

        }

        return timeObject
    }

    fun extTime_minute(word: String): ItemTime {
        val regex = Regex("""[^0-9]""")
        var replacedWord: String
        var timeObject: ItemTime = ItemTime()

        replacedWord = word.replace(regex, "")
        timeObject.minute = replacedWord.toInt()

        return timeObject
    }

    fun extTime_duration(word: String, type: String = "NONE"): MutableList<ItemTime> {
        val regex = Regex("""[^0-9|^:|^시|^분|^~|^\-]""")
        val SC = Subclassification()

        var replacedWord: String
        var timeZone: String = "NONE"
        var time_str: List<String>
        var timeObjects: MutableList<ItemTime> = mutableListOf()

        replacedWord = word.replace(regex, "")
        time_str = replacedWord.split(Regex("""[~|-]"""))


        // 시작 시간
        if (time_str[0].contains(Regex("""[오전|AM|am|아침|새벽]"""))) timeZone = "AM"
        else if (time_str[0].contains(Regex("""[오후|PM|pm|점심|낮|저녁|밤]"""))) timeZone = "PM"

        if (SC.isSubclass_std(time_str[0]) || SC.isSubclass_std_am(time_str[0]) || SC.isSubclass_std_pm(time_str[0])) timeObjects.add(extTime_std(time_str[0], timeZone))
        else if (SC.isSubclass_hm(time_str[0]) || SC.isSubclass_hm_am(time_str[0]) || SC.isSubclass_hm_pm(time_str[0])) timeObjects.add(extTime_hm(time_str[0], timeZone))
        else if (SC.isSubclass_hour(time_str[0]) || SC.isSubclass_hour_am(time_str[0]) || SC.isSubclass_hour_pm(time_str[0])) timeObjects.add(extTime_hour(time_str[0], timeZone))
        else println("알 수 없는 타입")


        // 종료 시간
        // 오전 9:00 ~ 11:00 의 경우, 뒷 시간이 앞 시간과 같이 오전일 것이라 판단.
        if (time_str[1].contains(Regex("""[오전|AM|am|아침|새벽]"""))) timeZone = "AM"
        else if (time_str[1].contains(Regex("""[오후|PM|pm|점심|낮|저녁|밤]"""))) timeZone = "PM"

        if (SC.isSubclass_std(time_str[1]) || SC.isSubclass_std_am(time_str[1]) || SC.isSubclass_std_pm(time_str[1])) timeObjects.add(extTime_std(time_str[1], timeZone))
        else if (SC.isSubclass_hm(time_str[1]) || SC.isSubclass_hm_am(time_str[1]) || SC.isSubclass_hm_pm(time_str[1])) timeObjects.add(extTime_hm(time_str[1], timeZone))
        else if (SC.isSubclass_hour(time_str[1]) || SC.isSubclass_hour_am(time_str[1]) || SC.isSubclass_hour_pm(time_str[1])) timeObjects.add(extTime_hour(time_str[1], timeZone))
        else println("알 수 없는 타입")


        // 종료 시간이 시작 시간보다 작은 경우를 고려.
        // 11:00 ~ 1:00 의 경우, 뒷 시간을 오후 시간으로 판단.
        if (timeObjects[1].hour!! <= 12 && timeObjects[0].hour!! > timeObjects[1].hour!!) {
            timeObjects[1].hour = timeObjects[1].hour?.plus(12)
        }

        return timeObjects
    }

    fun extTime_timeZone(word: String): MutableList<ItemTime>? {
        val regex = Regex("""[^(오전)|^(AM)|^(am)|^(아침)|^(새벽)|^(오후)|^(PM)|^(pm)|^(점심)|^낮|^(저녁)|^밤]""")
        var replacedWord: String
        var timeObject_start: ItemTime = ItemTime()
        var timeObject_end: ItemTime = ItemTime()
        var timeObjects: MutableList<ItemTime> = mutableListOf()

        replacedWord = word.replace(regex, "")
        if (replacedWord == "오전" || replacedWord == "AM" || replacedWord == "am") {
            timeObject_start.hour = 0
            timeObjects.add(timeObject_start)
            timeObject_end.hour = 12
            timeObjects.add(timeObject_end)
        }
        else if (replacedWord == "오후" || replacedWord == "PM" || replacedWord == "pm") {
            timeObject_start.hour = 12
            timeObjects.add(timeObject_start)
            timeObject_end.hour = 24
            timeObjects.add(timeObject_end)
        }
        else if (replacedWord == "새벽") {
            timeObject_start.hour = 0
            timeObjects.add(timeObject_start)
            timeObject_end.hour = 6
            timeObjects.add(timeObject_end)
        }
        else if (replacedWord == "아침") {
            timeObject_start.hour = 6
            timeObjects.add(timeObject_start)
            timeObject_end.hour = 12
            timeObjects.add(timeObject_end)
        }
        else if (replacedWord == "점심") {
            timeObject_start.hour = 12
            timeObjects.add(timeObject_start)
            timeObject_end.hour = 12
            timeObjects.add(timeObject_end)
        }
        else if (replacedWord == "낮") {
            timeObject_start.hour = 12
            timeObjects.add(timeObject_start)
            timeObject_end.hour = 18
            timeObjects.add(timeObject_end)
        }
        else if (replacedWord == "저녁") {
            timeObject_start.hour = 18
            timeObjects.add(timeObject_start)
            timeObject_end.hour = 18
            timeObjects.add(timeObject_end)
        }
        else if (replacedWord == "밤") {
            timeObject_start.hour = 18
            timeObjects.add(timeObject_start)
            timeObject_end.hour = 24
            timeObjects.add(timeObject_end)
        }
        else {
            println("none or Error")
        }

        return timeObjects
    }

//    fun extTime_later(word: String, type: String = "NONE"): MutableList<Int> {
//        val regex = Regex("""[^0-9]""")
//        var replacedWord: String
//        var time_int: MutableList<Int> = arrayListOf()
//
//        replacedWord = word.replace(regex, "")
//
//        if (type.uppercase() == "HOUR") {
//            if
//        }
//    }
}