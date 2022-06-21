package nl.uva.cci
package normativeservices

import util.{EFLINT, ServerState}

object Main extends App {
  val eflint = new EFLINT("src/main/eflint/notary.eflint", true)
  println("done")
}
