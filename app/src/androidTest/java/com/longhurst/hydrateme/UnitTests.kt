package com.longhurst.hydrateme

import com.longhurst.hydrateme.data.amountByWeight
import com.longhurst.hydrateme.data.amountofDrinks
import com.longhurst.hydrateme.data.drinksFromExercise
import org.junit.Test

class UnitTests {
    @Test
    fun lowWeight(){
        assert(amountByWeight(10F) == 5F)

        assert(amountofDrinks(5F) == 1F)

        assert(drinksFromExercise(4F) == 12F)
    }

    @Test
    fun highWeight(){
        assert(amountByWeight(100000F) == 50000F)

        assert(amountofDrinks(50000F) == 6250F)

        assert(drinksFromExercise(4F) == 12F)
    }

    @Test
    fun averageWeight(){
        assert(amountByWeight(190F) == 95F)

        assert(amountofDrinks(95F) == 12F)

        assert(drinksFromExercise(4F) == 12F)
    }

    @Test
    fun longExertion(){
        assert(amountofDrinks(88F) == 11F)
        assert(drinksFromExercise(50F) == 150F)
    }

    @Test
    fun shortExertion(){
        assert(amountofDrinks(95F) == 12F)

        assert(drinksFromExercise(0F) == 0F)
    }

    @Test
    fun lotsOfWater(){
        assert(amountofDrinks(900000000F) == 112500000F)
    }
}