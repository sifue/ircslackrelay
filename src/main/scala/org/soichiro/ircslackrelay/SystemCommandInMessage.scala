package org.soichiro.ircslackrelay

/**
 * System command for ircslacrelay in Massage
 */
sealed trait SystemCommandInMessage {
  def commandPrefix: String
}

/**
 * Remove original author name
 */
object NoNameCommand extends SystemCommandInMessage {
  override def commandPrefix: String = "noname> "
}

/**
 * Return pong and program name
 */
object PingCommand extends SystemCommandInMessage {
  override def commandPrefix: String = "ircslackrelayping>"
}