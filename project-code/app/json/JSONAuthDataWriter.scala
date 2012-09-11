package json

import com.tindr.pusher.AuthData

import play.api.libs.json.Json.toJson
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/** Writes AuthData to JSON
  */
object JSONAuthDataWriter extends Writes[AuthData] {
	override def writes(auth: AuthData): JsValue = {
		toJson(Map(
				"auth"         -> auth.auth,
				"channel_data" -> auth.channelData.getOrElse("")))
	}
}