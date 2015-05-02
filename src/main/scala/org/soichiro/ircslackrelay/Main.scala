package org.soichiro.ircslackrelay

import ActorSystemProvider._

/**
 * Main application singleton
 */
object Main extends App {

  val slackClientActor = system.actorOf(SlackClientActor.props, "slackClientActor")

  val ircToSlackActor = system.actorOf(IrcToSlackActor.props(slackClientActor), name = "ircToSlackActor")
  ircToSlackActor ! StartIrcToSlackActor

  val slackToIrcActor = system.actorOf(SlackToIrcActor.props(slackClientActor), name = "slackToIrcActor")
  slackToIrcActor ! StartSlackToIrcActor

}

