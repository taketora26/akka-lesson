# [Introduction to Actors](https://doc.akka.io/docs/akka/current/typed/actors.html)

```shell
scala> import akka.actor.typed.ActorSystem

scala> val system:ActorSystem[HelloWorldMain.SayHello] = ActorSystem(HelloWorldMain(),"hello")

scala> system ! HelloWorldMain.SayHello("Workd")

scala> system ! HelloWorldMain.SayHello("Akka")

```