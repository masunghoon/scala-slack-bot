package io.scalac.slack.bots.bob

import java.io._

import io.scalac.slack.MessageEventBus
import io.scalac.slack.bots.AbstractBot
import io.scalac.slack.common._

import scala.io.Source
import scala.util.Random

case class Restaurant(name: String)

/**
  * Maintainer: Sunghoon
  */
class BobBot(override val bus: MessageEventBus) extends AbstractBot {
  val file = new File("src/main/resources/restaurants.csv")

  def restaurants: Vector[Restaurant] = {
    val inputStream = new FileInputStream(file)
    val lines: Iterator[String] = Source.fromInputStream(inputStream).getLines
    lines.toVector.map(Restaurant)
  }

  override def act: Receive = {
    case Command("점심", _, message) =>
      publish(OutboundMessage(message.channel, s"${Random.shuffle(restaurants).head.name}?!"))

    case Command("list", _, message) =>
      publish(OutboundMessage(message.channel, restaurants.map(_.name.trim).mkString(", ")))

    case Command("add", params, message) =>
      val name = params.mkString(" ")

      if (restaurants.exists(_.name == name)) publish(OutboundMessage(message.channel, s"$name already exists."))
      else {
        val outputStream = new FileOutputStream(file, true)
        val writer = new PrintWriter(outputStream)
        writer.println(name)
        writer.close()
        publish(OutboundMessage(message.channel, s"$name added"))
      }

    case Command("poll", _, message) =>
      val pollMsg = s"""'점심 어디서?' ${Random.shuffle(restaurants).take(4).map(_.name.replaceAll(" ", "")).mkString("'","' '","'")}"""
      PollApp.call(pollMsg, message.channel)

    case BaseMessage(text, channel, user, _, _) =>
      BotInfoKeeper.current match {
        case Some(bi) if text.matches("(.*)(먹자)($|(\\s+.*))") && (text.contains(bi.id) || text.contains(bi.name)) && user != bi.id =>
          publish(OutboundMessage(channel, s"""${Random.shuffle(restaurants).head.name} GO!"""))

        case Some(bi) if text.matches("(.*)아는 식당($|(\\s+.*))") && (text.contains(bi.id) || text.contains(bi.name)) && user != bi.id =>
          publish(OutboundMessage(channel, restaurants.map(_.name).mkString(", ")))

        case Some(bi) if text.matches("(?i)(^|\\s*)(hi|hello)($|(\\s+.*))") && (text.contains(bi.id) || text.contains(bi.name)) && user != bi.id =>
          val welcomes = List("what's up?", "how's going?", "ready for work?", "nice to see you")
          def welcome = Random.shuffle(welcomes).head
          publish(OutboundMessage(channel, s"""hello <@$user>,\\n $welcome"""))

        case _ => //nothing to do!
      }

  }

  override def help(channel: String): OutboundMessage = OutboundMessage(channel, s"When you feel hungry and don't know where to go *$name* is something for you. \\n " +
    s"`Bob` - to talk with the bot")
}
