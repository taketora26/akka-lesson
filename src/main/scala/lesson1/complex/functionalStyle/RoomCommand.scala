package lesson1.complex.functionalStyle

import akka.actor.typed.scaladsl.{Behaviors, LoggerOps}
import akka.actor.typed.{ActorRef, Behavior}

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed trait RoomCommand
final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends RoomCommand

sealed trait SessionEvent
final case class SessionGranted(handle: ActorRef[PostMessage])      extends SessionEvent
final case class SessionDenied(reason: String)                      extends SessionEvent
final case class MessagePosted(screenName: String, message: String) extends SessionEvent

trait SessionCommand
final case class PostMessage(message: String)                 extends SessionCommand
private final case class NotifyClient(message: MessagePosted) extends SessionCommand

object ChatRoom {

  private final case class PublishSessionMessage(screenName: String, message: String) extends RoomCommand

  def apply(): Behavior[RoomCommand] = chatRoom(List.empty)

  private def chatRoom(sessions: List[ActorRef[SessionCommand]]): Behavior[RoomCommand] =
    Behaviors.receive { (context, message) =>
      message match {
        case GetSession(screenName, client)             =>
          val ses = context.spawn(
            session(context.self, screenName, client),
            name = URLEncoder.encode(screenName, StandardCharsets.UTF_8.name())
          )
          client ! SessionGranted(ses)
          chatRoom(ses :: sessions)

        case PublishSessionMessage(screenName, message) =>
          val notification = NotifyClient(MessagePosted(screenName, message))
          sessions.foreach(_ ! notification)
          Behaviors.same
      }

    }

  private def session(
      room: ActorRef[PublishSessionMessage],
      screenName: String,
      client: ActorRef[SessionEvent]
  ): Behavior[SessionCommand]                                                           =
    Behaviors.receiveMessage {
      case PostMessage(message)  =>
        // from client, publish to others via room
        room ! PublishSessionMessage(screenName, message)
        Behaviors.same
      case NotifyClient(message) =>
        // publish from the room
        client ! message
        Behaviors.same
    }

}

object Gabbler {

  def apply(): Behavior[SessionEvent] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case SessionGranted(handle)             =>
          handle ! PostMessage("Hello World!")
          Behaviors.same
        case MessagePosted(screenName, message) =>
          context.log.info2("message has been posted by '{}' : '{}'", screenName, message)
          Behaviors.stopped
      }

    }
}

//object ChatRoom {
//
//  private final case class PublishSessionMessage(screenName: String, message: String) extends RoomCommand
//
//  def apply(): Behavior[RoomCommand] = chatRoom(List.empty)
//
//  private def chatRoom(sessions: List[ActorRef[SessionCommand]]): Behavior[RoomCommand] =
//    Behaviors.receive { (context, message) =>
//      message match {
//        case GetSession(screenName, client)             =>
//          // create a child actor for further interaction with the client
//          val ses = context.spawn(
//            session(context.self, screenName, client),
//            name = URLEncoder.encode(screenName, StandardCharsets.UTF_8.name())
//          )
//          client ! SessionGranted(ses)
//          chatRoom(ses :: sessions)
//
//        case PublishSessionMessage(screenName, message) =>
//          val notification = NotifyClient(MessagePosted(screenName, message))
//          sessions.foreach(_ ! notification)
//          Behaviors.same
//      }
//    }
//
//  private def session(
//      room: ActorRef[PublishSessionMessage],
//      screenName: String,
//      client: ActorRef[SessionEvent]
//  ): Behavior[SessionCommand]                                                           =
//    Behaviors.receiveMessage {
//      case PostMessage(message)  =>
//        room ! PublishSessionMessage(screenName, message)
//        Behaviors.same
//      case NotifyClient(message) =>
//        client ! message
//        Behaviors.same
//    }
//
//}
