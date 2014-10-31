package models

import scala.util.Random

/**
 * Author: Dmytro Bischak
 * Date: 30.10.2014
 */
object CodeGenerator {

  val MAX_NUMBER = 999999

  def newCode: Int = math.abs(Random.nextInt() % MAX_NUMBER)

}
