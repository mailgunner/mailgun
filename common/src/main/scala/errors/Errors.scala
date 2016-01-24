package errors

import play.api.libs.json.Json

object Errors {

  abstract class BaseError(message: String) extends RuntimeException(message: String) {
    val json = Json.parse(s"""{ "error": "$message" }""")
  }

  case class InvalidJsonError(message: String = "Invalid JSON format") extends BaseError(message: String)
  case class UnknownError(message: String = "We could not process your request") extends BaseError(message: String)
}
