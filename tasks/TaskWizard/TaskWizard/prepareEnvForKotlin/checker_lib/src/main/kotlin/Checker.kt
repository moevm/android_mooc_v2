import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
abstract class Checker {

    abstract val mainFun: () -> Unit

    companion object {
        private var result = ""

        @JvmStatic
        @AfterClass
        fun tearDown() {
            println("CHECKER $result")
        }
    }
}