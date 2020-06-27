package li.doerf.iwashere.entities

enum class Language {
    DE,
    EN;

    fun lower(): String {
        return name.toLowerCase()
    }
}
