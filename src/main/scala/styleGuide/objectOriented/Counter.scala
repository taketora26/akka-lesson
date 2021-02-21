package styleGuide.objectOriented

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext}

object Counter {
  sealed trait Command
  case object Increment                               extends Command
  final case class GetValue(replyTo: ActorRef[Value]) extends Command
  final case class Value(n: Int)
}

class Counter(context: ActorContext[Counter.Command]) extends AbstractBehavior[Counter.Command](context) {

  import Counter._

  private var n = 0

  override def onMessage(msg: Command): Behavior[Counter.Command] =
    msg match {
      case Counter.Increment =>
        n += 1
        context.log.debug("Incremented counter to [{}]", n)
        this

      case GetValue(replyTo) =>
        replyTo ! Value(n)
        this
    }

}
