package scheduling

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

object Buncher {

  sealed trait Command
  final case class ExcitingMessage(message: String) extends Command
  final case class Batch(messages: Vector[Command])

  private case object Timeout extends Command
  private case object TimerKey

  def apply(target: ActorRef[Batch], after: FiniteDuration, mazSize: Int): Behavior[Command] =
    Behaviors.withTimers(times => new Buncher(times, target, after, mazSize).idle())

}

class Buncher(
    timers: TimerScheduler[Buncher.Command],
    target: ActorRef[Buncher.Batch],
    after: FiniteDuration,
    maxSize: Int
) {
  import Buncher._

  private def idel(): Behavior[Command] =
    Behaviors.receiveMessage[Command] { message =>
      timers.startTimerWithFixedDelay()

    }

}
