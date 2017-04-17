package io.scalac.slack.bots.bob

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import spray.http._
import spray.client.pipelining._

import scala.concurrent.Future

object PollApp {
  private val config = ConfigFactory load
  val token = config.getString("app.poll.token")
  val url = config.getString("app.poll.url")

  implicit val system = ActorSystem()
  import system.dispatcher

  def call(message: String, channel: String): Future[HttpResponse] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val formData = Map(
      "command" -> "/poll",
      "text" -> s"$message",
      "channel" -> s"$channel",
      "disp" -> "/poll",
      "token" -> token
    )

    pipeline(Post(url, FormData(formData)) ~> addHeader("Content-Type", "multipart/form-data"))
  }
}
