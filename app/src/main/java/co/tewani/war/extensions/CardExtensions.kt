package co.tewani.war.extensions

object CardExtensions {

    fun getCardValue(code: String): Int {
        if (code.length != 2) return 0
        when (code[0]) {
            '2' -> return 2
            '3' -> return 3
            '4' -> return 4
            '5' -> return 5
            '6' -> return 6
            '7' -> return 7
            '8' -> return 8
            '9' -> return 9
            '0' -> return 10
            'J' -> return 11
            'Q' -> return 12
            'K' -> return 13
            'A' -> return 14
        }
        return 0
    }

}