import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CredentialProperties {
    private final static String fileName = "credentials.properties";
    private String login;
    private String password;

    CredentialProperties() throws IOException {
        InputStream inputStream = null;
        try {
            Properties props = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                props.load(inputStream);
            }
            else {
                throw new FileNotFoundException(String.format("Не удалось загрузить файл %s", fileName));
            }

            login = props.getProperty("login");
            password = props.getProperty("password");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
