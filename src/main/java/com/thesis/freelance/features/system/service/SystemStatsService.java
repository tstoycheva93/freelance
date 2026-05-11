package com.thesis.freelance.features.system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class SystemStatsService {

    private final DataSource dataSource;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("uptime", formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()));
        stats.put("version", readVersion());

        Runtime rt = Runtime.getRuntime();
        stats.put("jvmMemoryUsed", formatBytes(rt.totalMemory() - rt.freeMemory()));
        stats.put("jvmMemoryMax", formatBytes(rt.maxMemory()));

        stats.put("dbConnected", checkDbConnection());

        return stats;
    }

    private String formatUptime(long millis) {
        long days = millis / 86_400_000;
        long hours = (millis % 86_400_000) / 3_600_000;
        long minutes = (millis % 3_600_000) / 60_000;
        return String.format("%dd %dh %dm", days, hours, minutes);
    }

    private String formatBytes(long bytes) {
        double gb = bytes / 1_073_741_824.0;
        if (gb >= 0.9) {
            return String.format("%.1f GB", gb);
        }
        double mb = bytes / 1_048_576.0;
        return String.format("%.0f MB", mb);
    }

    private String readVersion() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("META-INF/maven/com.thesis.freelance/freelance/pom.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String v = props.getProperty("version");
                if (v != null) return "v" + v;
            }
        } catch (Exception ignored) {}
        return "v4.2.1";
    }

    private boolean checkDbConnection() {
        try (var conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}
