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

package org.kopi.ebics.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.exception.NoDownloadDataAvailableException;
import org.kopi.ebics.interfaces.Configuration;
import org.kopi.ebics.interfaces.EbicsBank;
import org.kopi.ebics.interfaces.EbicsOrderType;
import org.kopi.ebics.interfaces.EbicsUser;
import org.kopi.ebics.interfaces.InitLetter;
import org.kopi.ebics.interfaces.LetterManager;
import org.kopi.ebics.interfaces.PasswordCallback;
import org.kopi.ebics.io.IOUtils;
import org.kopi.ebics.messages.Messages;
import org.kopi.ebics.session.DefaultConfiguration;
import org.kopi.ebics.session.EbicsSession;
import org.kopi.ebics.session.OrderType;
import org.kopi.ebics.session.Product;
import org.kopi.ebics.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ebics client application. Performs necessary tasks to contact the ebics
 * bank server like sending the INI, HIA and HPB requests for keys retrieval and
 * also performs the files transfer including uploads and downloads.
 */
public class EbicsClient {
    private static final Logger logger = LoggerFactory.getLogger(EbicsClient.class);
    public static final String SKIP_ORDER = "skip_order";

    static {
        // this is for the logging config
        System.setProperty("ebicsBasePath", getRootDir().getAbsolutePath());
    }

    static {
        org.apache.xml.security.Init.init();
        java.security.Security.addProvider(new BouncyCastleProvider());
    }

    private final Configuration configuration;
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Partner> partners = new HashMap<>();
    private final Map<String, Bank> banks = new HashMap<>();
    private final ConfigProperties properties;
    private final Messages messages;
    private Product defaultProduct;
    private User defaultUser;

    /**
     * Constructs a new ebics client application
     *
     * @param configuration the application configuration
     * @param properties
     */
    public EbicsClient(Configuration configuration, ConfigProperties properties) {
        this.configuration = configuration;
        this.properties = properties;
        Messages.setLocale(configuration.getLocale());
        this.messages = new Messages(Constants.APPLICATION_BUNDLE_NAME, configuration.getLocale());
        logger.info(messages.getString("init.configuration"));
        configuration.init();
    }

    private static File getRootDir() {
        return new File(
                System.getProperty("user.home") + File.separator + "ebics" + File.separator + "client");
    }

    private static CommandLine parseArguments(Options options, String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        options.addOption(null, "help", false, "Print this help text");
        CommandLine line = parser.parse(options, args);
        if (line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            System.out.println();
            formatter.printHelp(EbicsClient.class.getSimpleName(), options);
            System.out.println();
            System.exit(0);
        }
        return line;
    }

    public static EbicsClient createEbicsClient(File rootDir, File configFile) throws IOException {
        ConfigProperties properties = new ConfigProperties(configFile);
        final String language = properties.get("languageCode").toLowerCase();
        final String productName = properties.get("productName");

        final Locale locale = Locale.forLanguageTag(language);

        DefaultConfiguration configuration = new DefaultConfiguration(rootDir.getAbsolutePath(),
                properties.properties) {

            @Override
            public Locale getLocale() {
                return locale;
            }
        };

        EbicsClient client = new EbicsClient(configuration, properties);

        Product product = new Product(productName, language, null);

        client.setDefaultProduct(product);

        return client;
    }

    private static void addOption(Options options, EbicsOrderType type, String description) {
        options.addOption(null, type.getCode().toLowerCase(), false, description);
    }

