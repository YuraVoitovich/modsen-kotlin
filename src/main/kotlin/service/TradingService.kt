package service

import currency.CryptoCurrency
import exchange.Exchange
import transaction.Transaction
import wallet.Wallet
import java.math.BigDecimal

interface TradingService {

    fun addExchange(exchange: Exchange)

    fun getAllExchanges(): Set<Exchange>

    fun swap(
        wallet: Wallet,
        passphrase: String,
        fromCurrency: CryptoCurrency,
        fromCurrencyAmount: BigDecimal,
        toCurrency: CryptoCurrency,
        exchange: Exchange
    ): Transaction

    fun trade(
        initiator: Wallet,
        receiver: Wallet,
        fromCurrency: CryptoCurrency,
        fromCurrencyAmount: BigDecimal,
        toCurrency: CryptoCurrency,
        toCurrencyAmount: BigDecimal,
        exchange: Exchange
    ) : Transaction
}