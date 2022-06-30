!init.

// Init data
+!init =>
    +data(notary("Notary"));
    +data(municipality("Amsterdam"));
    +data(enforcement_service("Enforcer"))
    .

+data(Data) => #coms.inform("NotaryService", normative_fact(Data)).
+new_rule(Phrase) => #coms.achieve("NotaryService", amend_service(Phrase)).

+duty_issue_nim(N,C,mortgage(C,property(A,V)),property(A,V)) =>
    #println("OH I HAVE A NEW DUTY: "+duty_issue_nim(N,C,mortgage(C,property(A,V)),property(A,V)));
    #println("I DECIDED TO PERFORM IT");
    #println("BUSY WITH DOING IT, DONE! , NOW TIME TO PUT IT INTO NORMATIVE SERVICE");
    #coms.ask("NotaryService",perform_normative_if_enabled(issue_nim(N,C,mortgage(C,property(A,V)),property(A,V)))).
