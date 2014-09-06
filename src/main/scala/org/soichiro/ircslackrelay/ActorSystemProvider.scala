package org.soichiro.ircslackrelay

import akka.actor.ActorSystem

/**
 * Singleton for ActorSystem
 */
object ActorSystemProvider {
  val system = ActorSystem("Ircslackrelay")
}