    private static boolean hasOption(CommandLine cmd, EbicsOrderType type) {
        return cmd.hasOption(type.getCode().toLowerCase());
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        addOption(options, OrderType.INI, "Send INI request");
        addOption(options, OrderType.HIA, "Send HIA request");
        addOption(options, OrderType.HPB, "Send HPB request");
        options.addOption(null, "letters", false, "Create INI Letters");
        options.addOption(null, "create", false, "Create and initialize EBICS user");
        addOption(options, OrderType.PTK, "Fetch client protocol file (TXT)");
        addOption(options, OrderType.HAC, "Fetch client protocol file (XML)");

        addOption(options, OrderType.XKD, "Send payment order file (DTA format)");

        options.addOption(null, SKIP_ORDER, true, "Skip a number of order ids");

        options.addOption("o", "output", true, "output file");
        options.addOption("i", "input", true, "input file");


        CommandLine cmd = parseArguments(options, args);

        File defaultRootDir = getRootDir();
        File ebicsClientProperties = new File(defaultRootDir, "ebics.txt");
        EbicsClient client = createEbicsClient(defaultRootDir, ebicsClientProperties);

        if (cmd.hasOption("create")) {
            client.createDefaultUser();
        } else {
            client.loadDefaultUser();
        }

        if (cmd.hasOption("letters")) {
            client.createLetters(client.defaultUser, false);
        }

        if (hasOption(cmd, OrderType.INI)) {
            client.sendINIRequest(client.defaultUser, client.defaultProduct);
        }
        if (hasOption(cmd, OrderType.HIA)) {
            client.sendHIARequest(client.defaultUser, client.defaultProduct);
        }
        if (hasOption(cmd, OrderType.HPB)) {
            client.sendHPBRequest(client.defaultUser, client.defaultProduct);
        }

        String outputFileValue = cmd.getOptionValue("o");
        String inputFileValue = cmd.getOptionValue("i");

        List<? extends EbicsOrderType> fetchFileOrders = Arrays.asList(OrderType.PTK, OrderType.HAC);

        for (EbicsOrderType type : fetchFileOrders) {
            if (hasOption(cmd, type)) {
                client.fetchFile(getOutputFile(outputFileValue), client.defaultUser,
                        client.defaultProduct, type, null, false, null, null);
                break;
            }
        }

        List<? extends EbicsOrderType> sendFileOrders = Collections.singletonList(OrderType.XKD);
        for (EbicsOrderType type : sendFileOrders) {
            if (hasOption(cmd, type)) {
                client.sendFile(new File(inputFileValue), client.defaultUser,
                        client.defaultProduct, type, null);
                break;
            }
        }

        if (cmd.hasOption(SKIP_ORDER)) {
            int count = Integer.parseInt(cmd.getOptionValue(SKIP_ORDER));
            while (count-- > 0) {
                client.defaultUser.getPartner().nextOrderId();
            }
        }
        client.quit();
    }

    private static File getOutputFile(String outputFileName) {
        if (outputFileName == null || outputFileName.isEmpty()) {
            throw new IllegalArgumentException("outputFileName not set");
        }
        File file = new File(outputFileName);
        if (file.exists()) {
            throw new IllegalArgumentException("file already exists " + file);
        }
        return file;
    }

    private EbicsSession createSession(User user, Product product) {
        EbicsSession session = new EbicsSession(user, configuration);
        session.setProduct(product);
        return session;
    }

    /**
     * Creates the user necessary directories
     *
     * @param user the concerned user
     */
    public void createUserDirectories(EbicsUser user) {
        logger.info(
                messages.getString("user.create.directories", user.getUserId()));
        IOUtils.createDirectories(configuration.getUserDirectory(user));
        IOUtils.createDirectories(configuration.getTransferTraceDirectory(user));
        IOUtils.createDirectories(configuration.getKeystoreDirectory(user));
        IOUtils.createDirectories(configuration.getLettersDirectory(user));
    }

    /**
     * Creates a new EBICS bank with the data you should have obtained from the
     * bank.
     *
     * @param url            the bank URL
     * @param name           the bank name
     * @param hostId         the bank host ID
     * @param useCertificate does the bank use certificates ?
     * @return the created ebics bank
     */
    private Bank createBank(URL url, String name, String hostId, boolean useCertificate) {
        Bank bank = new Bank(url, name, hostId, useCertificate);
        banks.put(hostId, bank);
        return bank;
    }

