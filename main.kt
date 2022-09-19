package indigo

import java.util.Collections.addAll
import java.util.Collections.shuffle

fun main() { //Projeto 1, 2, 3 e 4
    val cardRanks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    val cardSuits = listOf("♦", "♥", "♠", "♣")
    val cardDeck = mutableListOf<String>()
    for (suit in cardSuits) {
        for (rank in cardRanks) {
            cardDeck.add(rank + suit)
        }
    }
    val playDeck = mutableListOf<String>()
    playDeck.addAll(cardDeck)
    shuffle(playDeck)
    println("Indigo Card Game")
    println("Play first? ")
    var order = readln()
    while (order.lowercase() != "yes" && order.lowercase() != "no") {
        println("Play first?")
        order = readln()
    }

    val cardsOnTable = mutableListOf<String>()
    val cardsDraw = mutableListOf<String>()
    val playerHand = mutableListOf<String>()
    val pcHand = mutableListOf<String>()
    val playerCards = mutableListOf<String>()
    val pcCards = mutableListOf<String>()
    var currentTurn = 0
    var wonLast = ""
    var gameOver = false
    var cardsPlayed = 4
    var playerPoints = 0
    var pcPoints = 0
    fun draw(cards: Int) {
        for (i in 0 until cards) {
            cardsDraw.add(i, playDeck[0])
            playDeck.removeAt(0)
        }
    }

    draw(4)
    cardsOnTable.addAll(cardsDraw)
    println("Initial cards on the table: ${cardsOnTable.joinToString(" ")}")
    cardsDraw.clear()

    fun boardState() {
        println(if (cardsOnTable.isEmpty()) "No cards on the table" else "${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable[cardsOnTable.lastIndex]}")
    }

    fun analisys(): Int {
        val cardToCompare = if (cardsOnTable.isEmpty()) "out" else cardsOnTable[cardsOnTable.lastIndex]
        val candidateCards5 = mutableListOf<String>()
        val candidateCards34shaped = pcHand.groupBy { it.last() }.filter { it.value.size > 1}.values
        val numfilter34 = if (candidateCards34shaped.isNotEmpty()) candidateCards34shaped.flatten().groupBy { it.first() }.filter { it.value.size > 1}.values else pcHand.groupBy { it.first() }.filter { it.value.size > 1}.values
        if (pcHand.size == 1) {
            return 0
        }
        for (card in pcHand) {
            if (card[card.lastIndex - 1] == cardToCompare[cardToCompare.lastIndex - 1] || card[card.lastIndex] == cardToCompare[cardToCompare.lastIndex]) {
                candidateCards5.add(card)
            }
        }
        val candidates5shaped = candidateCards5.toList().groupBy { it.last() }.filter { it.value.size > 1}.values
        val numfilter5 = if (candidates5shaped.isNotEmpty()) candidates5shaped.flatten().groupBy { it.first() }.filter { it.value.size > 1}.values else candidateCards5.groupBy { it.first() }.filter { it.value.size > 1}.values

        return when {
            numfilter5.isEmpty() -> {
                return when {
                    candidates5shaped.isEmpty() -> {
                        return when {
                            candidateCards5.isEmpty() -> {
                                return when {
                                    numfilter34.isEmpty() && candidateCards34shaped.isEmpty() -> 0
                                    numfilter34.isEmpty() && candidateCards34shaped.isNotEmpty() -> pcHand.indexOf(
                                        candidateCards34shaped.flatten()[0])
                                    numfilter34.isNotEmpty() -> pcHand.indexOf(numfilter34.flatten()[0])
                                    else -> 0
                                }
                            }
                            candidateCards5.isNotEmpty() -> pcHand.indexOf(candidateCards5[0])
                            else -> 0
                        }
                    }
                    candidates5shaped.isNotEmpty() -> pcHand.indexOf(candidates5shaped.flatten()[0])
                    else -> 0
                }
            }
            numfilter5.isNotEmpty() -> pcHand.indexOf(numfilter5.flatten()[0])
            else -> 0
        }
    }

    fun pcTurn(): String {
        println(pcHand.joinToString(" "))
        val idx = analisys()
        println("Computer plays ${pcHand[idx]}")
        cardsOnTable.add(pcHand[idx])
        val temp = pcHand[idx]
        pcHand.removeAt(idx)
        currentTurn = 2
        return temp
    }

    fun playerHandState() {
        print("Cards in hand:")
        for (i in 0 until playerHand.size) {
            print(" ${i + 1})${playerHand[i]}")
        }
        println()
        println("Choose a card to play (1-${playerHand.size}):")
    }

    fun playerTurn(card: String): String {
        if (card == "exit") {
            gameOver = true
        } else if (card.toIntOrNull() != null) {
            if (card.toInt() in 1..playerHand.size) {
                cardsOnTable.add(playerHand[card.toInt() - 1])
                val temp = playerHand[card.toInt() - 1]
                playerHand.removeAt(card.toInt() - 1)
                currentTurn = 1
                return temp
            } else {
                println("Choose a card to play (1-${playerHand.size}):")
                playerTurn(readln())
            }
        } else {
            println("Choose a card to play (1-${playerHand.size}):")
            playerTurn(readln())
        }
        return "this should not be reached"
    }

    fun points(cards: MutableList<String>): Int {
        val worth1 = listOf("A♦", "10♦", "J♦", "Q♦", "K♦", "A♥", "10♥", "J♥", "Q♥", "K♥", "A♠", "10♠", "J♠", "Q♠", "K♠", "A♣", "10♣", "J♣", "Q♣", "K♣")
        var currentPoints = 0
        for (card in cards) {
            if (worth1.contains(card)) {
                currentPoints++
            }
        }
        return currentPoints
    }


    fun isPoint(card: String) {
        if (cardsOnTable.size > 1) {
            val cardToCompare = cardsOnTable[cardsOnTable.lastIndex - 1]
            if (card[card.lastIndex - 1] == cardToCompare[cardToCompare.lastIndex - 1] || card[card.lastIndex] == cardToCompare[cardToCompare.lastIndex]) {
                when (currentTurn) {
                    1 -> {
                        playerCards.addAll(cardsOnTable)
                        cardsOnTable.clear()
                        playerPoints = points(playerCards)
                        wonLast = "player"
                        println("""
        Player wins cards
        Score: Player $playerPoints - Computer $pcPoints
        Cards: Player ${playerCards.size} - Computer ${pcCards.size}                    
                    """.trimIndent())
                    }
                    2 -> {
                        pcCards.addAll(cardsOnTable)
                        cardsOnTable.clear()
                        pcPoints = points(pcCards)
                        wonLast = "pc"
                        println("""
        Computer wins cards
        Score: Player $playerPoints - Computer $pcPoints
        Cards: Player ${playerCards.size} - Computer ${pcCards.size}                    
                    """.trimIndent())
                    }
                }
            }
        }
    }

    if (order == "yes") {
        boardState()
        draw(6)
        playerHand.addAll(cardsDraw)
        playerHandState()
        isPoint(playerTurn(readln()))
        cardsDraw.clear()
        cardsPlayed++
        currentTurn = 1
    } else {
        boardState()
        draw(6)
        pcHand.addAll(cardsDraw)
        isPoint(pcTurn())
        cardsDraw.clear()
        cardsPlayed++
        currentTurn = 2
    }

    while (cardsPlayed < 52 && !gameOver) {
        if (currentTurn == 1) {
            if (pcHand.isEmpty()) {
                draw(6)
                pcHand.addAll(cardsDraw)
                cardsDraw.clear()
            }
            boardState()
            isPoint(pcTurn())
            cardsPlayed++
        } else {
            if (playerHand.isEmpty()) {
                draw(6)
                playerHand.addAll(cardsDraw)
                cardsDraw.clear()
            }
            boardState()
            playerHandState()
            isPoint(playerTurn(readln()))
            cardsPlayed++
        }
    }

    if (!gameOver) {
        boardState()
        when (wonLast) {
            "player" -> {
                playerCards.addAll(cardsOnTable)
                cardsOnTable.clear()
                playerPoints = points(playerCards)
            }
            "pc" -> {
                pcCards.addAll(cardsOnTable)
                cardsOnTable.clear()
                pcPoints = points(pcCards)
            }
            "" -> {
                if (order == "yes") {
                    playerCards.addAll(cardsOnTable)
                    cardsOnTable.clear()
                    playerPoints = points(playerCards)
                } else {
                    pcCards.addAll(cardsOnTable)
                    cardsOnTable.clear()
                    pcPoints = points(pcCards)
                }
            }
        }
        println("""      
        Score: Player ${if (playerCards.size >= pcCards.size) playerPoints + 3 else playerPoints} - Computer ${if (playerCards.size < pcCards.size) pcPoints + 3 else pcPoints}        
        Cards: Player ${playerCards.size} - Computer ${pcCards.size}        
        """.trimIndent())
    }
    println("Game Over")
}
