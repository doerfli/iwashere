package li.doerf.iwashere.accounts

class InvalidUserStateException(expectedState: AccountState, state: AccountState) : Exception() {
}