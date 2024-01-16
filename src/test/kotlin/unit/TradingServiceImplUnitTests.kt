package unit

import currency.CryptoCurrency
import exception.ExchangePairIsNotPresentException
import exception.NotEnoughCurrencyOnTheBalanceException
import exception.TradingIsNotAllowedException
import exchange.Exchange
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.TradingService
import service.impl.TradingServiceImpl
import service.impl.TradingServiceImpl.Companion.EXCHANGE_PAIR_IS_NOT_PRESENT
import service.impl.TradingServiceImpl.Companion.NOT_ENOUGH_CURRENCY_EXCEPTION_MESSAGE
import service.impl.TradingServiceImpl.Companion.ZERO_BALANCE_MESSAGE
import transaction.SwapTransaction
import transaction.TradeTransaction
import user.User
import user.UserStatus
import wallet.Wallet
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class TradingServiceImplUnitTests {

    companion object {

        lateinit var tradingService: TradingService

        @JvmStatic
        @BeforeAll
        fun setup() {
            tradingService = TradingServiceImpl(clock, Random(1L))
        }

        val clock = Clock.fixed(Instant.parse("2024-01-14T12:00:00Z"), ZoneId.systemDefault())

        val USDT = CryptoCurrency("USDT")
        val BTC = CryptoCurrency("BTC")
        val NEAR = CryptoCurrency("NEAR")
        val ETH = CryptoCurrency("ETH")
        val BINANCE = Exchange("Binance")
        val BYBIT = Exchange("ByBit")

        val USER1 = User("email1", "fullName1", UserStatus.APPROVED)
        val USER2 = User("email1", "fullName1", UserStatus.APPROVED)
        val USER3 = User("email1", "fullName1", UserStatus.NEW)
        val USER4 = User("email1", "fullName1", UserStatus.BLOCKED)
        val USER1_NOT_ENOUGH_BALANCE_WALLET = Wallet("USER1_NOT_ENOUGH_BALANCE_WALLET", "pass2", USER1)
        val USER1_EMPTY_WALLET = Wallet("USER1_EMPTY_WALLET", "pass1", USER1)
        val USER1_ENOUGH_BALANCE_WALLET = Wallet("USER1_ENOUGH_BALANCE_WALLET", "pass2", USER1)
        val USER2_EMPTY_WALLET = Wallet("USER2_EMPTY_WALLET", "pass1", USER2)
        val USER2_NOT_ENOUGH_BALANCE_SWAP_WALLET = Wallet("USER2_NOT_ENOUGH_BALANCE_SWAP_WALLET", "pass2", USER2)
        val USER2_ZERO_BALANCE_SWAP_WALLET = Wallet("USER2_ZERO_BALANCE_SWAP_WALLET", "pass2", USER2)
        val USER2_ENOUGH_BALANCE_SWAP_WALLET = Wallet("USER2_ENOUGH_BALANCE_SWAP_WALLET", "pass2", USER2)
        val USER3_WALLET = Wallet("USER3_WALLET", "pass3", USER3)
    }

    @BeforeEach
    fun methodSetup() {
        USER1_NOT_ENOUGH_BALANCE_WALLET.cryptoCurrencies[BTC] = BigDecimal(0.5)
        USER1_ENOUGH_BALANCE_WALLET.cryptoCurrencies[BTC] = BigDecimal(1.1)

        USER2_ENOUGH_BALANCE_SWAP_WALLET.cryptoCurrencies[BTC] = BigDecimal(1)
        USER2_NOT_ENOUGH_BALANCE_SWAP_WALLET.cryptoCurrencies[BTC] = BigDecimal(0.5)

        BINANCE.exchangeRates[Pair(ETH, USDT)] = BigDecimal(22000)
        BINANCE.exchangeRates[Pair(NEAR, USDT)] = BigDecimal(3.4)
        BINANCE.exchangeRates[Pair(USDT, NEAR)] = BigDecimal(0.294)

        BYBIT.exchangeRates[Pair(BTC, USDT)] = BigDecimal(42100)
        BYBIT.exchangeRates[Pair(ETH, USDT)] = BigDecimal(22010)
        BYBIT.exchangeRates[Pair(NEAR, USDT)] = BigDecimal(3.4)
        BYBIT.exchangeRates[Pair(USDT, NEAR)] = BigDecimal(0.294)

        tradingService.addExchange(BYBIT)
        tradingService.addExchange(BINANCE)

    }

    @Test
    fun trade_zeroBalance_shouldThrowNotEnoughCurrencyOnTheBalanceException() {

        assertThatThrownBy { tradingService.trade(
            initiator = USER1_EMPTY_WALLET,
            receiver = USER2_EMPTY_WALLET,
            fromCurrency = BTC,
            fromCurrencyAmount = BigDecimal.ONE,
            toCurrency = USDT,
            toCurrencyAmount = BigDecimal(42120),
            exchange = BINANCE
        ) }.isInstanceOf(NotEnoughCurrencyOnTheBalanceException::class.java)
            .hasMessage(String.format(ZERO_BALANCE_MESSAGE, BTC, BigDecimal.ONE))
    }

    @Test
    fun trade_tradingFirstUserNotApproved_shouldThrowTradingIsNotAllowedException() {

        assertThatThrownBy { tradingService.trade(
            initiator = USER3_WALLET,
            receiver = USER1_EMPTY_WALLET,
            fromCurrency = BTC,
            fromCurrencyAmount = BigDecimal.ONE,
            toCurrency = USDT,
            toCurrencyAmount = BigDecimal(42120),
            exchange = BINANCE
        ) }.isInstanceOf(TradingIsNotAllowedException::class.java)


    }

    @Test
    fun trade_notEnoughBalance_shouldThrowNotNotEnoughCurrencyOnTheBalanceException() {

        assertThatThrownBy { tradingService.trade(
            initiator = USER1_NOT_ENOUGH_BALANCE_WALLET,
            receiver = USER2_EMPTY_WALLET,
            fromCurrency = BTC,
            fromCurrencyAmount = BigDecimal.ONE,
            toCurrency = USDT,
            toCurrencyAmount = BigDecimal(42120),
            exchange = BINANCE
        ) }.isInstanceOf(NotEnoughCurrencyOnTheBalanceException::class.java)
            .hasMessage(String.format(
                NOT_ENOUGH_CURRENCY_EXCEPTION_MESSAGE,
                BTC,
                USER1_NOT_ENOUGH_BALANCE_WALLET.cryptoCurrencies[BTC],
                BigDecimal.ONE
            ))


    }



    @Test
    fun trade_correctData_shouldReturnTransactionAndAddTransactionToTheHistory() {

        val expected = TradeTransaction(
            id = UUID.randomUUID(),
            date = LocalDateTime.now(clock),
            initiator = USER1_ENOUGH_BALANCE_WALLET,
            receiver = USER2_EMPTY_WALLET,
            fromCurrency = BTC,
            fromAmount = BigDecimal.ONE,
            toCurrency = USDT,
            toAmount = BigDecimal(42120),
        )

        val actual = tradingService.trade(
            initiator = USER1_ENOUGH_BALANCE_WALLET,
            receiver = USER2_EMPTY_WALLET,
            fromCurrency = BTC,
            fromCurrencyAmount = BigDecimal.ONE,
            toCurrency = USDT,
            toCurrencyAmount = BigDecimal(42120),
            exchange = BINANCE
        )

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected)

        assertThat(USER2_EMPTY_WALLET.cryptoCurrencies[USDT])
            .usingComparator(BigDecimal::compareTo)
            .isEqualByComparingTo(BigDecimal(42120).setScale(5, RoundingMode.HALF_UP))

        assertThat(USER1_ENOUGH_BALANCE_WALLET.cryptoCurrencies[BTC])
            .usingComparator(BigDecimal::compareTo)
            .isEqualByComparingTo(BigDecimal(0.1).setScale(5, RoundingMode.HALF_UP))

        assertThat(BINANCE.transactionHistory)
            .isNotEmpty

        assertThat(BINANCE.transactionHistory[0])
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(actual)
    }

    @Test
    fun swap_correctRequest_shouldReturnTransaction() {

        val expected = SwapTransaction(
            id = UUID.randomUUID(),
            date = LocalDateTime.now(clock),
            initiator = USER2_ENOUGH_BALANCE_SWAP_WALLET,
            fromCurrency = BTC,
            fromAmount = BigDecimal.ONE,
            toCurrency = USDT,
        )

        val actual = tradingService.swap(
            wallet = USER2_ENOUGH_BALANCE_SWAP_WALLET,
            fromCurrency = BTC,
            fromCurrencyAmount = BigDecimal.ONE,
            toCurrency = USDT,
            exchange = BYBIT,
            passphrase = USER2_ENOUGH_BALANCE_SWAP_WALLET.passphrase
        )


        assertThat(USER2_ENOUGH_BALANCE_SWAP_WALLET.cryptoCurrencies[USDT])
            .usingComparator(BigDecimal::compareTo)
            .isEqualByComparingTo(BYBIT.exchangeRates[BTC to USDT])


        assertThat(USER2_ENOUGH_BALANCE_SWAP_WALLET.cryptoCurrencies[BTC])
            .usingComparator(BigDecimal::compareTo)
            .isEqualByComparingTo(BigDecimal.ZERO)

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected)


        assertThat(BYBIT.transactionHistory)
            .isNotEmpty

        assertThat(BYBIT.transactionHistory[0])
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(actual)
    }

    @Test
    fun swap_exchangePairIsNotPresent_shouldThrowExchangePairIsNotPresentException() {

        assertThatThrownBy { tradingService.swap(
            wallet = USER2_ZERO_BALANCE_SWAP_WALLET,
            fromCurrency = BTC,
            fromCurrencyAmount = BigDecimal.ONE,
            toCurrency = USDT,
            exchange = BINANCE,
            passphrase = USER2_ZERO_BALANCE_SWAP_WALLET.passphrase
        ) }.isInstanceOf(ExchangePairIsNotPresentException::class.java)
            .hasMessage(String.format(EXCHANGE_PAIR_IS_NOT_PRESENT, BTC, USDT))
    }

    @Test
    fun swap_zeroBalance_shouldThrowNotEnoughCurrencyOnTheBalanceException() {

        assertThatThrownBy { tradingService.swap(
            wallet = USER2_ZERO_BALANCE_SWAP_WALLET,
            fromCurrency = BTC,
            fromCurrencyAmount = BigDecimal.ONE,
            toCurrency = USDT,
            exchange = BYBIT,
            passphrase = USER2_ZERO_BALANCE_SWAP_WALLET.passphrase
        ) }.isInstanceOf(NotEnoughCurrencyOnTheBalanceException::class.java)
            .hasMessage(String.format(ZERO_BALANCE_MESSAGE, BTC, BigDecimal.ONE))
    }

    @Test
    fun swap_notEnoughBalance_shouldThrowNotNotEnoughCurrencyOnTheBalanceException() {

        assertThatThrownBy { tradingService.swap(
            wallet = USER2_NOT_ENOUGH_BALANCE_SWAP_WALLET,
            fromCurrency = BTC,
            fromCurrencyAmount = BigDecimal.ONE,
            toCurrency = USDT,
            exchange = BYBIT,
            passphrase = USER2_NOT_ENOUGH_BALANCE_SWAP_WALLET.passphrase
        ) }.isInstanceOf(NotEnoughCurrencyOnTheBalanceException::class.java)
            .hasMessage(String.format(
                NOT_ENOUGH_CURRENCY_EXCEPTION_MESSAGE,
                BTC,
                USER2_NOT_ENOUGH_BALANCE_SWAP_WALLET.cryptoCurrencies[BTC],
                BigDecimal.ONE
            ))
    }

    @Test
    fun getAllExchanges_containsExchanges_shouldReturnExchanges() {

        val actual = tradingService.getAllExchanges()

        assertThat(actual).containsExactlyInAnyOrder(BYBIT, BINANCE)

    }
}