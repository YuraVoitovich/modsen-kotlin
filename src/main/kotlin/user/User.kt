package user

import wallet.Wallet
import java.util.*

data class User(val id: UUID, var email: String?, var fullName: String, var status: UserStatus) {


    private val walls = mutableSetOf<Wallet>()
    var wallets
        get() = walls.filter { !it.isCold }.toMutableSet()
        set(value) { walls.addAll(value) }

    init {
        if (wallets.isEmpty()) {
            walls.add(Wallet.getDefaultWallet(this))
        }
    }

    constructor(email: String, fullName: String) : this(
        UUID.randomUUID(),
        email,
        fullName,
        UserStatus.NEW
    )

    constructor(email: String, fullName: String, status: UserStatus) : this(
        UUID.randomUUID(),
        email,
        fullName,
        status
    )

    fun transformEmail(): String {
        return email?.let {
            val atIndex = it.indexOf('@')
            if (atIndex != -1) {
                it.substring(0, atIndex).uppercase()
            } else {
                it.uppercase()
            }
        } ?: ""
    }

}


val User.numWallets: Int
    get() = wallets.size