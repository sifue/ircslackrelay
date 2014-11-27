package org.soichiro.ircslackrelay

import akka.actor.{Actor, ActorLogging}
import StringModifier._
import IrcClientProvider._
import com.sorcix.sirc.Channel

/**
 * command of start actor
 */
case class StartSlackToIrcActor()

/**
 * Actor of relay Slack to Irc
 */
class SlackToIrcActor extends Actor with ActorLogging {
  lazy val slackIrcClient = new IrcClient(
    Config.slack.irc,
    "akka://Ircslackrelay/user/slackToIrcActor",
    Config.relays.slackChannels,
    Seq(Config.slack.api.username)
  )

  override def receive: Receive = {
    case StartSlackToIrcActor =>
      slackIrcClient.connect
      log.info("SlackToIrcActor Started.")
    case m: IrcMessage =>
      log.info(s"Messaged: ${m}")
      sendToIrc(m)
    case n: IrcNotice =>
      log.info(s"Noticed: ${n}")
      sendToIrc(n)
    case _ =>
      log.error("Not supported command.")
  }

  private def sendToIrc(c: IrcCommand): Unit = {
    if(isItalic(c.message)) {
      val message = getItalicString(c.message)
      ircClient.sendNotice(createPostMessage(c.sender.getNick, message, c.target), getIrcChannel(c.target), log)
    } else {
      ircClient.sendMessage(createPostMessage(c.sender.getNick, c.message, c.target), getIrcChannel(c.target), log)
    }
  }

  private def createPostMessage(nick: String, message: String, channel: Channel): String = {
    if(message.startsWith(NoNameCommand.commandPrefix)) {
      message.replace(NoNameCommand.commandPrefix, "")
    } else if (message.startsWith(PingCommand.commandPrefix)) {
      val pongMessage = s"pong, booted by ${System.getProperty("user.name")}"
      SlackClient.postMessage(pongMessage, channel.getName, log)
      pongMessage
    } else {
      s"(${insertSpace(nick)}) ${message}"
    }
  }

  private def getIrcChannel(slackChannel: Channel): String = {
    Config.relays.relayMapSlackToIrc(slackChannel.getName.toLowerCase)
  }

  val passwordRegex = "_([^_]+)_".r

  private def isItalic(s: String): Boolean = {
    s match  {
      case passwordRegex(_) => true
      case _ => false
    }
  }

  private def getItalicString(s: String): String = {
    s match {
      case passwordRegex(extracted) => extracted
      case _ => s
    }
  }
}