package service.impl

import currency.CryptoCurrency
import exception.*
import exchange.Exchange
import service.TradingService
import transaction.SwapTransaction
import transaction.TradeTransaction
import transaction.Transaction
import user.User
import user.UserStatus
import wallet.Wallet
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

class TradingServiceImpl(val clock: Clock, val random: Random) : TradingService {

    companion object {
        val ALLOWED_TRADE_STATUSES = setOf(UserStatus.APPROVED)
        const val ZERO_BALANCE_MESSAGE = "Not enough currency: %s on the balance. Your balance: 0, needed: %s"
        const val NOT_ENOUGH_CURRENCY_EXCEPTION_MESSAGE = "Not enough currency: %s on the balance. Your balance: %s, needed: %s"
        const val INCORRECT_PASSPHRASE_EXCEPTION_MESSAGE = "Passphrase is incorrect"
        const val TRADING_IS_NOT_ALLOWED_EXCEPTION_MESSAGE = "Trading for user with id %s is not allowed"
        const val EXCHANGE_PAIR_IS_NOT_PRESENT = "Exchange pair %s to %s is not present"
        const val TRANSACTION_FAILED_MESSAGE = "Transaction failed"
    }

    val exchanges = mutableSetOf<Exchange>()
    override fun addExchange(exchange: Exchange) {
        this.exchanges.add(exchange)
    }

    override fun getAllExchanges(): Set<Exchange> = exchanges

    override fun swap(
        wallet: Wallet,
        passphrase: String,
        fromCurrency: CryptoCurrency,
        fromCurrencyAmount: BigDecimal,
        toCurrency: CryptoCurrency,
        exchange: Exchange
    ) : Transaction {

        val exchangeRate = exchange.exchangeRates.getValueOrThrow(fromCurrency to toCurrency) {
            ExchangePairIsNotPresentException(EXCHANGE_PAIR_IS_NOT_PRESENT
                .format(fromCurrency, toCurrency))
        }

        val rand = random.nextInt(0, 51)
        if (rand in createRange(27, 50)) {
            throw TransactionFailedException(TRANSACTION_FAILED_MESSAGE)
        }

        checkWalletPassphrase(wallet, passphrase)
        checkUserCurrencyAmount(wallet, fromCurrency, fromCurrencyAmount)

        val transaction = SwapTransaction(
            id = UUID.randomUUID(),
            date = LocalDateTime.now(clock),
            initiator = wallet,
            fromCurrency = fromCurrency,
            fromAmount = fromCurrencyAmount,
            toCurrency = toCurrency,
        )

        val result = exchangeRate.multiply(fromCurrencyAmount)

        wallet.cryptoCurrencies[fromCurrency] = wallet.cryptoCurrencies[fromCurrency]!!
            .minus(fromCurrencyAmount)
            .setScale(5, RoundingMode.HALF_UP)

        wallet.cryptoCurrencies[toCurrency] = wallet.cryptoCurrencies
            .getOrDefault(toCurrency, BigDecimal.ZERO)
            .plus(result)
            .setScale(5, RoundingMode.HALF_UP)

        exchange.transactionHistory.add(transaction)

        return transaction
    }

    fun processTransaction(transaction: Transaction): Wallet {
        return when (transaction) {
            is TradeTransaction -> transaction.receiver
            is SwapTransaction -> transaction.initiator
        }
    }

    override fun trade(
        initiator: Wallet,
        receiver: Wallet,
        fromCurrency: CryptoCurrency,
        fromCurrencyAmount: BigDecimal,
        toCurrency: CryptoCurrency,
        toCurrencyAmount: BigDecimal,
        exchange: Exchange
    ) : Transaction {

        checkTradingForUserIsAllowed(initiator.user)
        checkTradingForUserIsAllowed(receiver.user)

        checkUserCurrencyAmount(
            wallet = initiator,
            currency = fromCurrency,
            amount = fromCurrencyAmount
        )

        val transaction = TradeTransaction(
            id = UUID.randomUUID(),
            date = LocalDateTime.now(clock),
            initiator = initiator,
            fromCurrency = fromCurrency,
            fromAmount = BigDecimal(1.0),
            receiver = receiver,
            toCurrency = toCurrency,
            toAmount = BigDecimal(42200)
        )

        initiator.cryptoCurrencies[fromCurrency] = initiator
            .cryptoCurrencies[fromCurrency]!!
            .minus(fromCurrencyAmount)
            .setScale(5, RoundingMode.HALF_UP)

        receiver.cryptoCurrencies[toCurrency] = receiver.cryptoCurrencies
            .getOrDefault(toCurrency, BigDecimal.ZERO).add(toCurrencyAmount)
        exchange.transactionHistory.add(transaction)
        return transaction
    }


    private fun checkWalletPassphrase(wallet: Wallet, passphrase: String) {
        if ((wallet.passphrase != passphrase))
            throw IncorrectPassphraseException(INCORRECT_PASSPHRASE_EXCEPTION_MESSAGE)
    }

    private fun checkTradingForUserIsAllowed(user: User) {
        if (!ALLOWED_TRADE_STATUSES.contains(user.status)) {
            throw TradingIsNotAllowedException(TRADING_IS_NOT_ALLOWED_EXCEPTION_MESSAGE
                .format(user.id))
        }
    }

    private fun checkUserCurrencyAmount(wallet: Wallet, currency: CryptoCurrency, amount: BigDecimal) {
        val balanceCurrencyAmount = wallet.cryptoCurrencies
            .getValueOrThrow(currency) {
                NotEnoughCurrencyOnTheBalanceException(ZERO_BALANCE_MESSAGE
                    .format(currency, amount) ) }
        if (balanceCurrencyAmount < amount) {
            throw NotEnoughCurrencyOnTheBalanceException(NOT_ENOUGH_CURRENCY_EXCEPTION_MESSAGE
                .format(currency, balanceCurrencyAmount, amount))
        }
    }

    fun <K, V> Map<K, V>.getValueOrThrow(key: K, exceptionSupplier: () -> Throwable)
    : V = this[key] ?: throw exceptionSupplier()

}

fun <A, B> Pair<A, B>.swap(): Pair<B, A> {
    return second to first
}

fun createRange(start: Int = 0, end: Int = 10): IntRange {
    return start..end
}