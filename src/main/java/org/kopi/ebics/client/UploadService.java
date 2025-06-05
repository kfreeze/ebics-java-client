package org.kopi.ebics.client;


public record UploadService(String serviceName, String serviceOption, String scope, String containerType,
                            String messageName, String messageFormat, String messageVariant, String messageVersion,
                            boolean signatureFlag, boolean edsFlag) implements IEbicsService {

}
