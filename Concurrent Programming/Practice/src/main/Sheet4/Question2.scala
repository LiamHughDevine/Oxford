import ox.scl._

class Question2 {
    private val lock = new Lock
    private val manEnter, womanEnter, manLeave = lock.newCondition
    private var manCanEnter = true
    private var womanCanEnter = false
    private var manCanLeave = false

    private var manName = ""
    private var womanName = ""

    def manSync(me: String) = lock.mutex {
        manEnter.await(manCanEnter)
        manCanEnter = false
        manName = me
        womanCanEnter = true
        womanEnter.signal()
        manLeave.await(manCanLeave)
        manCanLeave = false
        val result = womanName
        manCanEnter = true
        manEnter.signal()
        result
    }

    def womanSync(me: String) = lock.mutex {
        womanEnter.await(womanCanEnter)
        womanCanEnter = false
        val result = manName
        womanName = me
        manCanLeave = true
        manLeave.signal()
        result
    }
}
