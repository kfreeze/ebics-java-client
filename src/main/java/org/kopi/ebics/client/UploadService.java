package org.kopi.ebics.client;



public class UploadService implements IEbicsService {

    @Override
    public String getServiceName() {
        return "SDD";
    }

    @Override
    public String getServiceOption() {
        return "COR";
    }

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public String getContainerType() {
        return null;
    }

    @Override
    public String getMessageName() {
        return "pain.008";
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
