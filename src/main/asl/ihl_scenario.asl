
+!run =>
    +data(coeff_value(100));
    +data(proportionate_coeff(100));
    +data(target("RelSat1_Heavy"));
    +data(target("RelSat1_Light"));
    +data(target("RelSat2_Heavy"));
    +data(target("RelSat2_Light"));
    +data(target("DataCen_Heavy"));
    +data(target("DataCen_Light"));

    +data(outcome("DataCen_Heavy",20,80,100));

    +data(outcome("DataCen_Light",50,20,100));

    +data(outcome("RelSat1_Heavy",30,50,100));
    +data(outcome("RelSat1_Light",75,40,100));

    +data(outcome("RelSat2_Heavy",40,50,100));
    +data(outcome("RelSat2_Light",60,50,100));

    #Thread.sleep(#asInteger(5000));

    #coms.achieve("IHLDevice",calculate);

    #Thread.sleep(#asInteger(10000));

    #coms.achieve("IHLDevice",target(D));
    #Thread.sleep(#asInteger(2000))
    .


@atomic
+data(Fact) =>
    #coms.inform("IHLDevice", data(Fact)).

// for removing normative facts
@atomic
-data(Fact) =>
    #coms.un_inform("IHLAdvisor", data(Fact)).
