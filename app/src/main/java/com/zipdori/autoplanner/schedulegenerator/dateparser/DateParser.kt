package com.zipdori.autoplanner.schedulegenerator.dateparser

import TaggedWord
import Tags
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import com.zipdori.autoplanner.modules.common.NameEntity
import com.zipdori.autoplanner.schedulegenerator.DateForm
import scheduleItem.ItemDate
import scheduleItem.ItemSchedule
import scheduleItem.ItemSide
import scheduleItem.ItemTime
import tools.StringPositionRecorder
import tools.TypeOfRegex
import tools.TypeOfRegex.Companion.ymd
import java.util.*
import com.zipdori.autoplanner.R
import java.util.concurrent.ThreadLocalRandom

class DateParser(val context: Context) {
    var sources:ArrayList<NameEntity>? = null
    var events:ArrayList<EventsVO> = arrayListOf()
    var imgUris: ArrayList<Uri> = arrayListOf<Uri>()
    var curUri: Uri? = null

    fun setSource(s:ArrayList<NameEntity>){
        sources=s
    }
    fun setUri(uri:Uri){
        curUri=uri
    }

    fun extractAsDate(): MutableList<EventsVO> {
        val taggedWords:MutableList<TaggedWord> = mutableListOf()
        for (i in sources!!.indices){
            val currentTag:Int = convertTagStringToInt(sources!![i].type)
            taggedWords.add(TaggedWord(sources!![i].text, currentTag))
        }
        // 1. DT 태그가 연속해서 출현하면 붙여서 저장. StringPositionRecorder 리스트 사용
        println("---------------1단계--------------")
        val rangedWordBox:MutableList<StringPositionRecorder> = packUpWords(taggedWords)


        // 2. DT 태그를 연결시켜 재정리한 결과물인 StringPositionRecorder를 정규표현식으로 분석해서 ItemDate에 넘겨줌
        println("---------------2단계--------------")
        val itemDateList:MutableList<ItemDate> = mutableListOf()
        val itemTimeList:MutableList<ItemTime> = mutableListOf()
        val cal = Calendar.getInstance()
        val today = cal.get(Calendar.DAY_OF_MONTH)
        for(i in rangedWordBox){
            println("i : $i")
            // 정규표현식 적용하는 부분
            var resultRegexDate = ymd.find(i.str)
            if(resultRegexDate != null) {
                while (resultRegexDate != null) {
                    val itemDate: ItemDate? = parseDateByRegex(i, resultRegexDate)
                    if(itemDate != null) itemDateList.add(itemDate)
                    resultRegexDate = resultRegexDate.next()
                }
            }
            else{
                var thisRange = if(i.endIndex != null) i.startIndex..i.endIndex!! else i.startIndex..i.startIndex
                var result = Regex("내일").find(i.str)
                if(result != null) itemDateList.add(ItemDate(null,null,today+1,thisRange))
                else {
                    result = Regex("모레").find(i.str)
                    if(result != null) itemDateList.add(ItemDate(null,null,today+2,thisRange))
                    else {
                        result = Regex("어제").find(i.str)
                        if(result != null) itemDateList.add(ItemDate(null,null,today-1,thisRange))
                    }
                }
            }
        }

        // 일정과 시간 모두 모였다고 가정하고 어떻게 처리할지?
        // 3. 일정 사이드부터 수집. dt리스트는 itemDateList, ti리스트는 itemTimeList
        println("---------------3단계--------------")
        // TODO: 2022-05-27 시간 관련코드 완성시 수정
        // val randomItemTimeSingle = ItemTime(16,0,13..13)
        // itemTimeList.add(randomItemTimeSingle)  // 처리할 시간이 있다고 임의로 만들어서 가정

        val itemSideList:MutableList<ItemSide> = mutableListOf()

        // 일자와 인접한 시간은 일자와 묶어서 itemSide로 들어감
        for(dateItem in itemDateList){
            val sideItem = ItemSide(dateItem,range=dateItem.range!!)
            for(timeItem in itemTimeList){
                // 인식된 시간일정의 멀지 않은 앞쪽에 인식된 날짜일정이 있으면 년월일-시/분 연결된 하나의 일정으로 취급
                if(timeItem.range!!.first - dateItem.range!!.last in 1..2) {
                    sideItem.itemTime = timeItem
                    sideItem.range = dateItem.range!!.first..timeItem.range!!.last
                    timeItem.inserted = true
                    break
                }
            }
            itemSideList.add(sideItem)
        }

        //들어가지 못한 시간은 시간만 인식된 독립된 일정으로 처리.
        for(timeItem in itemTimeList){
            if(!timeItem.inserted) {
                itemSideList.add(ItemSide(null,timeItem,timeItem.range!!))
            }
        }

        println(itemSideList)

        // 4. itemSideList에서 ItemSide끼리 시작과 종료 날짜로 관련됐는지, 각기 무관한 일정인지 출신 인덱스 범위로 분석해서 일정 아이템화(작업중)
        val scheduleList:MutableList<ItemSchedule> = mutableListOf()
        var tempSchedule: ItemSchedule? = null
        var prevRangeStart = 0
        var prevRangeEnd = 0
        for(item in itemSideList){
            if(tempSchedule == null){
                tempSchedule = ItemSchedule(item, null, item.range)
            }
            else{
                if(item.range.first - prevRangeEnd <2 && item.range.first >= prevRangeStart){
                    tempSchedule.to = item
                    tempSchedule.range = tempSchedule.range.first..item.range.last
                    scheduleList.add(tempSchedule)
                    tempSchedule = null
                }
                else{
                    scheduleList.add(tempSchedule)
                    tempSchedule = ItemSchedule(item, null, item.range)
                }
            }
            prevRangeStart = item.range.first
            prevRangeEnd = item.range.last
        }
        if(tempSchedule!=null) scheduleList.add(tempSchedule)
        println(scheduleList)

        val eventList:MutableList<EventsVO> = convertListScheduleToEvent(scheduleList)

        println("----------------------------event----------------------------")
        for(event in eventList){
            val from = Date(event.dtStart)
            var to:Date = if(event.dtEnd != null) Date(event.dtEnd!!)
            else Date(event.dtStart+60000*60)

            val fromP = DateForm.integratedForm.format(event.dtStart)
            val toP = DateForm.integratedForm.format(event.dtEnd!!)
            println("$fromP ~ $toP")
        }
        for (e in eventList){
            events.add(e)
            imgUris.add(curUri!!)
        }
        return eventList
    }

