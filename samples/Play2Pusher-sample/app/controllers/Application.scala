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
		
		// Send message through Pusher
		val promise = Pusher().trigger(channel, event, message)
		
		// If you need to verify the response, just manipulate the promise object. Otherwise move on
		Ok(views.html.index(message))
	}

}