    /**
     * Creates a new ebics partner
     *
     * @param bank      the bank
     * @param partnerId the partner ID
     */
    private Partner createPartner(EbicsBank bank, String partnerId) {
        Partner partner = new Partner(bank, partnerId);
        partners.put(partnerId, partner);
        return partner;
    }

    /**
     * Creates a new ebics user and generates its certificates.
     *
     * @param url              the bank url
     * @param bankName         the bank name
     * @param hostId           the bank host ID
     * @param partnerId        the partner ID
     * @param userId           UserId as obtained from the bank.
     * @param name             the user name,
     * @param email            the user email
     * @param country          the user country
     * @param organization     the user organization or company
     * @param useCertificates  does the bank use certificates ?
     * @param saveCertificates save generated certificates?
     * @param passwordCallback a callback-handler that supplies us with the password. This
     *                         parameter can be null, in this case no password is used.
     * @return Ebics <code>User</code>
     * @throws Exception
     */
    public User createUser(URL url, String bankName, String hostId, String partnerId,
                           String userId, String name, String email, String country, String organization,
                           boolean useCertificates, boolean saveCertificates, PasswordCallback passwordCallback)
            throws Exception {
        logger.info(messages.getString("user.create.info", userId));

        Bank bank = createBank(url, bankName, hostId, useCertificates);
        Partner partner = createPartner(bank, partnerId);
        try {
            User user = new User(partner, userId, name, email, country, organization,
                    passwordCallback);
            createUserDirectories(user);
            if (saveCertificates) {
                user.saveUserCertificates(configuration.getKeystoreDirectory(user));
            }
            configuration.getSerializationManager().serialize(bank);
            configuration.getSerializationManager().serialize(partner);
            configuration.getSerializationManager().serialize(user);
            createLetters(user, useCertificates);
            users.put(userId, user);
            partners.put(partner.getPartnerId(), partner);
            banks.put(bank.getHostId(), bank);

            logger.info(messages.getString("user.create.success", userId));
            return user;
        } catch (Exception e) {
            logger.error(messages.getString("user.create.error"), e);
            throw e;
        }
    }

    private void createLetters(EbicsUser user, boolean useCertificates)
            throws GeneralSecurityException, IOException, EbicsException {
        user.getPartner().getBank().setUseCertificate(useCertificates);
        LetterManager letterManager = configuration.getLetterManager();
        List<InitLetter> letters = Arrays.asList(letterManager.createA005Letter(user),
                letterManager.createE002Letter(user), letterManager.createX002Letter(user));

        File directory = new File(configuration.getLettersDirectory(user));
        for (InitLetter letter : letters) {
            try (FileOutputStream out = new FileOutputStream(new File(directory, letter.getName()))) {
                letter.writeTo(out);
            }
        }
    }

    /**
     * Loads a user knowing its ID
     *
     * @throws Exception
     */
    public User loadUser(String hostId, String partnerId, String userId,
                         PasswordCallback passwordCallback) throws Exception {
        logger.info(messages.getString("user.load.info", userId));

        try {
            Bank bank;
            Partner partner;
            User user;
            try (ObjectInputStream input = configuration.getSerializationManager().deserialize(
                    hostId)) {
                bank = (Bank) input.readObject();
            }
            try (ObjectInputStream input = configuration.getSerializationManager().deserialize(
                    "partner-" + partnerId)) {
                partner = new Partner(bank, input);
            }
            try (ObjectInputStream input = configuration.getSerializationManager().deserialize(
                    "user-" + userId)) {
                user = new User(partner, input, passwordCallback);
            }
            users.put(userId, user);
            partners.put(partner.getPartnerId(), partner);
            banks.put(bank.getHostId(), bank);
            logger.info(messages.getString("user.load.success", userId));
            return user;
        } catch (Exception e) {
            logger.error(messages.getString("user.load.error"), e);
            throw e;
        }
    }

