package json

import com.tindr.pusher.PresenceChannelData

import play.api.libs.json.Json.toJson
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

/**
 * Writes PresenceChannelData objects to JSON
 */
object JSONPresenceChannelDataWriter extends Writes[PresenceChannelData] {
	override def writes(data: PresenceChannelData): JsValue = {
		toJson(Map(
				"user_id"   -> data.userId,
				"user_info" -> data.userInfo))
	}
}