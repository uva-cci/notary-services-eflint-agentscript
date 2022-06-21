//------------------BELIEFS----------------------
// decisions/targets
target("X").
target("Y").
target("Z").
target("N").

coeff_value(70).
proportionate_coeff(70).

outcome("X",42,8,50).
outcome("X",46,16,50).

outcome("Z",42,12,50).
outcome("Z",46,16,50).

outcome("N",44,14,100).

outcome("Y",4,8,100).

//==================RULES=====================
// calculations and rules
evciv(D,V) :-
    findall(A*Y,outcome(d(D),vciv(A),X,p(Y)),AA) &&
    sumlist(AA,V).

evma(D,V) :-
    findall(A*Y,outcome(d(D),X,vma(A),p(Y)),AA) &&
    sumlist(AA,V).

eqma(X,Y) :-
    evma(X,VMX) &&
    evma(Y,VMY) &&
    VX == VY.

lessciv(X,Y) :-
    d(X) && d(Y) &&
    evciv(X,VCX) &&
    evciv(Y,VCY) && VCX < VCY.

prop(X) :-
    evma(X,VMX) &&
    evciv(X,VCX) &&
    prp(Prp) &&
    PVCX is VCX * Prp &&
    VMX =< PVCX.

morerel(X,Y) :-
    evma(X,VMX) && evma(Y,VMY) &&
    evciv(X,VCX) && evciv(Y,VCY) &&
    SATX is VMX * VCX &&
    SATY is VMY * VCY &&
    SATX >= SATY.

dt(X) :-
    d(X) &&
    forall(d(Y), (X !== Y && eqma(X,Y) && lessciv(X,Y)) -> fail || true).

dp(D) :- d(D) && prop(D).

dmh(X) :- d(X) &&
    forall(d(Y), (X !== Y && not morerel(X,Y)) -> fail || true).

dav(D) :- dt(D) && dp(D) && dmh(D).

//=======================PREFERENCES==================
// preferences: A target D1 is preferred to D2 of the evma of D1 is more than D2
+!target(D1) >> +!target(D2) :- d(D1) >> d(D2).
d(D1) >> d(D2) :- evma(D1,V1) && evma(D2,V2) && V1 > V2.


//========================PLANS=======================

// MAIN PLAN
// target plan: if the parameter D is already bound (e.g. !target(y)) then checks if it is legal and performs it (prints it)
//              if the parameter D is not bound (free var, e.g. !target(D)) finds the most preferred legal option and performs it.
@preferences
+!target(D)
    : d(D) && dav(D)
        => #println("TARGETING:" + D).

// OTHERS
+?is_legal(D)
    : d(D) && dav(D)
        => #coms.respond(true).
+?is_legal(D)
        => #coms.respond(false).

+?get_all_legal
    : findall(X,dav(X),R)
        => #coms.respond(R).

+?get_all_most_preferred_legal
    : findall(X,(most_preferred(d(X)) && dav(X)),R)
        => #coms.respond(R).



