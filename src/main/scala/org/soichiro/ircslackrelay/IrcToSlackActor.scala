package org.soichiro.ircslackrelay

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.sorcix.sirc.Channel
import org.soichiro.ircslackrelay.SlackClientActor.PostToSlack
import org.soichiro.ircslackrelay.StringModifier._

/**
 * command of start actor
 */
case class StartIrcToSlackActor()

/**
 * Actor of relay IRC to Slack
 */
class IrcToSlackActor(slackClientActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case StartIrcToSlackActor =>
      IrcClientProvider.ircClient.connect
      log.info("IrcToSlackActor Started.")
    case m: IrcMessage =>
      log.info(s"Messaged: ${m}")
      slackClientActor ! PostToSlack(createPostMessage(m), getSlackChannel(m.target))
    case n: IrcNotice =>
      log.info(s"Noticed: ${n}")
      slackClientActor ! PostToSlack(createPostMessage(n, true), getSlackChannel(n.target))
    case _ =>
      log.error("Not supported command.")
  }

  private def createPostMessage(command: IrcCommand, isSandUnderscores: Boolean = false): String = {
    if(isSandUnderscores) {
      sandUnderscores(formatMassage(command))
    } else {
      formatMassage(command)
    }
  }

  private def formatMassage(command: IrcCommand): String = {
    if(command.message.startsWith(NoNameCommand.commandPrefix)) {
      command.message.replace(NoNameCommand.commandPrefix, "")
    } else if (command.message.startsWith(PingCommand.commandPrefix)) {
      val pongMessage = s"pong, booted by ${System.getProperty("user.name")}"
      IrcClientProvider.ircClient.sendMessage(pongMessage, command.target.getName, log)
      pongMessage
    } else {
      s":${insertUnderScore(command.sender.getNick.toLowerCase)}: ${command.message}"
    }
  }

  private def getSlackChannel(ircChannel: Channel): String = {
    Config.relays.relayMapIrcToSlack(ircChannel.getName.toLowerCase)
  }
}

object IrcToSlackActor {

  def props(slackClientActor: ActorRef) = Props(new IrcToSlackActor(slackClientActor))
}