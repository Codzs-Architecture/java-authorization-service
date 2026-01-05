package com.codzs.framework.util.database;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.util.StringUtils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for testing database connectivity.
 * Supports both standard and SSL certificate-based connections.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Slf4j
public class DatabaseConnectionTestUtil {

    /**
     * Tests database connection with optional certificate.
     * Auto-detects whether to use certificate-based SSL or standard connection.
     * 
     * @param connectionString MongoDB connection string
     * @param certificate PEM certificate (optional, can be null or empty)
     * @return ConnectionTestResult with success status and error message
     */
    public static boolean testConnection(String connectionString, String certificate) {
        try {
            MongoClientSettings settings = StringUtils.hasText(certificate) 
                ? buildWithCertificate(connectionString, certificate)
                : buildStandard(connectionString);
                
            try (MongoClient client = MongoClients.create(settings)) {
                // Perform simple ping operation to test connectivity
                client.getDatabase("admin").runCommand(new Document("ping", 1));
                log.debug("Database connection test successful for connection: {}", 
                    maskConnectionString(connectionString));
                return true;
            }
        } catch (Exception e) {
            log.debug("Connection test failed for connection {}: {}", 
                maskConnectionString(connectionString), e.getMessage());
            throw new RuntimeException("Database connection test failed: " + e.getMessage(), e);
        }
    }

    /**
     * Tests database connection without certificate (standard connection).
     * 
     * @param connectionString MongoDB connection string
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection(String connectionString) {
        return testConnection(connectionString, null);
    }

    /**
     * Builds standard MongoDB client settings with basic timeouts.
     */
    private static MongoClientSettings buildStandard(String connectionString) {
        return MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .applyToSocketSettings(builder -> 
                builder.connectTimeout(5, TimeUnit.SECONDS)
                       .readTimeout(3, TimeUnit.SECONDS))
            .applyToClusterSettings(builder -> 
                builder.serverSelectionTimeout(5, TimeUnit.SECONDS))
            .build();
    }

    /**
     * Builds MongoDB client settings with custom SSL certificate.
     */
    private static MongoClientSettings buildWithCertificate(String connectionString, String certificate) {
        SSLContext sslContext = createSSLContext(certificate);
        return MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .applyToSocketSettings(builder -> 
                builder.connectTimeout(5, TimeUnit.SECONDS)
                       .readTimeout(3, TimeUnit.SECONDS))
            .applyToClusterSettings(builder -> 
                builder.serverSelectionTimeout(5, TimeUnit.SECONDS))
            .applyToSslSettings(builder -> 
                builder.enabled(true)
                       .context(sslContext)
                       .invalidHostNameAllowed(false))
            .build();
    }

    /**
     * Creates SSL context with custom PEM certificate.
     */
    private static SSLContext createSSLContext(String pemCertificate) {
        try {
            // Parse PEM certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(
                new ByteArrayInputStream(pemCertificate.getBytes())
            );
            
            // Create KeyStore with the certificate
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setCertificateEntry("mongodb-cert", cert);
            
            // Create TrustManager with the KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            );
            tmf.init(trustStore);
            
            // Create SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SSL context from certificate", e);
        }
    }

    /**
     * Masks sensitive information in connection string for logging.
     */
    private static String maskConnectionString(String connectionString) {
        if (connectionString == null) return "null";
        
        // Simple masking - hide password and show only protocol and host
        try {
            ConnectionString cs = new ConnectionString(connectionString);
            String hosts = cs.getHosts().isEmpty() ? "unknown" : cs.getHosts().get(0);
            return String.format("%s://*****@%s", cs.isSrvProtocol() ? "mongodb+srv" : "mongodb", hosts);
        } catch (Exception e) {
            return "mongodb://***masked***";
        }
    }

}