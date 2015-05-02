package org.soichiro.ircslackrelay

import akka.actor.FSM
import akka.actor.{ActorLogging, Props}
import org.soichiro.ircslackrelay.SlackClientActor._

import scala.collection.immutable.Queue
import scala.concurrent.duration._

/**
 * An actor dealing with slack client as Finite State Machine(FSM)
 */
class SlackClientActor(slackClient: SlackClient, apiInterval: FiniteDuration)
  extends FSM[State, Data] with ActorLogging {

  import org.soichiro.ircslackrelay.SlackClientActor._
  import context._

  startWith(Idling, Uninitialized)

  when(Idling) {
    case Event(PostToSlack(message, channelName), Uninitialized) =>
      slackClient.postMessage(message, channelName, log)
      scheduleAwake()
      goto(Running) using PostQueue(Queue.empty)
  }

  when(Running) {
    case Event(post: PostToSlack, pq: PostQueue) =>
      stay using pq.copy(pq.queue.enqueue(post))
    case Event(Awoke, pq: PostQueue) =>
      pq.queue.dequeueOption match {
        case Some((PostToSlack(message, channelName), newQueue)) =>
          slackClient.postMessage(message, channelName, log)
          scheduleAwake()
          stay using PostQueue(newQueue)
        case _ =>
          goto(Idling) using Uninitialized
      }
  }

  private def scheduleAwake(): Unit = {
    system.scheduler.scheduleOnce(apiInterval, self, Awoke)
  }

  initialize()
}

object SlackClientActor {

  val apiLimitWaitMilliSec = 1 second

  def props = Props(new SlackClientActor(new SlackClientImpl, apiLimitWaitMilliSec))

  sealed trait State

  case object Idling extends State

  case object Running extends State

  sealed trait Data

  case object Uninitialized extends Data

  case class PostQueue(queue: Queue[PostToSlack]) extends Data

  sealed trait Message

  case class PostToSlack(message:String, channelName:String) extends Message

  case object Awoke extends Message
}
