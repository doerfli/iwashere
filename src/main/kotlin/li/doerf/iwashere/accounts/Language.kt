package li.doerf.iwashere.accounts

enum class Language {
    DE,
    EN;

    fun lower(): String {
        return name.toLowerCase()
    }
}
