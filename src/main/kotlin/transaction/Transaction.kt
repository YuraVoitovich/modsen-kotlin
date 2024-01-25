package transaction

import currency.CryptoCurrency
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

open class Transaction(
    val id: UUID,
    val date: LocalDateTime,
    val initiator: Wallet,
    val fromCurrency: CryptoCurrency,
    val fromAmount: BigDecimal
) {

    constructor(
        initiator: Wallet,
        fromCurrency: CryptoCurrency,
        fromAmount: BigDecimal
    ) : this(
        id = UUID.randomUUID(),
        date = LocalDateTime.now(),
        initiator = initiator,
        fromCurrency = fromCurrency,
        fromAmount = fromAmount
    )
}
