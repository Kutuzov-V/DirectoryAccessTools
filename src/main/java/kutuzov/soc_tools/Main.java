package kutuzov.soc_tools;




import javax.naming.NamingException;
import java.io.IOException;
import java.security.GeneralSecurityException;



public class Main {

    private static final String FILENAME = "C:\\Users\\sb32\\Desktop\\Новая папка";

    public static void main(String[] args) throws IOException, NamingException, GeneralSecurityException {
        Runner.run("CONFIG FROM FILE, PATH FROM USER");
    }
}
