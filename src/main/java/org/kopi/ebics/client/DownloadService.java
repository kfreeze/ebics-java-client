package org.kopi.ebics.client;


import java.util.Objects;

public final class DownloadService implements IEbicsService {

    private final String serviceName;
    private final String serviceOption;
    private final String scope;
    private final String containerType;
    private final String messageName;
    private final String messageFormat;
    private final String messageVariant;
    private final String messageVersion;

    public DownloadService(String serviceName, String serviceOption, String scope, String containerType,
                           String messageName, String messageFormat, String messageVariant,
                           String messageVersion) {
        this.serviceName = serviceName;
        this.serviceOption = serviceOption;
        this.scope = scope;
        this.containerType = containerType;
        this.messageName = messageName;
        this.messageFormat = messageFormat;
        this.messageVariant = messageVariant;
        this.messageVersion = messageVersion;
    }

    @Override
    public boolean signatureFlag() {
        return false;
    }

    @Override
    public boolean edsFlag() {
        return false;
    }

    @Override
    public String serviceName() {
        return serviceName;
    }

    @Override
    public String serviceOption() {
        return serviceOption;
    }

    @Override
    public String scope() {
        return scope;
    }

    @Override
    public String containerType() {
        return containerType;
    }

    @Override
    public String messageName() {
        return messageName;
    }

    @Override
    public String messageFormat() {
        return messageFormat;
    }

    @Override
    public String messageVariant() {
        return messageVariant;
    }

    @Override
    public String messageVersion() {
        return messageVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DownloadService) obj;
        return Objects.equals(this.serviceName, that.serviceName) &&
                Objects.equals(this.serviceOption, that.serviceOption) &&
                Objects.equals(this.scope, that.scope) &&
                Objects.equals(this.containerType, that.containerType) &&
                Objects.equals(this.messageName, that.messageName) &&
                Objects.equals(this.messageFormat, that.messageFormat) &&
                Objects.equals(this.messageVariant, that.messageVariant) &&
                Objects.equals(this.messageVersion, that.messageVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, serviceOption, scope, containerType, messageName, messageFormat, messageVariant, messageVersion);
    }

    @Override
    public String toString() {
        return "DownloadService[" +
                "serviceName=" + serviceName + ", " +
                "serviceOption=" + serviceOption + ", " +
                "scope=" + scope + ", " +
                "containerType=" + containerType + ", " +
                "messageName=" + messageName + ", " +
                "messageFormat=" + messageFormat + ", " +
                "messageVariant=" + messageVariant + ", " +
                "messageVersion=" + messageVersion + ']';
    }

}
