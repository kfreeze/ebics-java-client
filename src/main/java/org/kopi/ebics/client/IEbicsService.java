package org.kopi.ebics.client;

/**
 * Common interface to represent an EBICS service definition
 * including message metadata, format details, and container preferences.
 * <p>
 * This interface is designed for EBICS 3.0 usage, where services are
 * described via {@code ServiceName}, {@code MessageType}, {@code Format}, {@code Variant}, and {@code Version}.
 */
public interface IEbicsService {

    /**
     * @return the mandatory service name, such as {@code BTU} (Business Transaction Upload)
     *         or {@code BTD} (Business Transaction Download).
     */
    String getServiceName();

    /**
     * @return the optional service option, used to distinguish service variants
     *         (e.g., {@code SEPA}, {@code C53}, {@code STA}). Can be {@code null}.
     */
    String getServiceOption();

    /**
     * @return the optional service scope to which the service applies, such as
     *         {@code customer}, {@code partner}, or {@code system}. Can be {@code null}.
     */
    String getScope();

    /**
     * @return the container type used for the payload (e.g., {@code XML}, {@code ZIP}, {@code GZIP}).
     *         Defaults to {@code XML}.
     */
    default String getContainerType() {
        return "XML";
    }

    /**
     * @return the EBICS message name, such as {@code pain.001}, {@code camt.053}, or {@code pain.008}.
     *         This corresponds to the <code>&lt;MsgName&gt;</code> field in EBICS 3.0.
     */
    String getMessageName();

    /**
     * @return the message format, typically {@code xml} or {@code txt}.
     *         This maps to the <code>&lt;Format&gt;</code> field in BTF.
     */
    String getMessageFormat();

    /**
     * @return the message variant, such as {@code sepa}, {@code core}, {@code b2b}, or custom.
     *         This maps to the <code>&lt;Variant&gt;</code> field in BTF. Can be {@code null}.
     */
    String getMessageVariant();

    /**
     * @return the version of the message schema, such as {@code 001.03} or {@code 001.08}.
     *         This maps to the <code>&lt;Version&gt;</code> field in BTF.
     */
    String getMessageVersion();
}
