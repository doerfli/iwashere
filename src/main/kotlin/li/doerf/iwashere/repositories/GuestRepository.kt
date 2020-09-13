package li.doerf.iwashere.repositories

import li.doerf.iwashere.entities.Guest
import org.springframework.data.jpa.repository.JpaRepository

interface GuestRepository : JpaRepository<Guest, Long> {

}