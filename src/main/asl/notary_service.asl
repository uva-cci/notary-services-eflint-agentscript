

// for adding normative facts
+normative_fact(Fact) =>
    #coms.inform("NotaryServiceAdvisor", Fact).

// for removing normative facts
-normative_fact(Fact) =>
    #coms.un_inform("NotaryServiceAdvisor", Fact).

+!normative_event(Event) =>
    #println("Event:" + Event);
    #coms.achieve("NotaryServiceAdvisor", event(Event)).

// for performing acts (with permission as true)
+!perform_normative(Act, true) =>
    #println("Performing Permitted act:" + Act);
    #coms.achieve("NotaryServiceAdvisor", perform(Act)).

// for performing acts (with permission as false)
+!perform_normative(Act, false) =>
    #println("ACT NOT PERMITTED:" + Act).

// perform only if enabled by asking
+?perform_normative_if_enabled(Act) =>
    #println("Checking permission to perform: " + Act);
    Permission = #coms.ask("NotaryServiceAdvisor", permitted(Act));
    !perform_normative(Act, Permission);
    #coms.respond(Permission).

// for amending the service with phrases
@atomic
+!amend_service(Phrase) =>
    #coms.achieve("NotaryServiceAdvisor", amend(Phrase)).


//================ CONTEXT SPECIFIC PLANS ===================
// inform the notary application about its duty
+duty_issue_nim(N,C,mortgage(C,property(A,V)),property(A,V)) =>
    #coms.inform(N,duty_issue_nim(N,C,mortgage(C,property(A,V)),property(A,V))).

// inform
+duty_to_cancel_nim(C,N,mortgage(C,property(A,V))) =>
    #nl.uva.cci.normativeservices.Environment.logEvent(timer_started(cancel_nim_delay(C,mortgage(C,property(A,V))))).

