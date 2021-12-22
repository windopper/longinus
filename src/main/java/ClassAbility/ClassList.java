package ClassAbility;

public enum ClassList {

    블래스터(6, 4, 4, 10, 12),
    아이테르(6, 6, 8, 3, 0),
    플록스(5, 3, 3, 4, 3),
    바이V(4, 6, 6, 4, 8),
    엑셀러레이터(8, 8, 12, 12, 15);

    String RL;
    String RR;
    String RF;
    String FR;
    String FF;

    int RLcost;
    int RRcost;
    int RFcost;
    int FRcost;
    int FFcost;

    private ClassList(int var0, int var1, int var2, int var3, int var4) {
        RLcost = var0;
        RRcost = var1;
        RFcost = var2;
        FRcost = var3;
        FFcost = var4;
    }

    public int getRLcost() {
        return RLcost;
    }
    public int getRRcost() {
        return RRcost;
    }
    public int getRFcost() {
        return RFcost;
    }
    public int getFRcost() {
        return FRcost;
    }
    public int getFFcost() {
        return FFcost;
    }


}
