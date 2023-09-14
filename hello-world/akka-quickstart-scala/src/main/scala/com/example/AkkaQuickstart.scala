//#full-example
package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.GreeterMain.SayHello

//#greeter-actor
object Greeter {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info("Hello {}!", message.whom)
    // #greeter-send-messages
    message.replyTo ! Greeted(message.whom, context.self)
    // #greeter-send-messages
    Behaviors.same
  }
}
//#greeter-actor

//#greeter-bot
object GreeterBot {

  def apply(max: Int, greetingCounter: Int): Behavior[Greeter.Greeted] = {
    bot(greetingCounter, max)
  }

  private def bot(greetingCounter: Int, max: Int): Behavior[Greeter.Greeted] =
    Behaviors.receive { (context, message) =>
      val n = greetingCounter + 1
      context.log.info("Greeting {} for {}", n, message.whom)
      if (n == max) {
        Behaviors.stopped
      } else {
        // New actor for each reply, the path will be ..../message.whom+P
        val replyToSomeoneelse =
          context.spawn(GreeterBot(max, n), message.whom + 'P')
        message.from ! Greeter.Greet(message.whom + 'P', replyToSomeoneelse)
        bot(n, max)
      }
    }
}
//#greeter-bot

//#greeter-main
object GreeterMain {

  final case class SayHello(name: String)

  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>
      // Solon note: This code is run when SayHello is called
      // We build a actor first, who job is sending out a message
      // The actor path will be /greeter
      // #create-actors
      val greeter = context.spawn(Greeter(), "greeter")
      // #create-actors

      Behaviors.receiveMessage { message =>
        // Solon note: We create a callback to on receiving a reply to GreeterMain
        // #create-actors
        val replyTo = context.spawn(GreeterBot(max = 3, 0), message.name)
        // #create-actors
        greeter ! Greeter.Greet(message.name, replyTo)
        Behaviors.same
      }
    }
}
//#greeter-main

//#main-class
object AkkaQuickstart extends App {
  // Solon note: everything starts here, setting up a actor system
  // #actor-system
  val greeterMain: ActorSystem[GreeterMain.SayHello] =
    ActorSystem(GreeterMain(), "AkkaQuickStart")
  // #actor-system

  // Solon note: here we send out a message ( ! ) by calling the SayHello function inside the actor system
  // #main-send-messages
  greeterMain ! SayHello("Charles")
  // #main-send-messages
}
//#main-class
//#full-example
