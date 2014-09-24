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
      // Default notice
      ircClient.sendMessage(createPostMessage(m), getIrcChannel(m.target), log)
    case n: IrcNotice =>
      log.info(s"Noticed: ${n}")
      ircClient.sendMessage(createPostMessage(n), getIrcChannel(n.target), log)
    case _ =>
      log.error("Not supported command.")
  }

  private def createPostMessage(command: IrcCommand): String = {
    s"${insertSpace(command.sender.getNick)}: ${command.message}"
  }

  private def getIrcChannel(slackChannel: Channel): String = {
    Config.relays.relayMapSlackToIrc(slackChannel.getName.toLowerCase)
  }
}