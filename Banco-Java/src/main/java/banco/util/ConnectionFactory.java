package banco.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Factory de conexões implementada como Singleton.
 *
 * Ordem de prioridade das configurações:
 * 1) Arquivo bd/database.properties (chaves: db.url, db.user, db.password)
 * 2) Variáveis de ambiente (DB_URL, DB_USER, DB_PASSWORD)
 * 3) Valores padrão de fallback (localhost)
 */
public final class ConnectionFactory {

    private static volatile ConnectionFactory INSTANCE;

    private final String url;
    private final String user;
    private final String password;

    private ConnectionFactory() {
        // Valida a presença do driver do MySQL apenas uma vez na inicialização
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC do MySQL não encontrado no classpath.", e);
        }

        Properties props = new Properties();
        Path configPath = Paths.get("bd", "database.properties");

        try (InputStream in = new FileInputStream(configPath.toFile())) {
            props.load(in);
        } catch (IOException ignored) {
            // Se o arquivo de propriedades não existir, o fluxo segue para usar as variáveis de ambiente
        }

        this.url = props.getProperty("db.url", System.getenv().getOrDefault("DB_URL",
                "jdbc:mysql://localhost:3306/banco?serverTimezone=UTC&useSSL=false"));
        this.user = props.getProperty("db.user", System.getenv().getOrDefault("DB_USER", "root"));
        this.password = props.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", ""));
    }

    public static ConnectionFactory getInstance() {
        ConnectionFactory result = INSTANCE;
        if (result == null) {
            synchronized (ConnectionFactory.class) {
                result = INSTANCE;
                if (result == null) {
                    INSTANCE = result = new ConnectionFactory();
                }
            }
        }
        return result;
    }

    /**
     * Abre e retorna uma nova conexão usando o DriverManager.
     * Importante: O chamador é responsável por fechar a conexão (recomenda-se o uso de try-with-resources).
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}