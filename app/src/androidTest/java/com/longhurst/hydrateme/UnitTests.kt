package com.longhurst.hydrateme

import com.longhurst.hydrateme.data.amountByWeight
import com.longhurst.hydrateme.data.amountofDrinks
import com.longhurst.hydrateme.data.drinksFromExercise
import org.junit.Test

class UnitTests {
    @Test
    fun lowWeight(){
        assert(amountByWeight(10F) == 5)

        assert(amountofDrinks(5) == 1)

        assert(drinksFromExercise(4) == 5)
    }

    @Test
    fun highWeight(){
        assert(amountByWeight(100000F) == 50000)

        assert(amountofDrinks(50000) == 6250)

        assert(drinksFromExercise(4) == 6262)
    }

    @Test
    fun averageWeight(){
        assert(amountByWeight(190F) == 95)

        assert(amountofDrinks(95) == 12)

        assert(drinksFromExercise(4) == 24)
    }

    @Test
    fun longExertion(){
        assert(amountofDrinks(88) == 11)
        assert(drinksFromExercise(50) == 161)
    }

    @Test
    fun shortExertion(){
        assert(amountofDrinks(95) == 12)

        assert(drinksFromExercise(0) == 12)
    }

    @Test
    fun lotsOfWater(){
        assert(amountofDrinks(900000000) == 112500000)
    }
}