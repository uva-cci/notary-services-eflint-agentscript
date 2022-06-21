
!init.
//========================PLANS=======================

+!init =>
    +target("X");
    +target("Y");
    +target("Z");
    +target("N");

    +coeff_value(70);
    +proportionate_coeff(70);

    +outcome("X",42,8,50);
    +outcome("X",46,16,50);

    +outcome("Z",42,12,50);
    +outcome("Z",46,16,50);

    +outcome("N",44,14,100);

    +outcome("Y",4,8,100).

+?permitted(A): enabled(A) =>
    #coms.respond(true).

+?permitted(A) =>
    #coms.respond(false).

+!perform(A): enabled(A) =>
    #println("performing: " + A);
    #nl.uva.cci.NormUtils.perform(A).

+!perform(A) =>
    #println("NOT performing: " + A);
    #coms.inform(#executionContext.src, failed(A)).

+?holds(A): holds(A) =>
    #coms.respond(true).

+?holds(A) =>
    #coms.respond(false).

+dav(Target) => #println(Target).

+evma(Target,Value) =>
if(#vars("Source").ref == "IHLAdvisor") {
    #println(Source);
};
#println("+EVMA:"+Target+":"+Value).
-evma(Target,Value) => #println("-EVMA:"+Target+":"+Value).