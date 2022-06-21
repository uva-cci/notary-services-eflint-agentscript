
//===================GENERIC SERVICE PLANS========================
// check if an act is permitted (true)
+?permitted(A): enabled(A) =>
    #println("PERMITTED:" + A);
    #coms.respond(true).

// check if an act is permitted (false)
+?permitted(A) =>
    #println("NOT PERMITTED:" + A);
    #coms.respond(false).

// perform an act (enabled)
+!perform(A): enabled(A) =>
    #println("PERFORMING:" + A);
    #nl.uva.cci.NormUtils.perform(A).

// perform an act (not enabled)
+!perform(A) => #coms.inform(Source, failed(A)).

// force performing an act
+!perform_forced(A) =>
    #nl.uva.cci.NormUtils.perform(A).

// checking if a fact holds (true)
+?holds(A): holds(A) =>
    #coms.respond(true).

// checking if a fact holds (false)
+?holds(A) =>
    #coms.respond(false).

// amending the flint instance with a phrase
@atomic
+!amend(Phrase) =>
    #println("Amending norms with:" + Phrase);
    #nl.uva.cci.NormUtils.amend(Phrase).


//===========================CASE SPECIFIC PLANS========================
+duty_issue_nim(notary(N),citizen(C),mortgage(citizen(C),property(address(A),value(V))),property(address(A),value(V))) =>
    #println("New Duty: "+duty_issue_nim(N,C,mortgage(C,property(A,V)),property(A,V)));
    #coms.inform("NotaryService", duty_issue_nim(N,C,mortgage(C,property(A,V)),property(A,V))).








