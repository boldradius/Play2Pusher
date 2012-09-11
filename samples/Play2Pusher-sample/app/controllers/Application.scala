package controllers

import play.api._
import play.api.mvc._
import com.tindr.pusher.Pusher

object Application extends Controller {

	def index = Action {
		// Default channel and events created by Pusher for a new app
		val channel = "test_channel"
		val event = "my_event"
		val message = "Hello from the Play2Pusher sample app!"
		
		// Send message through Pusher and catch response
		val promise = Pusher().trigger(channel, event, message)
		val resp = promise.await(10000)
		
		// Check status and body
		Logger.debug("Status: " + resp.get.status)
		Logger.debug("Body: " + resp.get.body.toString())

		Ok(views.html.index(message))
	}

}