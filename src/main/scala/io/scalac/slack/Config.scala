package io.scalac.slack

import com.typesafe.config.ConfigFactory
import io.scalac.slack.api.APIKey

/**
 * Created on 20.01.15 22:17
 */
object Config {
  def websocketKey: String = config.getString("websocket.key")

  private val config = ConfigFactory.load()

  def apiKey: APIKey = APIKey(config.getString("api.key"))

  def baseUrl(endpoint: String) = config.getString("api.base.url") + endpoint

  def scalaLibraryPath = config.getString("scalaLibraryPath")
}
