package org.soichiro.ircslackrelay

import java.util.Date

import akka.event.LoggingAdapter
import com.sorcix.sirc.User

import java.nio.charset.Charset

import com.sorcix.sirc.{Channel, IrcConnection, IrcAdaptor}

/**
 * IRC client
 *
 * @param conf
 * @param callbackActorPath
 * @param channels
 * @param ignoreNicks
 */
class IrcClient(conf: IrcClientConfig,
                callbackActorPath: String,
                channels: Seq[String],
                ignoreNicks: Seq[String] ) extends IrcAdaptor {
  val lowerCaseChannels = channels.map(_.toLowerCase)
  val irc = new IrcConnection(conf.address, conf.port, conf.password)
  irc.setCharset(Charset.forName(conf.charset))
  irc.setNick(conf.nickname)
  irc.setUsingSSL(conf.useSsl)
  irc.setUsername(conf.username)
  irc.addServerListener(this)
  irc.addMessageListener(this)

  def connect: Unit = {
    if(conf.useSsl) {
      irc.connect(IgnoreSSLContextProvider.getIgnoreSSLContext)
    } else {
      irc.connect()
    }
  }

  override def onMessage(irc: IrcConnection, sender: User, target: Channel, message: String) = {
    if(lowerCaseChannels.contains(target.getName.toLowerCase) && !ignoreNicks.contains(sender.getNick)) {
      ActorSystemProvider.system.actorSelection(callbackActorPath) ! IrcMessage(irc, sender, target, message)
    }
  }

  override def onNotice(irc: IrcConnection, sender: User, target: Channel, message: String) = {
    if(lowerCaseChannels.contains(target.getName.toLowerCase) && !ignoreNicks.contains(sender.getNick)) {
      ActorSystemProvider.system.actorSelection(callbackActorPath) ! IrcNotice(irc, sender, target, message)
    }
  }

  override def onConnect(irc: IrcConnection) = {
    channels.foreach{irc.createChannel(_).join()}
  }

  val reconnectWaitMilliSec = 3000
  override def onDisconnect(irc: IrcConnection) = {
    connect
    System.err.println(
      s"Unexpected disconnection and reconnection. (${conf.address}:${conf.port.toString}) at ${new Date().toString}")
    while (!irc.isConnected) {
      Thread.sleep(reconnectWaitMilliSec)
      connect
      System.err.println(
        s"Unexpected disconnection and retry reconnection. (${conf.address}:${conf.port.toString}) at ${new Date().toString}")
    }
  }

  override def onKick(irc: IrcConnection, channel: Channel, sender: User, user: User, message: String) = {
    irc.createChannel(channel.getName.toLowerCase).join()
  }

  val GroupedCharCount = 400

  /**
   * Send private message
   * @param message
   * @param channelName
   * @param log
   */
  def sendMessage(message: String, channelName: String, log: LoggingAdapter) = {
    message.grouped(GroupedCharCount).foreach(s => irc.createChannel(channelName).send(s.trim + " "))
    log.info(s"IrcClient post message. channel:${channelName} message:${message}")
  }

  /**
   * Send notice
   * @param notice
   * @param channelName
   * @param log
   */
  def sendNotice(notice: String, channelName: String, log: LoggingAdapter) = {
    notice.grouped(GroupedCharCount).foreach(s => irc.createChannel(channelName).sendNotice(s.trim + " "))
    log.info(s"IrcClient post notice. channel:${channelName} message:${notice}")
  }

}
