import ox.scl._

class Question4 {
    private val enterSem = new Semaphore(true)
    private val mutex = new Semaphore(true)

    private var total = 0
    def enter(id: Int) = {
        enterSem.down
        mutex.down
        total += id
        if (total % 3 == 0) enterSem.up
        mutex.up
    }

    def exit(id: Int) = {
        mutex.down
        total -= id
        if (total % 3 == 0 && id % 3 != 0) enterSem.up
        mutex.up
    }
}
