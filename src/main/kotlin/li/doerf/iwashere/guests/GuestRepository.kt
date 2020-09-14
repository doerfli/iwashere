package li.doerf.iwashere.guests

import org.springframework.data.jpa.repository.JpaRepository

interface GuestRepository : JpaRepository<Guest, Long> {

}