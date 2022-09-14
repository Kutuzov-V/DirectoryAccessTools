package kutuzov.soc_tools;

import kutuzov.soc_tools.entities.config.Config;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;
import javax.crypto.*;


public class ConfigManager {
    private static final String STORAGE_PATH = ".\\Config";
    private static final String SECRET_KEY_PATH = "C:\\ProgramData\\DirectoryAccessTool";


    public static void setBaseCatalogInConfig(Config config, String baseCatalogPath){
        config.setBaseCatalog(baseCatalogPath);
    }
    public static Config readConfigFromOpenFile() {
        StringBuffer sb = new StringBuffer();
        try {
            FileInputStream fileInputStream = new FileInputStream(".\\config.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            int i;
            while ((i = inputStreamReader.read()) != -1) {
                sb.append((char) i);
            }

            //System.out.println(sb.toString());
        } catch (IOException io) {
            System.out.println("Error: can't read config-file (config.txt)");
            io.printStackTrace();
        }

        Config config = parseConfig(sb.toString());
        return config;
    }
    public static void saveProtectionConfigsToFile(Config config) {
        File storageDirectory = new File(STORAGE_PATH);
        File secretKeyDirectory = new File(SECRET_KEY_PATH);

        //Checking that a config directory for logs is exists
        if (!(storageDirectory.exists() & storageDirectory.isDirectory())) {
            storageDirectory.mkdir();
        }

        //Checking that a secret key directory is exists
        if (!(secretKeyDirectory.exists() & secretKeyDirectory.isDirectory())) {
            secretKeyDirectory.mkdir();
        }

        try {
            //Save secret key to file
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(SECRET_KEY_PATH + "\\Key.k"));

            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            Key k = keygen.generateKey();
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, k);

            objectOutputStream.writeObject(k);
            objectOutputStream.flush();
            objectOutputStream.close();

            //Converting Config-object to byte array through serialization and put this array to ByteArrayOutputStream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(bos);
            objectOutputStream.writeObject(config);

            //Encryption byte array, that contain Config-object, and write it the file
            FileOutputStream fs = new FileOutputStream(STORAGE_PATH + "\\Config.cfg");
            CipherOutputStream out = new CipherOutputStream(fs, aes);
            out.write(bos.toByteArray());
            out.flush();
            out.close();


        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException saveConfigException) {
            saveConfigException.printStackTrace();
        }

    }
    public static Config loadProtectConfigFromFile() {
        Config config;

        try {

            FileInputStream fis = new FileInputStream(SECRET_KEY_PATH + "\\Key.k");
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            Key k = (Key) objectInputStream.readObject();

            /*
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            Key k = keygen.generateKey();
            */

            fis = new FileInputStream(STORAGE_PATH + "\\Config.cfg");
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, k);

            CipherInputStream in = new CipherInputStream(fis, aes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int numberOfBytedRead;
            while ((numberOfBytedRead = in.read(b)) >= 0) {
                baos.write(b, 0, numberOfBytedRead);
            }
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            config = (Config) objectInputStream.readObject();

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | ClassNotFoundException ex) {
            config = null;
            ex.printStackTrace();
        }

        return config;
    }




    private static Config parseConfig(String configData) {
        Config config = new Config();
        String[] strings = configData.split("\r\n");

        String providerURL = parseProviderURL(strings);
        config.setProviderURL(providerURL);

        String[] ldapCatalogs = parseLdapCatalogs(strings);
        config.setAdSearchCatalogs(ldapCatalogs);

        String baseCatalog = parseBaseCatalog(strings);
        config.setBaseCatalog(baseCatalog);

        return config;
    }

    private static String parseProviderURL(String[] configData) {
        String result = new String();

        for (int i = 0; i < configData.length; i++) {
            if (configData[i].contentEquals("Provider URL:")) {
                if (configData[i + 1].contains("ldap://")) {
                    result = configData[i + 1];
                } else {
                    result = "Bad URL";
                }
            }
        }
        return result;
    }

    private static String[] parseLdapCatalogs(String[] configData) {
        ArrayList<String> ldapCatalogs = new ArrayList<>();
        int i = 0;
        int startIndexCatalogList = -1;

        while (i < configData.length) {
            if (configData[i].contentEquals("LDAP catalog list:")) {
                startIndexCatalogList = i + 1;
            }
            i++;
        }

        i = startIndexCatalogList;
        while (i < configData.length) {
            if (configData[i].startsWith("OU=")) {
                ldapCatalogs.add(configData[i]);
            }
            i++;
        }

        String[] ldapCatalogsStrings = new String[ldapCatalogs.size()];
        ldapCatalogs.toArray(ldapCatalogsStrings);

        return ldapCatalogsStrings;
    }

    private static String parseBaseCatalog(String[] configData) {
        String result = new String();

        for (int i = 0; i < configData.length; i++) {
            if (configData[i].contentEquals("Base catalog:")) {
                if (new File(configData[i + 1]).exists()) {
                    result = configData[i + 1];
                } else {
                    result = "Bad file path. Path not exist or incorrect. Input path:" + configData[i+1];
                }
                break;
            }
        }
        return result;
    }

    protected static String getStoragePath() {
        return STORAGE_PATH;
    }
}
