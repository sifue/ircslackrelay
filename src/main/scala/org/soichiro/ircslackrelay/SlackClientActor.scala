package org.soichiro.ircslackrelay

import akka.actor.{Actor, ActorLogging, Props}

import scala.collection.immutable.Queue
import scala.concurrent.duration._

/**
 * An actor dealing with slack client
 */
class SlackClientActor(slackClient: SlackClient, apiInterval: FiniteDuration) extends Actor with ActorLogging {

  import org.soichiro.ircslackrelay.SlackClientActor._
  import context._

  override def receive: Receive = idling()

  private def idling(): Receive = {
    case PostToSlack(message, channelName) =>
      slackClient.postMessage(message, channelName, log)
      context.become(running())
      scheduleAwake()
  }

  private def running(queue: Queue[PostToSlack] = Queue.empty): Receive = {
    case post@PostToSlack(message, channelName) =>
      context.become(running(queue.enqueue(post)))

    case Awoke =>
      queue.dequeueOption match {
        case Some((PostToSlack(message, channelName), newQueue)) =>
          slackClient.postMessage(message, channelName, log)
          context.become(running(newQueue))
          scheduleAwake()
        case _ =>
          context.become(idling())
      }
  }

  private def scheduleAwake(): Unit = {
    system.scheduler.scheduleOnce(apiInterval, self, Awoke)
  }
}

object SlackClientActor {

  val apiLimitWaitMilliSec = 1 second

  def props = Props(new SlackClientActor(new SlackClientImpl, apiLimitWaitMilliSec))

  case class PostToSlack(message:String, channelName:String)

  case object Awoke
}
