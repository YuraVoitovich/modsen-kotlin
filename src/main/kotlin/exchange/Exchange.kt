package exchange

import currency.CryptoCurrency
import transaction.Transaction
import java.math.BigDecimal

data class Exchange (var name: String) {

    var exchangeRates = mutableMapOf<Pair<CryptoCurrency, CryptoCurrency>, BigDecimal>()

    var transactionHistory = mutableListOf<Transaction>()

}