package styleGuide.classic

import akka.actor.{Actor, ActorLogging, Props}

// Classic Actor

object HelloWorld {

  final case class Greet(whom: String)
  final case class Greeted(whom: String)

  def props(): Props = Props(new HelloWorld)

}

class HelloWorld extends Actor with ActorLogging {
  import HelloWorld._

  override def receive: Receive = {
    case Greet(whom) =>
      log.info("Hello {}!", whom)
      sender() ! Greeted(whom)
  }
}