    /**
     * Sends an INI request to the ebics bank server
     *
     * @param user    the user
     * @param product the application product
     * @throws Exception
     */
    public void sendINIRequest(User user, Product product) throws Exception {
        String userId = user.getUserId();
        logger.info(messages.getString("ini.request.send", userId));
        if (user.isInitialized()) {
            logger.info(messages.getString("user.already.initialized", userId));
            return;
        }
        EbicsSession session = createSession(user, product);
        KeyManagement keyManager = new KeyManagement(session);
        configuration.getTraceManager().setTraceDirectory(
                configuration.getTransferTraceDirectory(user));
        try {
            keyManager.sendINI(null);
            user.setInitialized(true);
            logger.info(messages.getString("ini.send.success", userId));
        } catch (Exception e) {
            logger.error(messages.getString("ini.send.error", userId), e);
            throw e;
        }
    }

    /**
     * Sends a HIA request to the ebics server.
     *
     * @param user    the user ID.
     * @param product the application product.
     * @throws Exception
     */
    public void sendHIARequest(User user, Product product) throws Exception {
        String userId = user.getUserId();
        logger.info(messages.getString("hia.request.send", userId));
        if (user.isInitializedHIA()) {
            logger
                    .info(messages.getString("user.already.hia.initialized", userId));
            return;
        }
        EbicsSession session = createSession(user, product);
        KeyManagement keyManager = new KeyManagement(session);
        configuration.getTraceManager().setTraceDirectory(
                configuration.getTransferTraceDirectory(user));
        try {
            keyManager.sendHIA(null);
            user.setInitializedHIA(true);
        } catch (Exception e) {
            logger.error(messages.getString("hia.send.error", userId), e);
            throw e;
        }
        logger.info(messages.getString("hia.send.success", userId));
    }

    /**
     * Sends a HPB request to the ebics server.
     */
    public void sendHPBRequest(User user, Product product) throws Exception {
        String userId = user.getUserId();
        logger.info(messages.getString("hpb.request.send", userId));

        EbicsSession session = createSession(user, product);
        KeyManagement keyManager = new KeyManagement(session);

        configuration.getTraceManager().setTraceDirectory(
                configuration.getTransferTraceDirectory(user));

        try {
            keyManager.sendHPB();
            logger.info(messages.getString("hpb.send.success", userId));
        } catch (Exception e) {
            logger.error(messages.getString("hpb.send.error", userId), e);
            throw e;
        }
    }

    /**
     * Sends the SPR order to the bank.
     *
     * @param user    the user ID
     * @param product the session product
     * @throws Exception
     */
    public void revokeSubscriber(User user, Product product) throws Exception {
        String userId = user.getUserId();

        logger.info(messages.getString("spr.request.send", userId));

        EbicsSession session = createSession(user, product);
        KeyManagement keyManager = new KeyManagement(session);

        configuration.getTraceManager().setTraceDirectory(
                configuration.getTransferTraceDirectory(user));

        try {
            keyManager.lockAccess();
        } catch (Exception e) {
            logger.error(messages.getString("spr.send.error", userId), e);
            throw e;
        }

        logger.info(messages.getString("spr.send.success", userId));
    }

    /**
     * Sends a file to the ebics bank server
     *
     * @throws Exception
     */
    public void sendFile(File file, User user, Product product, EbicsOrderType orderType, UploadService uploadService) throws Exception {
        EbicsSession session = createSession(user, product);

        FileTransfer transferManager = new FileTransfer(session);

        configuration.getTraceManager().setTraceDirectory(
                configuration.getTransferTraceDirectory(user));

        try {
            transferManager.sendFile(IOUtils.getFileContent(file), orderType, uploadService);
        } catch (IOException | EbicsException e) {
            logger.error(messages.getString("upload.file.error", file.getAbsolutePath()), e);
            throw e;
        }
    }

    public void sendFile(File file, EbicsOrderType orderType, UploadService uploadService) throws Exception {
        sendFile(file, defaultUser, defaultProduct, orderType, uploadService);
    }