    private fun fillNullDefaultItemSchedule(itemSch: ItemSchedule) {
        fillNullDefaultItemSide(itemSch.from)
        if(itemSch.to != null) {
            println("asdf : $itemSch.to")
            if(itemSch.to!!.itemDate == null) itemSch.to!!.itemDate = itemSch.from.itemDate!!.copy()
            else {
                if(itemSch.to!!.itemDate!!.year == null) itemSch.to!!.itemDate!!.year = itemSch.from.itemDate!!.year
                if(itemSch.to!!.itemDate!!.month == null) itemSch.to!!.itemDate!!.month = itemSch.from.itemDate!!.month
            }

            if(itemSch.to!!.itemTime == null) fillNullDefaultItemTime(itemSch.to!!.itemTime)
        }

    }

    private fun fillNullDefaultItemSide(side: ItemSide) {
        side.itemDate = fillNullDefaultItemDate(side.itemDate)
        side.itemTime = fillNullDefaultItemTime(side.itemTime)
    }

    private fun fillNullDefaultItemTime(itemTime: ItemTime?): ItemTime? {
        var finishedTime = itemTime
        if(finishedTime == null){
            finishedTime = ItemTime(9, 0)
        }
        else{
            if(finishedTime.minute == null) finishedTime.minute = 0
        }
        return finishedTime
    }

    private fun fillNullDefaultItemDate(itemDate: ItemDate?): ItemDate? {
        val cal = Calendar.getInstance()
        val curYear = cal.get(Calendar.YEAR)
        val curMonth = cal.get(Calendar.MONTH)+1
        val curDay = cal.get(Calendar.DAY_OF_MONTH)

        var finishedDate = itemDate
        if(finishedDate == null){
            finishedDate = ItemDate(curYear, curMonth, curDay)
        }
        else{
            if(finishedDate.year == null) finishedDate.year = curYear
            if(finishedDate.month == null)finishedDate.month = curMonth
            if(finishedDate.day == null) finishedDate.day = curDay
        }

        return finishedDate
    }

    private fun convertSideToMillis(side: ItemSide): Long {
        val cal = Calendar.getInstance()

        println("여기 확인 : $side")
        val year = side.itemDate!!.year!!
        val month = side.itemDate!!.month!!-1
        val day = side.itemDate!!.day!!
        if(side.itemTime != null) {
            val hour = side.itemTime!!.hour!!
            val minute = side.itemTime!!.minute!!

            println("::$year, $month,$day,$hour,$minute")
            cal.set(year, month, day, hour, minute)
        }
        else
            cal.set(year,month,day, 9, 0)
        return cal.timeInMillis
    }

