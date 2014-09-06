package org.soichiro.ircslackrelay

import akka.actor.Props
import ActorSystemProvider._

/**
 * Main application singleton
 */
object Main extends App {

  val ircToSlackActor = system.actorOf(Props[IrcToSlackActor], name = "ircToSlackActor")
  ircToSlackActor ! StartIrcToSlackActor

  val slackToIrcActor = system.actorOf(Props[SlackToIrcActor], name = "slackToIrcActor")
  slackToIrcActor ! StartSlackToIrcActor

}

