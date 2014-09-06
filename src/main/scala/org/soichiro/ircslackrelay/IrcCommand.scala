package org.soichiro.ircslackrelay

import com.sorcix.sirc.{Channel, User, IrcConnection}

/**
 * Command of IRC
 */
sealed trait IrcCommand extends Serializable{
  val irc: IrcConnection
  val sender: User
  val target: Channel
  val message: String
}

/**
 * Message of Irc for Actor
 * @param irc
 * @param sender
 * @param target
 * @param message
 */
case class IrcMessage(irc: IrcConnection, sender: User, target: Channel, message: String) extends IrcCommand

/**
 * Notice of Irc for Actor
 * @param irc
 * @param sender
 * @param target
 * @param message
 */
case class IrcNotice(irc: IrcConnection, sender: User, target: Channel, message: String) extends IrcCommand
