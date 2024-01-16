package transaction

import currency.CryptoCurrency
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class TradeTransaction(
    id: UUID,
    date: LocalDateTime,
    initiator: Wallet,
    fromCurrency: CryptoCurrency,
    fromAmount: BigDecimal,
    val receiver: Wallet,
    toCurrency: CryptoCurrency,
    toAmount: BigDecimal
) :
    Transaction(id, date, initiator, fromCurrency, fromAmount) {
        constructor(
            initiator: Wallet,
            fromCurrency: CryptoCurrency,
            fromAmount: BigDecimal,
            receiver: Wallet,
            toCurrency: CryptoCurrency,
            toAmount: BigDecimal
        ) : this(
            UUID.randomUUID(),
            LocalDateTime.now(),
            initiator,
            fromCurrency,
            fromAmount,
            receiver,
            toCurrency,
            toAmount
        )
}