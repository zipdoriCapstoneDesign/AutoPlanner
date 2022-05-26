package tools

class TypeOfRegex {
    companion object{
        const val num = "^\\d+$"
        const val duration = "~|부터"
        const val candidateDivider = ".|/|-"
        const val dateDividerKor = "년|월|일"
        const val endCommitmentFinder = "내일|모레|글피|어제|그제"

        const val dateRegistReverse = ".*\\d?\\d?\\d\\d[./년]\\s?\\d?\\d[./월]\\s?\\d?\\d.*"
        val extNum = Regex("\\D")
        val ymd = Regex("\\D*(\\d*[\\./년\\s])?\\s?(\\d*[\\./월\\s])?\\s?(\\d+)[^-~]?")

    }
}