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

+dav(target(Target)) => #coms.inform("IHLDevice",dav(Target)).
-dav(target(Target)) => #coms.un_inform("IHLDevice",dav(Target)).
+evma(target(Target),value(Value)) => #coms.inform("IHLDevice",evma(Target,Value)).
-evma(target(Target),value(Value)) => #coms.un_inform("IHLDevice",evma(Target,Value)).