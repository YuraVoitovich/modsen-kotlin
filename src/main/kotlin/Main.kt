import currency.CryptoCurrency
import transaction.Transaction
import user.User
import user.UserStatus
import wallet.Wallet
import java.math.BigDecimal
import java.util.*
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    userDestructuring()
    transactionDestructuring()
    task10()
    println(fibonacciSum(10))
}

fun transactionDestructuring() {

    val user = User("user@example.com", "John Doe", UserStatus.BLOCKED)
    val transaction = Transaction(
        initiator = Wallet(name = "InitiatorWallet", passphrase = "string", user = user),
        fromCurrency = CryptoCurrency("BTC"),
        fromAmount = BigDecimal("10.0")
    )

    val (id, date, initiator, fromCurrency, fromAmount) = transaction

    println("Transaction ID: $id")
    println("Transaction Date: $date")
    println("Transaction Initiator: ${initiator.name}")
    println("Transaction From Currency: ${fromCurrency.name}")
    println("Transaction From Amount: $fromAmount")
}

fun userDestructuring() {
    val user = User("user@example.com", "John Doe", UserStatus.BLOCKED)
    val (id, email, _, status) = user

    println("User ID: $id")
    println("User email: $email")
    println("User status: $status")

}

fun filterUsers() {
    val users = listOf(
        User("user1@example.com", "John Doe", UserStatus.BLOCKED),
        User( "user2@example.com", "Jane Doe", UserStatus.BLOCKED),
        User( "user3@example.com", "Bob Smith", UserStatus.BLOCKED)
    )

    val filteredUsers = users.filter { it.wallets.size > 2 }

    val idToUserMap = filteredUsers.map { it.id to it }
    val userToStatusMap = filteredUsers.map { it to it.status }

    println("Users with more than 2 wallets:")
    filteredUsers.forEach {
        println("User id: ${it.id}, email: ${it.email}, fullName: ${it.fullName}, status: ${it.status}")
    }
}

fun task10() {
    val users = generateUsers(10000)

    val normalListTime = measureTimeMillis {
        val resultNormalList = users
            .filter { it.status == UserStatus.APPROVED }
            .map { it.fullName }
            .any { it.startsWith("A", ignoreCase = true) }
        println("Normal List Result: $resultNormalList")
    }
    println("Time taken for normal list: $normalListTime ms")

    val sequenceTime = measureTimeMillis {
        val resultSequence = users.asSequence()
            .filter { it.status == UserStatus.APPROVED }
            .map { it.fullName }
            .any { it.startsWith("A", ignoreCase = true) }
        println("Sequence Result: $resultSequence")
    }
    println("Time taken for sequence: $sequenceTime ms")
}

fun generateUsers(count: Int): List<User> {
    return (1..count).map {
        User("User$it", fullName = generateRandomName(), getRandomStatus())
    }
}

fun getRandomStatus(): UserStatus {
    return if (Random().nextBoolean()) UserStatus.APPROVED else UserStatus.BLOCKED
}

fun generateRandomName(): String {
    val prefixes = listOf("Alice", "Bob", "Charlie", "David", "Eva", "Frank")
    val suffix = Random().nextInt(1000)
    return "${prefixes.random()}_$suffix"
}

fun fibonacciSum(n: Int): Int {
    return when (n) {
        0 -> 0
        1 -> 1
        else -> fibonacciSum(n - 1) + fibonacciSum(n - 2)
    }
}