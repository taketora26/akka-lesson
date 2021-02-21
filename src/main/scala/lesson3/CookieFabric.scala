package lesson3

import akka.actor.TypedActor.context
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors

object CookieFabric {
  case class Request(query: String, replyTo: ActorRef[Response])
  case class Response(result: String)

  def apply(): Behaviors.Receive[Request] =
    Behaviors.receiveMessage[Request] {
      case Request(query, replyTo) =>
        println(query)
        replyTo ! Response(s"Here are ")
        Behaviors.same
    }
}

object Main {

  val cookieFabric = ActorSystem(CookieFabric(), "fire-and-forget-sample")

//  cookieFabric ! CookieFabric.Request("give me cookies", context.self)

}
