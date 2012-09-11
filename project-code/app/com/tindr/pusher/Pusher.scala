package com.tindr.pusher

import play.api.Logger
import play.Configuration
import play.api.Play.current
import play.api.UnexpectedException
import play.api.UnexpectedException
import play.api.libs.ws.WS
import play.api.libs.concurrent.Promise
import play.api.libs.ws.Response
import play.api.libs.json.Json.toJson
import json.JSONAuthDataWriter
import json.JSONPresenceChannelDataWriter
import PusherUtil._

case class Pusher(private val appId: String, private val key: String, private val secret: String) {

	private val host = "api.pusherapp.com"

	/**
	 * Triggers an event on a channel and delivers a message to all the channel suscribers.
	 */
	def trigger(channel: String, event: String, message: String): Promise[Response] = trigger(channel, event, message, None)

	/**
	 * Triggers an event on a channel and delivers a message to all the channel suscribers
	 * excluding the passed socketId.
	 */
	def trigger(channel: String, event: String, message: String, socketId: Option[String]) = {

		val path = "/apps/" + appId + "/channels/" + channel + "/events"
		val socketParam = socketId match {
			case Some(id) => "&socket_id=" + id
			case _ => ""
		}

		val query = "auth_key=" + this.key +
			"&auth_timestamp=" + (System.currentTimeMillis() / 1000) +
			"&auth_version=1.0" +
			"&body_md5=" + md5(message).getOrElse("") +
			"&name=" + event +
			socketParam

		val key = "POST\n" + path + "\n" + query
		val signature = sha256(key, secret).getOrElse("")
		
		val uri = "http://" + host + path + "?" + query + "&auth_signature=" + signature
		
		Logger.info("Sending message to the following uri: '" + uri + "'")

		WS.url(uri).post(message)
	}

	/**
	 * Generates an authentication signature for a private channel
	 */
	def createAuthString(socketId: String, channel: String) = {
		val signature = sha256((socketId + ":" + channel), this.secret)
		val auth = AuthData(this.key + ":" + signature)

		toJson(auth)(JSONAuthDataWriter)
	}

	/**
	 * Generates an authentication signature for a presence channel
	 */
	def createAuthString(socketId: String, channel: String, channelData: PresenceChannelData) = {

		val jsonChannelData = toJson(channelData)(JSONPresenceChannelDataWriter).toString

		val signature = sha256((socketId + ":" + channel + ":" + jsonChannelData), this.secret)
		val auth = AuthData(this.key + ":" + signature, Some(jsonChannelData))

		toJson(auth)(JSONAuthDataWriter)
	}
}

object Pusher {
	def apply() = {
		val appId = current.configuration.getString("pusher.appId") match {
			case Some(id) => id
			case _ => throw new UnexpectedException(Some("Module Pusher requires that you specify pusher.appId in your application.conf"))
		}

		val key = current.configuration.getString("pusher.key") match {
			case Some(key) => key
			case _ => throw new UnexpectedException(Some("Module Pusher requires that you specify pusher.key in your application.conf"))
		}

		val secret = current.configuration.getString("pusher.secret") match {
			case Some(secret) => secret
			case _ => throw new UnexpectedException(Some("Module Pusher requires that you specify pusher.secret in your application.conf"))
		}

		new Pusher(appId, key, secret)
	}
}