package transaction

import currency.CryptoCurrency
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

sealed class Transaction(
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

    operator fun component1(): UUID = id
    operator fun component2(): LocalDateTime = date
    operator fun component3(): Wallet = initiator
    operator fun component4(): CryptoCurrency = fromCurrency
    operator fun component5(): BigDecimal = fromAmount
}
