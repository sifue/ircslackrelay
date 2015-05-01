package org.soichiro.ircslackrelay

import akka.event.LoggingAdapter
import dispatch._
import dispatch.Defaults._

/**
 * Client of Slack
 *
 */
trait SlackClient {

  /**
   * post message to Slack
   *
   * @param message
   * @param channelName
   * @param log
   */
  def postMessage(message:String, channelName:String, log: LoggingAdapter): Unit
}

class SlackClientImpl extends SlackClient {
  val conf = Config.slack.api

  override def postMessage(message:String, channelName:String, log: LoggingAdapter): Unit = {
    try {
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
    } catch {
      case e: Throwable => log.error(e, "SlackClient caught Throwable.")
    }
  }
}
