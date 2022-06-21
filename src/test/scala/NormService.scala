package nl.uva.cci

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorSystem, Scheduler}
import akka.util.Timeout
import asl.{notary, notary_service, notary_service_advisor}
import bb.expstyla.exp.{IntTerm, StringTerm, StructTerm}
import infrastructure._
import nl.uva.cci.normativeservices.EFlintBBFactory
import org.scalatest.wordspec.AnyWordSpecLike

import _root_.scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class NormService extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()


  val mas = MAS()

  override def beforeAll(): Unit = {
    // Create System

    val system: ActorSystem[IMessage] = typed.ActorSystem(mas(), "MAS")

    val BB = new EFlintBBFactory("src/main/eflint/notary.eflint")
    //    ClusterBootstrap(system).start()
    implicit val timeout: Timeout = 5000.milliseconds
    implicit val ec: ExecutionContextExecutor = system.executionContext
    implicit val scheduler: Scheduler = system.scheduler

    // Ask the system to create agents
    val result: Future[IMessage] = system.ask(ref => AgentRequestMessage(
      Seq(
        AgentRequest(new notary_service_advisor(beliefBaseFactory = BB).agentBuilder, "NotaryServiceAdvisor", 1),
        AgentRequest(new notary_service().agentBuilder, "NotaryService", 1),
        AgentRequest(new notary().agentBuilder, "Notary", 1),
      ), ref))(timeout, scheduler)
    //wait for response
    println("WAITING RESPONSE")
    val system_ready: Boolean = try {
      val response = Await.result(result, timeout.duration).asInstanceOf[ReadyMessage]
      println("RESPONSE:" + response)
      true
    }
    catch {
      case x: Throwable =>
        x.printStackTrace()
        false
    }

    if (system_ready)
      println("agent created")

  }

  "the agents" should {
    "exist in yellow pages if it was created before" in {
      assert(mas.yellowPages.getAgent("NotaryServiceAdvisor").isDefined)
      assert(mas.yellowPages.getAgent("NotaryService").isDefined)
      assert(mas.yellowPages.getAgent("Notary").isDefined)
    }
  }


  "notary service advisor" should {
    "init" in {
      val prob = testKit.createTestProbe[IMessage]()

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("citizen", Seq(StringTerm("Alice"))))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("citizen", Seq(StringTerm("Bob"))))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("citizen", Seq(StringTerm("Chloe"))))), AkkaMessageSource(prob.ref)
      )

      Thread.sleep(1000)
      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("property", Seq(StringTerm("A1"), IntTerm(3))))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("mortgage", Seq(StringTerm("Alice"), StructTerm("property", Seq(StringTerm("A1"), IntTerm(3))))))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("occupant", Seq(StringTerm("Alice"), StructTerm("property", Seq(StringTerm("A1"), IntTerm(3))))))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("property", Seq(StringTerm("B1"), IntTerm(2))))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("occupant", Seq(StringTerm("Bob"), StructTerm("property", Seq(StringTerm("B1"), IntTerm(2))))))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("mortgage", Seq(StringTerm("Bob"), StructTerm("property", Seq(StringTerm("B1"), IntTerm(2))))))), AkkaMessageSource(prob.ref)
      )

      Thread.sleep(1000)
      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("first_property", Seq(StringTerm("Alice"), StructTerm("property", Seq(StringTerm("A1"), IntTerm(3))))))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! BeliefMessage(
        StructTerm("normative_fact", Seq(StructTerm("first_property", Seq(StringTerm("Bob"), StructTerm("property", Seq(StringTerm("B1"), IntTerm(2))))))), AkkaMessageSource(prob.ref)
      )

      Thread.sleep(3000)
    }


    "amend" in {
      val prob = testKit.createTestProbe[IMessage]()

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! GoalMessage(
        StructTerm("amend_service", Seq(StringTerm("Extend Fact article_one Conditioned by article_three()"))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! GoalMessage(
        StructTerm("amend_service", Seq(StringTerm("Fact undue_cancel_nim Identified by citizen * mortgage"))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! GoalMessage(
        StructTerm("amend_service", Seq(StringTerm("Duty duty_to_cancel_nim Holder citizen Claimant notary  Related to mortgage, occupant    When citizen == mortgage.citizen && citizen == occupant.citizen  Violated when undue_cancel_nim()  Holds when nim_covered(mortgage) && mortgage.property != occupant.property"))), AkkaMessageSource(prob.ref)
      )

      mas.yellowPages.getAgent("NotaryService").get.asInstanceOf[AkkaMessageSource].address() ! GoalMessage(
        StructTerm("amend_service", Seq(StringTerm("Event cancel_nim_delay  Related to citizen, mortgage  Creates undue_cancel_nim() When duty_to_cancel_nim()"))), AkkaMessageSource(prob.ref)
      )

      println("AMENDING COMPLETE")

      Thread.sleep(3000)

    }

  }


  override def afterAll(): Unit = {
    Thread.sleep(5000)
    testKit.shutdownTestKit()
  }
}