package org.kopi.ebics.client;


/**
 * Interface representing a logical EBICS service configuration.
 * <p>
 * This interface abstracts the attributes relevant to the EBICS BTF (Business Transaction Format)
 * parameters as used in EBICS 3.0 uploads and downloads, such as service name, option, scope,
 * message type, and container behavior.
 * </p>
 * <p>
 * Implementations typically provide metadata to generate or validate EBICS requests,
 * such as BTU/BTD OrderTypes and their associated parameters.
 * </p>
 *
 * <p><b>Typical usage:</b></p>
 * <pre>
 * {@code
 *     IEbicsService service = new UploadService();
 *     if (service.isSignatureFlag()) {
 *         // enforce EDS requirement
 *     }
 * }
 * </pre>
 *
 */
public interface IEbicsService {

    /**
     * Gets the EBICS BTF service name (e.g., "SDD", "EOP", "B2C").
     * <p>
     * This element identifies the core business context of the transaction, such as
     * SEPA Direct Debit (SDD) or account reporting (EOP).
     *
     * @return the EBICS service name as a non-null uppercase string
     */
    String serviceName();

    /**
     * Gets the service option value, which further refines the service context.
     * <p>
     * Examples: "COR", "B2B", "CCT", "C52". Some service options are mandatory
     * in conjunction with the service name, especially in SEPA scenarios.
     *
     * @return the EBICS service option, or null if not applicable
     */
    String serviceOption();

    /**
     * Gets the applicable scope of the service definition.
     * <p>
     * Scope defines the market or bilateral context under which the service is valid.
     * Typical values are:
     * <ul>
     *   <li>"DE" – national (Germany)</li>
     *   <li>"GLB" – globally defined</li>
     *   <li>"BIL" – bilaterally defined (between bank and partner)</li>
     * </ul>
     *
     * @return the EBICS BTF scope (2-letter ISO country code or reserved EBICS keyword)
     */
    String scope();

    /**
     * Returns the type of container used in EBICS data transfer.
     * <p>
     * Values may include:
     * <ul>
     *   <li>"XML" – plain XML structure</li>
     *   <li>"ZIP" – compressed container format (e.g., camt.053 in zip)</li>
     * </ul>
     *
     * @return the container type as a string, or null if not applicable
     */
    String containerType();

    /**
     * Gets the message name (MsgName) used in this service.
     * <p>
     * This usually refers to the ISO 20022 message name like "pain.008", "camt.053", "pain.001".
     * It is used to validate the payload and build the BTF parameters.
     *
     * @return the message name as a string (e.g., "pain.008")
     */
    String messageName();

    /**
     * Returns the message format string (optional).
     * <p>
     * This may be used to distinguish between "XML" and "SWIFT" or between different formatting standards.
     * Not always used in EBICS 3.0 BTF definitions.
     *
     * @return the message format identifier or null if not defined
     */
    String messageFormat();

    /**
     * Gets the variant of the message type.
     * <p>
     * Variants might be used when multiple dialects of the same message exist
     * (e.g., bank-specific or market-specific adjustments).
     *
     * @return the message variant identifier, or null if not applicable
     */
    String messageVariant();

    /**
     * Gets the specific version of the message, typically aligned with ISO 20022 schema versions.
     * <p>
     * Example: "001.08" for "pain.008.001.08"
     *
     * @return the message version part as a string (e.g., "001.08")
     */
    String messageVersion();

    /**
     * Indicates whether this service mandates the EBICS <SignatureFlag> element.
     * <p>
     * If true, the service should include <SignatureFlag requestEDS="true"/> in the request,
     * meaning that the transaction is expected to go through the Electronic Distributed Signature (VEU) process.
     *
     * @return true if <SignatureFlag requestEDS="true"/> is required, false otherwise
     */
    boolean signatureFlag();

    /**
     * Indicates whether an EBICS VEU (Electronic Distributed Signature) process is enabled for this service.
     * <p>
     * This may be used to drive validation logic or UI workflows that require additional signers.
     *
     * @return true if the service supports or expects VEU (EDS), false otherwise
     */
    boolean edsFlag();
}
