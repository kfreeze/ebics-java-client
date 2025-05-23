package org.kopi.ebics.client;


public class UploadService implements IEbicsService {

    private final String serviceName;
    private final String serviceOption;
    private final String scope;
    private final String containerType;
    private final String messageName;
    private final String messageFormat;
    private final String messageVariant;
    private final String messageVersion;
    private final boolean signatureFlag;
    private final boolean edsFlag;

    public UploadService(String serviceName, String serviceOption, String messageName) {
        this(serviceName, serviceOption, null, null, messageName,
                null, null, null, true, false);
    }

    public UploadService(String serviceName, String serviceOption, String scope, String containerType,
                         String messageName, String messageFormat, String messageVariant, String messageVersion, boolean signatureFlag, boolean edsFlag) {
        this.serviceName = serviceName;
        this.serviceOption = serviceOption;
        this.scope = scope;
        this.containerType = containerType;
        this.messageName = messageName;
        this.messageFormat = messageFormat;
        this.messageVariant = messageVariant;
        this.messageVersion = messageVersion;
        this.signatureFlag = signatureFlag;
        this.edsFlag = edsFlag;
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public String getServiceOption() {
        return this.serviceOption;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public String getContainerType() {
        return this.containerType;
    }

    @Override
    public String getMessageName() {
        return this.messageName;
    }

    @Override
    public String getMessageFormat() {
        return this.messageFormat;
    }

    @Override
    public String getMessageVariant() {
        return this.messageVariant;
    }

    @Override
    public String getMessageVersion() {
        return this.messageVersion;
    }

    @Override
    public boolean isSignatureFlag() {
        return this.signatureFlag;
    }

    @Override
    public boolean isEdsFlag() {
        return this.edsFlag;
    }
}
