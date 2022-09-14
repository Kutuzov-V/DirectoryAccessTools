package kutuzov.soc_tools;

import kutuzov.soc_tools.entities.config.Config;
import kutuzov.soc_tools.entities.fileSystemModel.Directory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.naming.NamingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.TreeMap;

public class Runner {

    public static void run(String runMode) throws IOException, NamingException {
        Config config = null;
        switch (runMode){
            case "CONFIG FROM FILE":
                config = getConfigFromFile();
                break;
            case "CONFIG FROM FILE, PATH FROM USER":
                config = getConfigFromFileAndPathFromUser();
                break;
            default:
                System.out.println("Неверно указан режим работы программы. Проверьте правильность вводимых параметров...");
                return;
        }

        TreeMap<String,String> adUsers = kutuzov.soc_tools.LDAPExplorer.getUsers(config);
        TreeMap<String, Directory> accessMatrix = kutuzov.soc_tools.AccessListExtractor.extractAccessMatrix(config.getBaseCatalog(), adUsers);
        //kutuzov.soc_tools.Reporter.printAccessList(accessMatrix);
        HSSFWorkbook exelReport = kutuzov.soc_tools.Reporter.writeExelReport(accessMatrix);
        ExelStyler.applyStyle(exelReport,"directory1");


        try (OutputStream fileOut = new FileOutputStream("Access Matrix.xls")) {
            exelReport.write(fileOut);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    private static Config getConfigFromFile() throws IOException {

        System.out.println("Используется режим скрипта.\n" +
                "1) Если программа запущена впервые,то все входные конфигурационные " +
                "данные загружаются из файла " + new File(".\\").getCanonicalPath()+ "\\config.txt" +
                "\nВ этом случае будут запрошены логин и пароль для доступа к контроллеру домена, для получения списка пользователей\n");

        System.out.println("2)Если программа ранее уже была запущена, то входные данные будут загружены из защищённого " +
                "специального конфигурационного файла " + new File(ConfigManager.getStoragePath()).getCanonicalPath() + "\\config.cfg \n");

        System.out.println("В случае, если требуется изменить исходные данные (изменить логин, пароль, каталоги AD или путь до сканируемого каталога) - удалите папку " + new File(ConfigManager.getStoragePath()).getCanonicalPath() + "\\config.cfg \n");


        Config config = ConfigManager.loadProtectConfigFromFile();
        if (config == null) {
            String login = null;
            String pass = null;
            config = ConfigManager.readConfigFromOpenFile();
            System.out.println("Введите логин (в формате user@domain.local)");
            Scanner scanner = new Scanner(System.in);

            if(scanner.hasNext()){
                login = scanner.next();
            }
            System.out.println("Введите пароль");
            if(scanner.hasNext()){
                pass = scanner.next();
            }
            scanner.close();

            config.setLogin(login);
            config.setPass(pass);
            ConfigManager.saveProtectionConfigsToFile(config);
            System.out.println();
        }
        return config;
    }

    private static Config getConfigFromFileAndPathFromUser() throws IOException {
        System.out.println("Используется режим скрипта с ручным вводом пути к анализируемому каталогу.\n" +
                "1) Если программа запущена впервые,то все входные конфигурационные (кроме адреса каталога) " +
                "данные загружаются из файла " + new File(".\\").getCanonicalPath()+ "\\config.txt" +
                "\nВ этом случае будут запрошены логин и пароль для доступа к контроллеру домена, для получения списка пользователей\n");

        System.out.println("2)Если программа ранее уже была запущена, то входные данные будут загружены из защищённого " +
                "специального конфигурационного файла " + new File(ConfigManager.getStoragePath()).getCanonicalPath() + "\\config.cfg \n");

        System.out.println("В случае, если требуется изменить исходные данные (изменить логин, пароль, каталоги AD или путь до сканируемого каталога) - удалите папку " + new File(ConfigManager.getStoragePath()).getCanonicalPath() + "\\config.cfg \n");

        String baseCatalog = null;
        System.out.println("Введите полный путь к анализируемой директории (Например: C:\\Program Files):");
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNext()){
            baseCatalog = scanner.next();
        }

        Config config = ConfigManager.loadProtectConfigFromFile();
        if (config == null) {
            String login = null;
            String pass = null;

            config = ConfigManager.readConfigFromOpenFile();

            System.out.println("Введите полный путь к анализируемой директории (Например: C:\\Program Files):");

            if(scanner.hasNext()){
                baseCatalog = scanner.next();
            }

            System.out.println("Введите логин (в формате user@domain.local):");
            if(scanner.hasNext()){
                login = scanner.next();
            }

            System.out.println("Введите пароль:");
            if(scanner.hasNext()){
                pass = scanner.next();
            }
            scanner.close();

            config.setLogin(login);
            config.setPass(pass);
            config.setBaseCatalog(baseCatalog);

            ConfigManager.saveProtectionConfigsToFile(config);
            System.out.println();
        }

        config.setBaseCatalog(baseCatalog);
        return config;
    }

}