    private fun convertListScheduleToEvent(scheduleList: MutableList<ItemSchedule>): MutableList<EventsVO> {
        val eventList:MutableList<EventsVO> = mutableListOf()

        for(itemSch in scheduleList){
            fillNullDefaultItemSchedule(itemSch)

            val fromMillis = convertSideToMillis(itemSch.from)
            var toMillis:Long = if (itemSch.to == null) fromMillis+60000*60 else convertSideToMillis(itemSch.to!!)

            val colors: ArrayList<String> = ArrayList()

            colors.add("#5EAEFF")
            colors.add("#82B926")
            colors.add("#a276eb")
            colors.add("#6a3ab2")
            colors.add("#666666")
            colors.add("#FFFF00")
            colors.add("#3C8D2F")
            colors.add("#FA9F00")
            colors.add("#FF0000")

            val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            val calendarId = sharedPreferences.getLong(context.getString(R.string.calendar_index), 0)
            val eventTemp = EventsVO(0,calendarId,null,"제목을 정해주세요",null,null, -10572033,
                Color.parseColor(colors.get(ThreadLocalRandom.current().nextInt(1, 9))),fromMillis,toMillis,"Asia/Seoul",null,null,null,null,null,null,null)
            eventList.add(eventTemp)
        }

        return eventList
    }

    private fun parseDateByRegex(i:StringPositionRecorder, resultRegexDate: MatchResult): ItemDate? {
        // 날짜 정보가 들어있으면 ItemDate로 가공
        val itemDate = ItemDate()
        var (n1, n2, n3) = resultRegexDate.destructured
        var num1 = n1.replace(TypeOfRegex.extNum, "")
        var num2 = n2.replace(TypeOfRegex.extNum, "")
        var num3 = n3.replace(TypeOfRegex.extNum, "")
        println("num1 = $num1, num2 = $num2, num3 = $num3")

        // divider가 다르면 앞에것만 취함
        if(num1.isNotEmpty() && num2.isNotEmpty()) {
            if (n1[num1.length] != '년' && n1[num1.length] != n2[num2.length]) {
                num3 = num2
                num2 = num1
                num1 = ""
            }
        }

        // num3부터 일.월.년 순으로 취급함
        itemDate.day = num3.toInt()

        if (num1 != "" && num2 == "") itemDate.month = num1.toInt()
        else itemDate.month = num2.toIntOrNull()

        if (num1 != "" && num2 != "") itemDate.year = num1.toIntOrNull()

        // 이 일정이 추출된 단어의 원본에서의 구간 저장
        if(i.endIndex !=null) itemDate.range = i.startIndex..i.endIndex!!
        else itemDate.range = i.startIndex..i.startIndex
        println(itemDate)

        if(itemDate.year!=null) {
            if (itemDate.year!! < 2000 || itemDate.year!!>2200)
                return null
        }
        if(itemDate.month!=null) if (itemDate.month!! >12) return null
        if(itemDate.day!! >31) return null
        return itemDate
    }


    private fun packUpWords(taggedWords: MutableList<TaggedWord>): MutableList<StringPositionRecorder> {
        val wordPacks:MutableList<StringPositionRecorder> = mutableListOf()
        var temp:StringPositionRecorder? = null
        for ((index, item) in taggedWords.withIndex()) {
            val currentTag = item.tag
            if (!isTagDT(currentTag)) {
                if(temp!=null) {
                    temp.endIndex = index-1
                    wordPacks.add(temp)
                    temp = null
                }
                continue
            }
            else {
                if(temp == null) temp = StringPositionRecorder(startIndex = index)
                temp.str.append(item.word)
            }
        }
        println(wordPacks)
        return(wordPacks)
    }

    private fun convertTagStringToInt(s: String): Int {
        when(s){
            "O" -> return Tags.O
            "DT_YEAR" -> return Tags.DT_YEAR
            "DT_MONTH" -> return Tags.DT_MONTH
            "DT_DAY" -> return Tags.DT_DAY
            "DT_OTHERS" -> return Tags.DT_OTHERS
            "DT_DURATION" -> return Tags.DT_DURATION

            "TI_DURATION" -> return Tags.TI_DURATION
        }
        return -1
    }
    private fun isTagDT(tag: Int): Boolean {
        if(tag in 1..5) return true
        return false
    }
}