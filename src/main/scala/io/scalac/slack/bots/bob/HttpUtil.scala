package io.scalac.slack.bots.bob

import akka.actor.ActorSystem
import spray.http._
import spray.client.pipelining._

import scala.concurrent.Future

object HttpUtil {
  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  def callSlackPollApp(message: String, channel: String): Future[HttpResponse] = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val formData = Map(
      "command" -> "/poll",
      "text" -> s"$message",
      "channel" -> s"$channel",
      "disp" -> "/poll",
      "token" -> "xoxs-167637290244-167597978946-168748619766-15531f2e1b"
    )

    pipeline(Post("https://whatsuplegs.slack.com/api/chat.command?_x_id=ad4ccaf0-1491978239.985", FormData(formData)) ~> addHeader("Content-Type", "multipart/form-data"))
  }
}
