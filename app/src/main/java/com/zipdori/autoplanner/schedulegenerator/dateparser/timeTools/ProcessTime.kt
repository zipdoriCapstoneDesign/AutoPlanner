import scheduleItem.ItemTime

// 시간 처리 클래스
class ProcessTime {
    // 시, 분을 세부적으로 분리.
    fun sepTime(word: String, tag: String): MutableList<ItemTime>? {
        val ET = ExtTime()
        var subclass: String
        var timeObject: ItemTime? = null
        var timeObjects: MutableList<ItemTime>? = mutableListOf()

        if (tag == "TI_OTHERS") {
            subclass = subclassification_TI_OTHERS(word)
            println(subclass)
            when (subclass) {
                "std" -> timeObject = ET.extTime_std(word)
                "std_am" -> timeObject = ET.extTime_std(word, "AM")
                "std_pm" -> timeObject = ET.extTime_std(word, "PM")

                "hm" -> timeObject = ET.extTime_hm(word)
                "hm_am" -> timeObject = ET.extTime_hm(word, "AM")
                "hm_pm" -> timeObject = ET.extTime_hm(word, "PM")

                "hour" -> timeObject = ET.extTime_hour(word)
                "hour_am" -> timeObject = ET.extTime_hour(word, "AM")
                "hour_pm" -> timeObject = ET.extTime_hour(word, "PM")

                else -> println("none or Error")
            }
        }
        else if (tag == "TI_HOUR") {
            subclass = subclassification_TI_HOUR(word)
            println(subclass)
            when (subclass) {
                "std" -> timeObject = ET.extTime_std(word)
                "std_am" -> timeObject = ET.extTime_std(word, "AM")
                "std_pm" -> timeObject = ET.extTime_std(word, "PM")

                "hour" -> timeObject = ET.extTime_hour(word)
                "hour_am" -> timeObject = ET.extTime_hour(word, "AM")
                "hour_pm" -> timeObject = ET.extTime_hour(word, "PM")

                else -> println("none or Error")
            }
        }
        else if (tag == "TI_MINUTE") {
            subclass = subclassification_TI_MINUTE(word)
            println(subclass)
            when (subclass) {
                "minute" -> timeObject = ET.extTime_minute(word)

                else -> println("none or Error")
            }
        }
        else if (tag == "TI_DURATION") {
            subclass = subclassification_TI_DURATION(word)
            println(subclass)
            when (subclass) {
                "std_duration" -> timeObjects = ET.extTime_duration(word)
                "hm_duration" -> timeObjects = ET.extTime_duration(word)
                "timeZone" -> timeObjects = ET.extTime_timeZone(word)

                else -> println("none or Error")
            }
        }
        else {
            println("Incorrect Tag")
        }

        if (tag != "TI_DURATION") {
            timeObjects?.add(timeObject!!)
            //timeObjects?.add(timeObject!!)
        }

        return timeObjects
    }

    // TI_OTHERS 태그를 세부적으로 분리.
    fun subclassification_TI_OTHERS(word: String): String {
        val SC = Subclassification()

        // std - 12:30
        if (SC.isSubclass_std(word)) return "std"

        // std_am - 오전 12:30
        else if (SC.isSubclass_std_am(word)) return "std_am"

        // std_pm - 오후 12:30
        else if (SC.isSubclass_std_pm(word)) return "std_pm"

        // hm - 12시 30분
        else if (SC.isSubclass_hm(word)) return "hm"

        // hm_am - 오전 12시 30분
        else if (SC.isSubclass_hm_am(word)) return "hm_am"

        // hm_pm - 오후 12시 30분
        else if (SC.isSubclass_hm_pm(word)) return "hm_pm"

        // hm_later - 1시간 30분 후
        else if (SC.isSubclass_hm_later(word)) return "hm_later"

        // hour - 9시(부터/까지)
        else if (SC.isSubclass_hour(word)) return "hour"

        // hour_am - 오전 9시(부터/까지)
        else if (SC.isSubclass_hour_am(word)) return "hour_am"

        // hour_pm - 오후 9시(부터/까지)
        else if (SC.isSubclass_hour_pm(word)) return "hour_pm"

        // hour_later - 1시간 후
        else if (SC.isSubclass_hour_later(word)) return "hour_later"

        // minute_later - 30분 후
        else if (SC.isSubclass_minute_later(word)) return "minute_later"

        // none - 무시
        else return "none"
    }

    fun subclassification_TI_HOUR(word: String): String {
        val SC = Subclassification()

        // std - 12:00
        if (SC.isSubclass_std(word)) return "std"

        // std_am - 오전 12:00
        else if (SC.isSubclass_std_am(word)) return "std_am"

        // std_pm - 오후 12:00
        else if (SC.isSubclass_std_pm(word)) return "std_pm"

        // hour - 9시
        else if (SC.isSubclass_hour(word)) return "hour"

        // hour_am - 오전 9시
        else if (SC.isSubclass_hour_am(word)) return "hour_am"

        // hour_pm - 오후 9시
        else if (SC.isSubclass_hour_pm(word)) return "hour_pm"

        // none - 무시
        else return "none"
    }

    fun subclassification_TI_MINUTE(word: String): String {
        val SC = Subclassification()

        // minute - 30분
        if (SC.isSubclass_minute(word)) return "minute"

        // none - 무시
        else return "none"
    }

    fun subclassification_TI_DURATION(word: String): String {
        val SC = Subclassification()

        // 오전 11:30 ~ 오후 1:30
        if (SC.isSubclass_std_duration(word)) return "std_duration"

        // 오전 11시 30분 ~ 오후 1시 30분
        else if (SC.isSubclass_hm_duration(word)) return "hm_duration"

        // 오전, 오후, 낮, 밤 등
        else if (SC.isSubclass_timeZone(word)) return "timeZone"

        else return "none"
    }
}