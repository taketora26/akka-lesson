package lesson3

import akka.actor.testkit.typed.scaladsl.{LogCapturing, ScalaTestWithActorTestKit}
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.wordspec.AnyWordSpecLike

import java.net.URI

class BackendSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "The interaction patterns docs" must {
    "contain a sample for adapted response" in {

      val backend  = spawn(Behaviors.receiveMessage[Backend.Request] {
        case Backend.StartTranslationJob(taskId, _, replyTo) =>
          replyTo ! Backend.JobStarted(taskId)
          replyTo ! Backend.JobProgress(taskId, 0.25)
          replyTo ! Backend.JobProgress(taskId, 0.50)
          replyTo ! Backend.JobProgress(taskId, 0.75)
          replyTo ! Backend.JobCompleted(taskId, new URI("https://akka.io/docs/sv/"))
          Behaviors.same
      })

      val frontend = spawn(Frontend(backend))
      val probe    = createTestProbe[URI]()
      frontend ! Frontend.Translate(new URI("https://akka.io/docs/"), probe.ref)
      probe.expectMessage(new URI("https://akka.io/docs/sv/"))
    }

  }

}
