package org.kopi.ebics.client;


public class DownloadService implements IEbicsService {

    @Override
    public String getServiceName() {
        return "EOP";
    }


    @Override
    public String getServiceOption() {
        return null;
    }


    @Override
    public String getScope() {
        return "DE";
    }


    @Override
    public String getContainerType() {
        return null;
    }


    @Override
    public String getMessageName() {
        return "mt940";
    }


    @Override
    public String getMessageFormat() {
        return null;
    }


    @Override
    public String getMessageVariant() {
        return null;
    }


    @Override
    public String getMessageVersion() {
        return null;
    }
}
