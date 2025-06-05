/*
 * Copyright (c) 1990-2012 kopiLeft Development SARL, Bizerte, Tunisia
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.ebics.letter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.codec.binary.Hex;
import org.kopi.ebics.certificate.KeyUtil;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.interfaces.InitLetter;
import org.kopi.ebics.messages.Messages;


public abstract class AbstractInitLetter implements InitLetter {

    protected static final String BUNDLE_NAME = "org.kopi.ebics.letter.messages";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    protected final Messages messages;
    protected Locale locale;
    private Letter letter;

    /**
     * Constructs a new initialization letter.
     *
     * @param locale the application locale
     */
    protected AbstractInitLetter(Locale locale) {
        this.locale = locale;
        this.messages = new Messages(BUNDLE_NAME, locale);
    }

    @Override
    public void writeTo(OutputStream output) throws IOException {
        output.write(letter.getLetter());
    }

    // --------------------------------------------------------------------
    // INNER CLASS
    // --------------------------------------------------------------------

    /**
     * Builds an initialization letter.
     *
     * @param hostId      the host ID
     * @param bankName    the bank name
     * @param userId      the user ID
     * @param username    the user name
     * @param partnerId   the partner ID
     * @param version     the signature version
     * @param certTitle   the certificate title
     * @param certificate the certificate content
     * @param hashTitle   the hash title
     * @param hash        the hash value
     * @throws IOException if something is wrong with building the letter
     */
    @SuppressWarnings("java:S107")
    protected void build(String hostId,
                         String bankName,
                         String userId,
                         String username,
                         String partnerId,
                         String version,
                         String certTitle,
                         byte[] certificate,
                         String hashTitle,
                         byte[] hash)
            throws IOException {
        letter = new Letter(getTitle(),
                hostId,
                bankName,
                userId,
                username,
                partnerId,
                version, messages);
        letter.build(certTitle, certificate, hashTitle, hash);
    }


    /**
     * Returns the value of the property key.
     *
     * @param key the property key
     * @return the property value
     */
    protected String getString(String key) {
        return messages.getString(key);
    }

    /**
     * Generates a SHA-256 hash of the given certificate and returns it as a byte array after formatting.
     * <p>
     * The certificate input is hashed using the SHA-256 algorithm. The resulting hash is encoded
     * as a hexadecimal string , then passed through a custom {@code format} method
     * (which should be defined elsewhere in the class), and finally converted back to a byte array.
     * </p>
     *
     * @param certificate the input certificate in binary (DER) format, not {@code null}
     * @return the formatted SHA-256 hash of the certificate as a byte array
     * @throws GeneralSecurityException if the SHA-256 algorithm is not available
     */
    protected byte[] getHash(byte[] certificate) throws GeneralSecurityException {
        // Calculate SHA-256 digest of the certificate
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(certificate);

        // Convert binary hash to hexadecimal representation
        String hash256 = new String(Hex.encodeHex(hash, false));

        // Apply formatting (e.g. grouping, truncation, or custom processing)
        return format(hash256).getBytes();
    }

    protected byte[] getHash(RSAPublicKey publicKey) throws EbicsException {
        byte[] digest = KeyUtil.getDigest(publicKey);
        // Format the hexadecimal digest and return as byte array
        return format(new String(Hex.encodeHex(digest, false))).getBytes();
    }

    /**
     * Formats a hash 256 input.
     *
     * @param hash256 the hash input
     * @return the formatted hash
     */
    private String format(String hash256) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < hash256.length(); i += 2) {
            builder.append(hash256.charAt(i));
            builder.append(hash256.charAt(i + 1));
            builder.append(' ');
        }
        return builder.substring(0, 48) + LINE_SEPARATOR + builder.substring(48) + LINE_SEPARATOR;
    }

    /**
     * The <code>Letter</code> object is the common template
     * for all initialization letter.
     *
     * @author Hachani
     */
    class Letter {

        private final Messages messages;
        private final String title;
        private final String hostId;
        private final String bankName;
        private final String userId;
        private final String username;
        private final String partnerId;
        private final String version;
        private ByteArrayOutputStream out;
        private Writer writer;

        /**
         * Constructs new <code>Letter</code> template
         *
         * @param title     the letter title
         * @param hostId    the host ID
         * @param bankName  the bank name
         * @param userId    the user ID
         * @param partnerId the partner ID
         * @param version   the signature version
         */
        @SuppressWarnings("java:S107")
        public Letter(String title,
                      String hostId,
                      String bankName,
                      String userId,
                      String username,
                      String partnerId,
                      String version,
                      Messages messages) {
            this.title = title;
            this.hostId = hostId;
            this.bankName = bankName;
            this.userId = userId;
            this.username = username;
            this.partnerId = partnerId;
            this.version = version;
            this.messages = messages;
        }

        /**
         * Builds the letter content.
         *
         * @param certTitle   the certificate title
         * @param certificate the certificate content
         * @param hashTitle   the hash title
         * @param hash        the hash content
         */
        public void build(String certTitle,
                          byte[] certificate,
                          String hashTitle,
                          byte[] hash)
                throws IOException {
            out = new ByteArrayOutputStream();
            writer = new PrintWriter(out, true);
            buildTitle();
            buildHeader();
            if (certificate != null)
                buildCertificate(certTitle, certificate);
            buildHash(hashTitle, hash);
            buildFooter();
            writer.close();
            out.flush();
            out.close();
        }

        /**
         * Builds the letter title.
         *
         */
        public void buildTitle() throws IOException {
            emit(title);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
        }

        // --------------------------------------------------------------------
        // DATA MEMBERS
        // --------------------------------------------------------------------

        /**
         * Builds the letter header
         *
         */
        public void buildHeader() throws IOException {
            emit(messages.getString("Letter.date"));
            appendSpacer();
            emit(formatDate(new Date()));
            emit(LINE_SEPARATOR);
            emit(messages.getString("Letter.time"));
            appendSpacer();
            emit(formatTime(new Date()));
            emit(LINE_SEPARATOR);
            emit(messages.getString("Letter.hostId"));
            appendSpacer();
            emit(hostId);
            emit(LINE_SEPARATOR);
            emit(messages.getString("Letter.bank"));
            appendSpacer();
            emit(bankName);
            emit(LINE_SEPARATOR);
            emit(messages.getString("Letter.userId"));
            appendSpacer();
            emit(userId);
            emit(LINE_SEPARATOR);
            emit(messages.getString("Letter.username"));
            appendSpacer();
            emit(username);
            emit(LINE_SEPARATOR);
            emit(messages.getString("Letter.partnerId"));
            appendSpacer();
            emit(partnerId);
            emit(LINE_SEPARATOR);
            emit(messages.getString("Letter.version"));
            appendSpacer();
            emit(version);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
        }

        /**
         * Writes the certificate core.
         *
         * @param title the title
         * @param cert  the certificate core
         */
        public void buildCertificate(String title, byte[] cert)
                throws IOException {
            emit(title);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit("-----BEGIN CERTIFICATE-----" + LINE_SEPARATOR);
            emit(new String(cert));
            emit("-----END CERTIFICATE-----" + LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
        }

        /**
         * Builds the hash section.
         *
         * @param title the title
         * @param hash  the hash value
         */
        public void buildHash(String title, byte[] hash)
                throws IOException {
            emit(title);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit(new String(hash));
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
            emit(LINE_SEPARATOR);
        }

        /**
         * Builds the footer section
         *
         */
        public void buildFooter() throws IOException {
            emit(messages.getString("Letter.date"));
            emit("                                  ");
            emit(messages.getString("Letter.signature"));
        }

        /**
         * Appends a spacer
         *
         */
        public void appendSpacer() throws IOException {
            emit("        ");
        }

        /**
         * Emits a text to the writer
         *
         * @param text the text to print
         */
        public void emit(String text) throws IOException {
            writer.write(text);
        }

        /**
         * Formats the input date
         *
         * @param date the input date
         * @return the formatted date
         */
        public String formatDate(Date date) {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    messages.getString("Letter.dateFormat"), locale);
            return formatter.format(date);
        }

        /**
         * Formats the input time
         *
         * @param time the input time
         * @return the formatted time
         */
        public String formatTime(Date time) {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    messages.getString("Letter.timeFormat"), locale);
            return formatter.format(time);
        }

        /**
         * Returns the letter content
         *
         * @return letter content as a <code>byte[]</code>
         */
        public byte[] getLetter() {
            return out.toByteArray();
        }
    }
}
