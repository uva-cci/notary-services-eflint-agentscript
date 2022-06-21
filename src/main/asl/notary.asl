
+duty_issue_nim(N,C,mortgage(C,property(A,V)),property(A,V)) =>
    #println("OH I HAVE A NEW DUTY TO "+duty_issue_nim(N,C,mortgage(C,property(A,V)),property(A,V)));
    #println("I DECIDED TO PERFORM IT");
    #println("BUSY WITH DOING IT, ... , NOW TIME TO PUT IT INTO NORMATIVE SERVICE");
    #coms.achieve("NotaryService",perform_normative_if_enabled(issue_nim(N,C,mortgage(C,property(A,V)),property(A,V)))).
