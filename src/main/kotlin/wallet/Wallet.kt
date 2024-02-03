package wallet

import currency.CryptoCurrency
import user.User
import java.math.BigDecimal
import java.util.*

data class Wallet(
    val id: UUID,
    var name: String,
    var isCold: Boolean,
    var passphrase: String,
    val user: User
) {
    val cryptoCurrencies = mutableMapOf<CryptoCurrency, BigDecimal>()

    companion object {
        fun getDefaultWallet(user: User): Wallet {
            val defaultWallet = Wallet("Default", "default", user)
            defaultWallet.cryptoCurrencies[CryptoCurrency("USDT")] = BigDecimal.TEN
            return defaultWallet
        }
    }
    constructor(name: String, passphrase: String, user: User) : this(
        UUID.randomUUID(),
        name,
        false,
        passphrase,
        user
    )
}


