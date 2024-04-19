import ox.scl._

@main
def main(): Unit = {
  test3()
}

def test3() : Unit = {
  val a = Array(2, 4, 2, 5, -1, 6, 0, 3, 1, 10)
  val q = new Question3(10, a)
  println(a.toList)
  println(q.apply().toList)
}

/*def test2() : Unit = {
  val q = new Question6()
  def men = || (for (i <- 0 until 20) yield thread { val result = q.manSync("Bob" + i); println("Bob" + i + " / " + result)})
  def women = || (for (i <- 0 until 20) yield thread { val result = q.womanSync("Sue" + i); println("Sue" + i + " / " + result)})
  run(men || women)
}*/