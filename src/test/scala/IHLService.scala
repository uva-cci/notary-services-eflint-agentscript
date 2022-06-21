package nl.uva.cci

import normativeservices.EFlintBBFactory

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorSystem, Scheduler}
import akka.util.Timeout
import asl.{ihl_advisor, notary_service_advisor}
import bb.expstyla.exp.{BooleanTerm, IntTerm, StringTerm, StructTerm}
import infrastructure._
import org.scalatest.wordspec.AnyWordSpecLike

import _root_.scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class IHLService extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  import org.apache.log4j.BasicConfigurator
  BasicConfigurator.configure()


  val mas = MAS()

  override def beforeAll(): Unit = {
    // Create System

    val system: ActorSystem[IMessage] = typed.ActorSystem(mas(), "MAS")

    val BB = new EFlintBBFactory("src/main/eflint/ihl.eflint")
    implicit val timeout: Timeout = 10000.milliseconds
    implicit val ec: ExecutionContextExecutor = system.executionContext
    implicit val scheduler: Scheduler = system.scheduler

    // Ask the system to create agents
    val result: Future[IMessage] = system.ask(ref => AgentRequestMessage(
      Seq(
        AgentRequest(new ihl_advisor(beliefBaseFactory = BB).agentBuilder, "IHLAdvisor", 1),
      ),ref))(timeout,scheduler)
    //wait for response
    println("WAITING RESPONSE")
    val system_ready : Boolean = try {
      val response = Await.result(result, timeout.duration).asInstanceOf[ReadyMessage]
      println("RESPONSE:" + response)
      true
    }
    catch {
      case x : Throwable =>
        x.printStackTrace()
        false
    }

    if(system_ready)
      println("agent created")

  }

  "the agents" should {
    "exist in yellow pages if it was created before" in {
        assert(mas.yellowPages.getAgent("IHLAdvisor").isDefined)
    }
  }


  "ihl advisor" should {

    "init" in {
      val prob = testKit.createTestProbe[IMessage]()
      mas.yellowPages.getAgent("IHLAdvisor").get.asInstanceOf[AkkaMessageSource].address()  ! GoalMessage(
        StructTerm("init"),AkkaMessageSource(prob.ref)
      )

      Thread.sleep(4000)

      prob.receiveMessage()
    }

    "return true if it has a fact" in {
      val prob = testKit.createTestProbe[IMessage]()
      mas.yellowPages.getAgent("IHLAdvisor").get.asInstanceOf[AkkaMessageSource].address()  ! AskMessage(
        StructTerm("holds",Seq(StructTerm("target",Seq(StringTerm("X"))))),AkkaMessageSource(prob.ref)
      )

      val message = prob.receiveMessage();
      assert(message.isInstanceOf[BeliefMessage])
      assert(message.asInstanceOf[BeliefMessage].p_content equals(BooleanTerm(true)))
    }

    "return false if it does not have a fact" in {
      val prob = testKit.createTestProbe[IMessage]()

      mas.yellowPages.getAgent("IHLAdvisor").get.asInstanceOf[AkkaMessageSource].address()  ! AskMessage(
        StructTerm("holds",Seq(StructTerm("target",Seq(StringTerm("M"))))),AkkaMessageSource(prob.ref)
      )

      val message = prob.receiveMessage();
      assert(message.isInstanceOf[BeliefMessage])
      assert(message.asInstanceOf[BeliefMessage].p_content equals(BooleanTerm(false)))
    }

    "return true if an event/act is enabled" in {
      val prob = testKit.createTestProbe[IMessage]()

      mas.yellowPages.getAgent("IHLAdvisor").get.asInstanceOf[AkkaMessageSource].address()  ! AskMessage(
        StructTerm("permitted",Seq(StructTerm("calculate",Seq(StringTerm("Nothing"))))),AkkaMessageSource(prob.ref)
      )

      val message = prob.receiveMessage();
      assert(message.isInstanceOf[BeliefMessage])
      assert(message.asInstanceOf[BeliefMessage].p_content equals(BooleanTerm(true)))
    }


    "have a dav after calculated" in {
      val prob = testKit.createTestProbe[IMessage]()

      mas.yellowPages.getAgent("IHLAdvisor").get.asInstanceOf[AkkaMessageSource].address()  ! GoalMessage(
        StructTerm("perform",Seq(StructTerm("calculate",Seq(StringTerm("Nothing"))))),AkkaMessageSource(prob.ref)
      )

//      val message = prob.receiveMessage();
//      assert(message.isInstanceOf[BeliefMessage])
//      assert(message.asInstanceOf[BeliefMessage].p_content equals(BooleanTerm(false)))
    }


  }


//  "A normative service agent" should {
//
//    "init" in {
//      val prob = testKit.createTestProbe[IMessage]()
//      mas.yellowPages.getAgent("NSeller").get.asInstanceOf[AkkaMessageSource].address()  ! AskMessage(
//        StructTerm("act",Seq(StructTerm("offer",Seq(StringTerm("Seller"),StringTerm("Buyer"),StringTerm("Book"),IntTerm(5))))),AkkaMessageSource(prob.ref)
//      )
//    }

//  }

  override def afterAll(): Unit = {
    Thread.sleep(5000)
    testKit.shutdownTestKit()
  }
}