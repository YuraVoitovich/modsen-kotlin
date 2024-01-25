package service.impl

import exchange.Exchange
import service.PersonalAccountManagementService
import transaction.Transaction
import user.User
import wallet.Wallet
import java.time.LocalDateTime

class PersonalAccountManagementServiceImpl : PersonalAccountManagementService {
    override fun addNewWallet(user: User, wallet: Wallet) {
        user.wallets.add(wallet)
    }

    override fun printAllWalletsBalance(user: User) {
        println(user.wallets.getWalletsInfo())
    }

    override fun printWalletsBalance(vararg wallets: Wallet) {
        for (wallet in wallets) {
            println(wallet.toString())
        }
    }

    override fun getAllExchangeTransactions(
        user: User,
        exchange: Exchange,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) : List<Transaction> {
        return exchange.transactionHistory
            .filter { transaction -> transaction.initiator.user.id == user.id
                    && transaction.date.isInRange(startDate, endDate)}
    }
}

private fun LocalDateTime.isInRange(startDate: LocalDateTime, endDate: LocalDateTime): Boolean {
    return this.isAfter(startDate) && this.isBefore(endDate)
}

private fun <E> MutableSet<E>.getWalletsInfo(): String {
    return this.map { it to it.toString() }.joinToString("/n")
}

