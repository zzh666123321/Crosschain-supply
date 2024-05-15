package com.tanklab.supply.common;

public enum CityBlock {
    Paris("http://116.204.29.222:10012","0847b4b2f1fe3944bdb62be2f528d3a1a2cca2b957df7fba422830c1c7a14bf6",15987),//国外链
    Tianjin("http://116.204.29.222:10016","0847b4b2f1fe3944bdb62be2f528d3a1a2cca2b957df7fba422830c1c7a14bf6",15988),//国内链
    Beijing("http://116.204.29.222:12301","",0);
//    Tokyo("Chain 3",3),
//    London("Chain 4",4);

    public String netWorkUrl;
    public String walletKey;
    public long chainId;
    CityBlock(String netWorkUrl,String walletKey,long chainId) {
        this.netWorkUrl = netWorkUrl;
        this.walletKey = walletKey;
        this.chainId = chainId;
    }
}
