package service

import exchange.Exchange
import transaction.Transaction
import user.User
import wallet.Wallet
import java.time.LocalDateTime

interface PersonalAccountManagementService {

    fun addNewWallet(user: User, wallet: Wallet)

    fun printAllWalletsBalance(user: User)

    fun printWalletsBalance(vararg wallets: Wallet)

    fun getAllExchangeTransactions(
        user: User,
        exchange: Exchange,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) : List<Transaction>
}