import ox.scl._

class Question6 {
    private val manEnter = new Semaphore(true)
    private val womanEnter = new Semaphore(false)
    private val manLeave = new Semaphore(false)
    
    private var manName = ""
    private var womanName = ""
    
    def manSync(me: String) : String = {
        manEnter.down
        manName = me
        womanEnter.up
        manLeave.down
        val result = womanName
        manEnter.up
        result
    }
    
    def womanSync(me: String) : String = {
        womanEnter.down
        val result = manName
        womanName = me
        manLeave.up
        result
    }
}
