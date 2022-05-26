package tools

// 구상 도중 만들었지만 안 쓰일 수도 있음
data class StringPositionRecorder (val str:StringBuilder = StringBuilder(), var startIndex:Int = -1, var endIndex:Int? = null)