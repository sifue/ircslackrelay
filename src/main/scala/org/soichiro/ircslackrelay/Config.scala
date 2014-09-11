package org.soichiro.ircslackrelay


import java.io.File
import java.util

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * Configuration singleton
 */
object Config {
  private val internalConf = ConfigFactory.parseFile(new File("./ircslackrelay.conf"))
  def irc = Irc
  object Irc extends IrcClientConfig {
      protected val ircConf: com.typesafe.config.Config = internalConf.getConfig("irc")
  }

  def slack = Slack
  object Slack {
    private val slackConf: com.typesafe.config.Config = internalConf.getConfig("slack")

    def irc = Irc
    object Irc extends IrcClientConfig {
      protected val ircConf: com.typesafe.config.Config = slackConf.getConfig("irc")
    }

    def api = Api
    object Api {
      private val apiConf: com.typesafe.config.Config = slackConf.getConfig("api")
      val username: String = apiConf.getString("username")
      val token: String = apiConf.getString("token")
      val iconUrl: String = apiConf.getString("icon_url")
    }

  }

  def relays = Relays
  object Relays {
    private val relaysConf: util.List[_ <: com.typesafe.config.Config] = internalConf.getConfigList("relays")
    private val relays: mutable.Buffer[Relay] = relaysConf.asScala.map(c => Relay(c.getString("irc_channel"), c.getString("slack_channel")))
    val relaySeq: Seq[Relay] = relays.toSeq.map(r => Relay(r.ircChannel.toLowerCase, r.slackChannel.toLowerCase))
    val ircChannels: Seq[String] = relaySeq.map(_.ircChannel)
    val slackChannels: Seq[String] = relaySeq.map(_.slackChannel)
    val relayMapIrcToSlack: Map[String, String] = relaySeq.map(r => r.ircChannel -> r.slackChannel).toMap
    val relayMapSlackToIrc: Map[String, String] = relaySeq.map(r => r.slackChannel -> r.ircChannel).toMap
  }
}

/**
 * Config for IrcClient
 */
trait IrcClientConfig {
  protected val ircConf: com.typesafe.config.Config
  def address = ircConf.getString("address")
  def nickname: String = ircConf.getString("nickname")
  def username: String = ircConf.getString("username")
  def password: String = ircConf.getString("password")
  def port: Int = ircConf.getInt("port")
  def useSsl: Boolean = ircConf.getBoolean("use_ssl")
  def charset: String = ircConf.getString("charset")
}

/**
 * Two way relay between IRC and Slack
 * @param ircChannel
 * @param slackChannel
 */
case class Relay(ircChannel: String, slackChannel: String)