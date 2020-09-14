package li.doerf.iwashere.services

import li.doerf.iwashere.accounts.AccountState

class InvalidUserStateException(expectedState: AccountState, state: AccountState) : Exception() {
}