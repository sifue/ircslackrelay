package org.soichiro.ircslackrelay

/**
 * Size zero space insert 
 */
object StringModifier {

  /**
   * insert size zero space to string
   * @param string
   * @return
   */
  def insertSpace(string: String):String = {
    if (string.size > 1) {
      string.head + "\u200B" + string.tail
    } else {
      string
    }
  }

  /**
   * Sand underscores
   * @param string
   * @return
   */
  def sandUnderscores(string: String): String = {
    s"_${string}_"
  }
}