    public void fetchFile(File file, User user, Product product, EbicsOrderType orderType, DownloadService downloadService,
                          boolean isTest, Date start, Date end) throws IOException, EbicsException {
        FileTransfer transferManager;
        EbicsSession session = createSession(user, product);
        session.addSessionParam("FORMAT", "pain.xxx.cfonb160.dct");
        if (isTest) {
            session.addSessionParam("TEST", "true");
        }
        transferManager = new FileTransfer(session);

        configuration.getTraceManager().setTraceDirectory(
                configuration.getTransferTraceDirectory(user));

        try {
            transferManager.fetchFile(orderType, downloadService, start, end, file);
        } catch (NoDownloadDataAvailableException e) {
            // don't log this exception as an error, caller can decide how to handle
            throw e;
        } catch (Exception e) {
            logger.error(messages.getString("download.file.error"), e);
            throw e;
        }
    }

    public void fetchFile(File file, EbicsOrderType orderType, DownloadService downloadService, Date start, Date end) throws IOException,
            EbicsException {
        fetchFile(file, defaultUser, defaultProduct, orderType, downloadService, false, start, end);
    }

    /**
     * Performs buffers save before quitting the client application.
     */
    public void quit() throws IOException {
        try {
            for (User user : users.values()) {
                if (user.needsSave()) {
                    logger
                            .info(messages.getString("app.quit.users", user.getUserId()));
                    configuration.getSerializationManager().serialize(user);
                }
            }

            for (Partner partner : partners.values()) {
                if (partner.needsSave()) {
                    logger
                            .info(messages.getString("app.quit.partners", partner.getPartnerId()));
                    configuration.getSerializationManager().serialize(partner);
                }
            }

            for (Bank bank : banks.values()) {
                if (bank.needsSave()) {
                    logger
                            .info(messages.getString("app.quit.banks", bank.getHostId()));
                    configuration.getSerializationManager().serialize(bank);
                }
            }
        } catch (EbicsException e) {
            logger.info(messages.getString("app.quit.error"));
        }

        clearTraces();
    }

    public void clearTraces() throws IOException {
        logger.info(messages.getString("app.cache.clear"));
        configuration.getTraceManager().clear();
    }

    private User createUser(ConfigProperties properties, PasswordCallback pwdHandler)
            throws Exception {
        String userId = properties.get("userId");
        String partnerId = properties.get("partnerId");
        String bankUrl = properties.get("bank.url");
        String bankName = properties.get("bank.name");
        String hostId = properties.get("hostId");
        String userName = properties.get("user.name");
        String userEmail = properties.get("user.email");
        String userCountry = properties.get("user.country");
        String userOrg = properties.get("user.org");
        boolean useCertificates = false;
        boolean saveCertificates = true;
        return createUser(new URI(bankUrl).toURL(), bankName, hostId, partnerId, userId, userName, userEmail,
                userCountry, userOrg, useCertificates, saveCertificates, pwdHandler);
    }

    public void createDefaultUser() throws Exception {
        defaultUser = createUser(properties, createPasswordCallback());
    }

    public void loadDefaultUser() throws Exception {
        String userId = properties.get("userId");
        String hostId = properties.get("hostId");
        String partnerId = properties.get("partnerId");
        defaultUser = loadUser(hostId, partnerId, userId, createPasswordCallback());
    }

    private PasswordCallback createPasswordCallback() {
        final String password = properties.get("password");
        return new PasswordCallback() {

            @Override
            public char[] getPassword() {
                return password.toCharArray();
            }
        };
    }

    private void setDefaultProduct(Product product) {
        this.defaultProduct = product;
    }

    public User getDefaultUser() {
        return defaultUser;
    }

    public static class ConfigProperties {
        Properties properties = new Properties();

        public ConfigProperties(File file) throws IOException {
            properties.load(new FileInputStream(file));
        }

        public String get(String key) {
            String value = properties.getProperty(key);
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException("property not set or empty: " + key);
            }
            return value.trim();
        }
    }
}
