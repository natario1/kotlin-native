import library.*
import kotlinx.cinterop.*
import kotlin.test.assertNotNull

fun main() {
    assertNotNull(s.ptr)
    assertNotNull(u.ptr)
    assertNotNull(array)
}