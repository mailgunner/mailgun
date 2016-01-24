package services

import play.api.{Configuration, Play}
import play.api.Play.current

/**
 * Provides configuration access, whether it's Play's or sbt's.
 */
trait ConfigComponent {
  def config: Configuration
}

/**
 * Default config using Play's built-in config.
 */
trait DefaultConfigComponent extends ConfigComponent {
  override val config = Play.configuration
}
