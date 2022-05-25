package com.zipdori.autoplanner.modules.common

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zipdori.autoplanner.BuildConfig
import com.zipdori.autoplanner.modules.calendarprovider.CalendarProviderModule
import com.zipdori.autoplanner.modules.calendarprovider.EventsVO
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CommonModule(val context: Context) {
    fun getAllEventsAsHashmap() : HashMap<String, ArrayList<EventsVO>> {
        // 기존 일정 불러오기
        val schedules: HashMap<String, ArrayList<EventsVO>> = HashMap()

        val calendarProviderModule: CalendarProviderModule = CalendarProviderModule(context!!)
        val allEvents: ArrayList<EventsVO> = calendarProviderModule.selectAllEvents()

        allEvents.forEach {
            if (it.deleted != 1) {
                val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.US)

                val start: Calendar = Calendar.getInstance()
                start.timeInMillis = it.dtStart
                start.set(Calendar.HOUR, 0)
                start.set(Calendar.MINUTE, 0)
                start.set(Calendar.SECOND, 0)
                if (it.dtEnd != null) {
                    val end: Calendar = Calendar.getInstance()
                    end.timeInMillis = it.dtEnd!!

                    while(start <= end) {
                        if (simpleDateFormat.format(start.time).equals(simpleDateFormat.format(end.time)) && it.allDay == 1) break

                        var tempArray: ArrayList<EventsVO>? = schedules.get(simpleDateFormat.format(start.time))
                        if (tempArray == null) {
                            tempArray = ArrayList()
                        }
                        tempArray.add(it)
                        schedules.put(simpleDateFormat.format(start.time), tempArray)

                        start.set(Calendar.DATE, start.get(Calendar.DATE) + 1)
                    }
                } else {
                    var tempArray: ArrayList<EventsVO>? = schedules.get(simpleDateFormat.format(start.time))
                    if (tempArray == null) {
                        tempArray = ArrayList()
                    }
                    tempArray.add(it)
                    schedules.put(simpleDateFormat.format(start.time), tempArray)
                }
            }
        }

        return schedules
    }

    fun getEventsVOArrayList(dateFormat : String) : ArrayList<EventsVO>? {
        val eventsVOArrayList: ArrayList<EventsVO>? = getAllEventsAsHashmap().get(dateFormat)
        if (eventsVOArrayList != null) {
            eventsVOArrayList.sortBy { it.dtStart }
        }

        return eventsVOArrayList
    }

    fun callNerApi(text : String) : ArrayList<NameEntity> {
        // val openApiUrl = "http://aiopen.etri.re.kr:8000/WiseNLU" // 문어
        val openApiUrl = "http://aiopen.etri.re.kr:8000/WiseNLU_spoken" // 구어

        val accessKey = "${BuildConfig.ETRI_API_KEY}" // API Key
        val analysisCode = "ner" // 언어 분석 코드
        // val gson: Gson = Gson()
        val gson: Gson = GsonBuilder().setLenient().create()

        val request: MutableMap<String, Any> = HashMap()
        val argument: MutableMap<String, String> = HashMap()

        argument.put("analysis_code", analysisCode)
        argument.put("text", text)

        request.put("access_key", accessKey)
        request.put("argument", argument)

        val url: URL
        var responseCode: Int? = null
        var responseBodyJson: String? = null
        var responseBody: MutableMap<*, *>? = null
        try {
            url = URL(openApiUrl)
            val con: HttpURLConnection = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.doOutput = true

            val wr = DataOutputStream(con.outputStream)
            wr.write(gson.toJson(request).toByteArray(charset("UTF-8")))
            wr.flush()
            wr.close()

            responseCode = con.responseCode
            val inputStream: InputStream = con.inputStream
            val br = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuffer()

            var inputLine: String? = ""
            inputLine = br.readLine()
            while (inputLine != null) {
                sb.append(inputLine)
                inputLine = br.readLine()
            }

            responseBodyJson = sb.toString()
            // TODO: 2022-05-25 delete 
            Log.i("responseBodyJson", responseBodyJson)

            // http 요청 오류 시 처리
            if (responseCode != 200) {
                // 오류 내용 출력
                System.out.println("[error] $responseBodyJson")
                return arrayListOf<NameEntity>()
            }

            responseBody = gson.fromJson(responseBodyJson, MutableMap::class.java)
            val result = (responseBody.get("result") as Double).toInt()
            var returnObject: Map<String?, Any?>
            var sentences: List<Map<*, *>?>

            // 분석 요청 오류 시 처리
            if (result != 0) {
                // 오류 내용 출력
                System.out.println("[error] " + responseBody.get("result"))
                return arrayListOf<NameEntity>()
            }

            // 분석 결과 활용
            returnObject = responseBody["return_object"] as Map<String?, Any?>
            sentences = (returnObject["sentence"] as List<Map<*, *>?>?)!!

            val nameEntitiesMap: MutableMap<String, NameEntity> = HashMap<String, NameEntity>()
            var nameEntities: ArrayList<NameEntity> = arrayListOf<NameEntity>()

            for (sentence in sentences) {
                // 개체명 분석 결과 수집
                val nameEntityRecognitionResult = sentence?.get("NE") as List<Map<String, Any>>?
                for (nameEntityInfo in nameEntityRecognitionResult!!) {
                    val name = nameEntityInfo["text"] as String?
                    var nameEntity = nameEntitiesMap[name]

                    // 원래는 null 일 때 새로운 키 추가, null 아닐 때 count + 1
                    if (nameEntity == null) {
                        nameEntity = NameEntity(name!!, (nameEntityInfo["type"] as String?)!!)
                        nameEntitiesMap.put(name, nameEntity)
                    }
                    nameEntities.add(nameEntity)
                }
            }

            /*
            // 개체명 분석 결과 빈도수로 정렬 (NameEntity 에 count 필요)
            if (0 < nameEntitiesMap.size) {
                nameEntities = ArrayList<NameEntity>(nameEntitiesMap.values)
                nameEntities?.sortedWith( kotlin.Comparator { nameEntity1, nameEntity2 -> nameEntity2.count - nameEntity1.count })
            }

            nameEntities?.forEach {
                Log.i("Text : ", it.text)
                Log.i("Type : ", it.type)
                println("Text : , " + it.text + "Type : " + it.type)
            }

             */

            return nameEntities

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return arrayListOf<NameEntity>()
    }
}