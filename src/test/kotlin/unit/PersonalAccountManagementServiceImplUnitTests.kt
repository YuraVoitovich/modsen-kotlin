package unit

import currency.CryptoCurrency
import exchange.Exchange
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import service.PersonalAccountManagementService
import service.impl.PersonalAccountManagementServiceImpl
import transaction.TradeTransaction
import transaction.Transaction
import user.User
import user.UserStatus
import wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class PersonalAccountManagementServiceImplUnitTests {


    companion object {

        lateinit var accountService: PersonalAccountManagementService

        @JvmStatic
        @BeforeAll
        fun setup() {
            accountService = PersonalAccountManagementServiceImpl()
        }



        val BTC = CryptoCurrency("BTC")
        val BINANCE = Exchange("Binance")
        val USER1 = User("email1", "fullName1", UserStatus.APPROVED)
        val USER2 = User("email1", "fullName1", UserStatus.APPROVED)
        val USER3 = User("email1", "fullName1", UserStatus.NEW)

        val USER1_EMPTY_WALLET = Wallet("USER1_EMPTY_WALLET", "pass1", USER1)
        val USER1_ENOUGH_BALANCE_WALLET = Wallet("USER1_ENOUGH_BALANCE_WALLET", "pass2", USER1)
        val USER2_EMPTY_WALLET = Wallet("USER2_EMPTY_WALLET", "pass1", USER2)
        val USER2_NOT_ENOUGH_BALANCE_SWAP_WALLET = Wallet("USER2_NOT_ENOUGH_BALANCE_SWAP_WALLET", "pass2", USER2)
        val USER2_ZERO_BALANCE_SWAP_WALLET = Wallet("USER2_ZERO_BALANCE_SWAP_WALLET", "pass2", USER2)
        val USER2_ENOUGH_BALANCE_SWAP_WALLET = Wallet("USER2_ENOUGH_BALANCE_SWAP_WALLET", "pass2", USER2)
        val USER3_WALLET = Wallet("USER3_WALLET", "pass3", USER3)
    }

    @Test
    fun getAllExchangeTransactions_transactionExists_shouldReturnAllExchangeTransactions() {

        val expected = mutableListOf<Transaction>()
        expected.add(
            TradeTransaction(
            id = UUID.randomUUID(),
            date = LocalDateTime.of(2024, 11, 3, 1, 0),
            initiator = USER1_EMPTY_WALLET,
            fromCurrency = BTC,
            fromAmount = BigDecimal.ONE,
            receiver = USER2_EMPTY_WALLET,
            toCurrency = BTC,
            toAmount = BigDecimal.ONE
            )
        )

        expected.add(
            TradeTransaction(
                id = UUID.randomUUID(),
                date = LocalDateTime.of(2025, 11, 3, 1, 0),
                initiator = USER1_EMPTY_WALLET,
                fromCurrency = BTC,
                fromAmount = BigDecimal.ONE,
                receiver = USER2_EMPTY_WALLET,
                toCurrency = BTC,
                toAmount = BigDecimal.ONE
            )
        )

        BINANCE.transactionHistory.addAll(expected)
        BINANCE.transactionHistory.add(
            TradeTransaction(
                id = UUID.randomUUID(),
                date = LocalDateTime.of(2026, 11, 3, 1, 0),
                initiator = USER1_EMPTY_WALLET,
                fromCurrency = BTC,
                fromAmount = BigDecimal.ONE,
                receiver = USER2_EMPTY_WALLET,
                toCurrency = BTC,
                toAmount = BigDecimal.ONE
            )
        )

        val actual = accountService.getAllExchangeTransactions(
            USER1,
            BINANCE,
            LocalDateTime.of(2012, 1, 1, 1, 1),
            LocalDateTime.of(2025, 11, 12, 1, 1)
        )

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)

    }

}