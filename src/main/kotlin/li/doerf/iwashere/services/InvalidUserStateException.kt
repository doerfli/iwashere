package li.doerf.iwashere.services

import li.doerf.iwashere.entities.AccountState

class InvalidUserStateException(expectedState: AccountState, state: AccountState) : Exception() {
}