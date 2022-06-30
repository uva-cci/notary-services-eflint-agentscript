//==== PREFERENCES ====
+!target(D1) >> +!target(D2) :- d(D1) >> d(D2).
d(D1) >> d(D2) :- evma(D1,V1) && evma(D2,V2) && V1 > V2.

//==== MAIN TARGETING ABILITY ====
// If receives a concrete target it will check if that target is `dav`
// If receives a variable it will find the most preferred `dav` target if any
@preferences
+!target(D) : dav(D) => #println("TARGETING ... " + D).

//==== Other stuff ========
// We can add all sorts of queries here
//=========================

// for adding normative facts
@atomic
+data(Fact) =>
    #coms.inform("IHLAdvisor", Fact).

// for removing normative facts
@atomic
-data(Fact) =>
    #coms.un_inform("IHLAdvisor", Fact).

// for performing acts
+!perform_normative(Act, true) =>
    #coms.achieve("IHLAdvisor", perform(Act)).

// perform only if enabled by asking
+?perform_normative_if_enabled(Act) =>
    #println("Checking permission to perform: " + Act);
    Permission = #coms.ask("IHLAdvisor", permitted(Act));
    !perform_normative(Act, Permission);
    #coms.respond(Permission).

// Need to run after getting data and before target selection
+!calculate =>
    !perform_normative(calculate("Nothing"),true).
