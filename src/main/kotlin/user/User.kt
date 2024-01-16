package user

import wallet.Wallet
import java.util.*

class User(val id: UUID, var email: String, var fullName: String, var status: UserStatus) {


    private val walls = mutableSetOf<Wallet>()
    var wallets
        get() = walls.filter { !it.isCold }.toMutableSet()
        set(value) { walls.addAll(value) }

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

}