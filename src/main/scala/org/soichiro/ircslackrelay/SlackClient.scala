package org.soichiro.ircslackrelay

import java.util.concurrent.Semaphore
import akka.event.LoggingAdapter
import dispatch._
import dispatch.Defaults._

/**
 * Client of Slack
 *
 */
class SlackClient {
  val conf = Config.slack.api

  /**
   * post message to Slack
   *
   * @param message
   * @param channelName
   * @param log
   */
  def postMessage(message:String, channelName:String, log: LoggingAdapter): Unit = {
    try {
      SlackClient.available.acquire();
      val request = url("https://slack.com/api/chat.postMessage")
      val requestWithParameters = request
        .POST
        .addParameter("token", conf.token)
        .addParameter("channel", channelName)
        .addParameter("text", message)
        .addParameter("username", conf.username)
        .addParameter("icon_url", conf.iconUrl)
        .secure
      val response = Http(requestWithParameters OK as.String).apply()
      if(response.contains("\"ok\":false")) {
        log.warning(s"SlackClient response is not ok. response:${response} channel:${channelName} message:${message}")
      } else {
        log.info(s"SlackClient post message. channel:${channelName} message:${message}")
      }
      Thread.sleep(SlackClient.apiLimitWaitMilliSec)
    } catch {
      case e: Throwable => log.error(e, "SlackClient caught Throwable.")
    } finally  {
      SlackClient.available.release()
    }
  }
}

object SlackClient extends SlackClient {
  val available = new Semaphore(1, true)
  val apiLimitWaitMilliSec = 1000L
}
