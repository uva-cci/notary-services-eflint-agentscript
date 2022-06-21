package nl.uva.cci

import bb.expstyla.exp.{GenericTerm, StringTerm}
import infrastructure.ExecutionContext
import nl.uva.cci.normativeservices.EflintBeliefBase

object NormUtils {

  def perform(term: GenericTerm)(implicit e: ExecutionContext) = {
    e.beliefBase.query(term)
  }

  def amend(term: GenericTerm)(implicit e: ExecutionContext) = {
    e.beliefBase.asInstanceOf[EflintBeliefBase].amend(term)
  }
}
