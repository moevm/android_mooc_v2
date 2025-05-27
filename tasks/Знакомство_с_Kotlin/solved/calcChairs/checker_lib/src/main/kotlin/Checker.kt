import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
abstract class Checker {

    abstract val calcChairsFun: (Int) -> Int

    @Test
    fun test1() {
        assertEquals(66, calcChairsFun(44))
        result += "2Ax9"
    }

    @Test
    fun test2() {
        assertEquals(105, calcChairsFun(70))
        result += "k4Pq"
    }

    @Test
    fun test3() {
        assertEquals(9, calcChairsFun(6))
        result += "mN5j"
    }

    @Test
    fun test4() {
        assertEquals(18, calcChairsFun(12))
        result += "7bWd"
    }

    @Test
    fun test5() {
        assertEquals(0, calcChairsFun(0))
        result += "tR8v"
    }

    @Test
    fun test6() {
        assertEquals(1, calcChairsFun(1))
        result += "3hKf"
    }

    @Test
    fun test7() {
        assertEquals(9, calcChairsFun(6))
        result += "cL6g"
    }

    @Test
    fun test8() {
        assertEquals(10, calcChairsFun(7))
        result += "9pYs"
    }

    @Test
    fun test9() {
        assertEquals(1500, calcChairsFun(1000))
        result += "4nMw"
    }

    @Test
    fun test10() {
        assertEquals(3, calcChairsFun(2))
        result += "dQ7x"
    }

    @Test
    fun test11() {
        assertEquals(4, calcChairsFun(3))
        result += "5jBz"
    }

    @Test
    fun test12() {
        assertEquals(75, calcChairsFun(50))
        result += "vH8y"
    }

    @Test
    fun test13() {
        assertEquals(76, calcChairsFun(51))
        result += "2fTm"
    }

    @Test
    fun test14() {
        assertEquals(7501, calcChairsFun(5001))
        result += "kP4w"
    }

    @Test
    fun test15() {
        assertEquals(7500, calcChairsFun(5000))
        result += "8sGn"
    }

    @Test
    fun test16() {
        assertEquals(6, calcChairsFun(4))
        result += "bW3h"
    }

    @Test
    fun test17() {
        assertEquals(7, calcChairsFun(5))
        result += "6mJx"
    }

    @Test
    fun test18() {
        assertEquals(3, calcChairsFun(2))
        result += "9cVq"
    }
    companion object {
        private var result = ""

        @JvmStatic
        @AfterClass
        fun tearDown() {
            println("CHECKER $result")
        }
    }
}