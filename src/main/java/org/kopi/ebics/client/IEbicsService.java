package org.kopi.ebics.client;

/**
 * Generic interface representing a Business Transaction Format (BTF)
 * used in EBICS 3.0 for both upload (e.g. BTU) and download (e.g. BTD) operations.
 * <p>
 * EBICS 3.0 replaces the older {@code OrderAttribute} mechanism with
 * a semantically rich description via BTF elements. This interface provides access
 * to the standard BTF fields as defined in EBICS 3.0 specification.
 * <p>
 * Implementations should return the appropriate values for a specific
 * service such as SEPA Direct Debit, Credit Transfer, or account statements.
 *
 * @see <a href="https://www.ebics.org">EBICS Official Site</a>
 */
public interface IEbicsService {

    /**
     * Returns the BTF service name.
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code "SDD"} – SEPA Direct Debit</li>
     *     <li>{@code "CCT"} – SEPA Credit Transfer</li>
     *     <li>{@code "CAMT"} – Account statement (download)</li>
     * </ul>
     *
     * @return the service name component of the BTF (never {@code null})
     */
    String getServiceName();

    /**
     * Returns the BTF service option (e.g. execution method or variant).
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code "COR"} – COR1 Direct Debit</li>
     *     <li>{@code "B2B"} – Business-to-business Direct Debit</li>
     *     <li>{@code "URN"} – Urgent transfer</li>
     * </ul>
     *
     * @return the service option component of the BTF (never {@code null})
     */
    String getServiceOption();

    /**
     * Returns the geographical or national scope of the transaction.
     * <p>
     * This value is optional and may be empty or {@code null} depending on the BTF specification.
     * For example, {@code "DE"} for Germany or {@code "EU"} for Eurozone-wide formats.
     *
     * @return the optional scope string, or {@code null} if not applicable
     */
    String getScope();

    /**
     * Returns the container type for the transaction content.
     * <p>
     * Typical values:
     * <ul>
     *     <li>{@code "XML"} – for structured SEPA message files</li>
     *     <li>{@code "ZIP"} – for zipped payloads</li>
     *     <li>{@code "TXT"} – for plain text files</li>
     * </ul>
     *
     * @return the container type string (defaults to {@code "XML"})
     */
    default String getContainerType() {
        return "XML";
    }

    /**
     * Returns the name of the message type being exchanged.
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code "pain.008"} – Direct Debit</li>
     *     <li>{@code "pain.001"} – Credit Transfer</li>
     *     <li>{@code "camt.053"} – Account statement</li>
     * </ul>
     *
     * @return the message name (never {@code null})
     */
    String getMessageName();

    /**
     * Returns the message format of the transaction.
     * <p>
     * Most transactions will use {@code "ISO"} for ISO 20022 messages.
     *
     * @return the format identifier (e.g., {@code "ISO"})
     */
    String getMessageFormat();

    /**
     * Returns the message variant to further qualify the message format.
     * <p>
     * This may differentiate between country-specific implementations or bank-specific variants.
     *
     * @return the variant string, or {@code null} if not applicable
     */
    String getMessageVariant();

    /**
     * Returns the version of the message schema being used.
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code "08"} for {@code pain.008.001.08}</li>
     *     <li>{@code "04"} for {@code camt.053.001.04}</li>
     * </ul>
     *
     * @return the message version number (e.g., {@code "08"})
     */
    String getMessageVersion();

    /**
     * Indicates whether the transaction requires the signature flag to be set.
     * <p>
     * This is usually {@code true} for upload operations (e.g., BTU),
     * but {@code false} for most download operations (e.g., BTD).
     *
     * @return {@code true} if a user signature is required; {@code false} otherwise
     */
    default boolean isSignatureFlag() {
        return false;
    }

    /**
     * Indicates whether the transaction requires the EDS (Electronic Distributed Signature) flag.
     * <p>
     * EDS is typically used in multi-user signature workflows.
     *
     * @return {@code true} if EDS flag should be activated; {@code false} otherwise
     */
    default boolean isEdsFlag() {
        return false;
    }
}
