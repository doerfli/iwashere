package li.doerf.iwashere.services

import li.doerf.iwashere.entities.Visitor

interface VisitorService {
    fun createVisitor(name: String, email: String, phone: String): Visitor
    fun deleteAll(visitors: List<Visitor>)
}
