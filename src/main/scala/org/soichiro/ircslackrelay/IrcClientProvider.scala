package org.soichiro.ircslackrelay

/**
 * Provider of IrcClient
 */
object IrcClientProvider {
  lazy val ircClient = new IrcClient(
    Config.irc,
    "akka://Ircslackrelay/user/ircToSlackActor",
    Config.relays.ircChannels,
    Seq(Config.irc.nickname)
  )
}
