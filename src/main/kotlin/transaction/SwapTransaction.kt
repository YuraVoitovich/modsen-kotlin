package transaction

import currency.CryptoCurrency
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class SwapTransaction(
    id: UUID,
    date: LocalDateTime,
    initiator: Wallet,
    fromCurrency: CryptoCurrency,
    fromAmount: BigDecimal,
    toCurrency: CryptoCurrency,
) :
    Transaction(id, date, initiator, fromCurrency, fromAmount) {

        constructor(
            initiator: Wallet,
            fromCurrency: CryptoCurrency,
            fromAmount: BigDecimal,
            toCurrency: CryptoCurrency,
            toAmount: BigDecimal
        ) : this(
            id = UUID.randomUUID(),
            date = LocalDateTime.now(),
            initiator = initiator,
            fromCurrency = fromCurrency,
            fromAmount = fromAmount,
            toCurrency = toCurrency,
        )

}