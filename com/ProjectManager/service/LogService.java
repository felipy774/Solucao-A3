package com.ProjectManager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogService {
    private static LogService instance;
    private List<LogEntry> logs;

    private LogService() {
        logs = new ArrayList<>();
    }

    public static LogService getInstance() {
        if (instance == null) {
            instance = new LogService();
        }
        return instance;
    }

    public void log(String userId, String action, String entityId, String description) {
        LogEntry entry = new LogEntry(userId, action, entityId, description, LocalDateTime.now());
        logs.add(entry);
    }

    public List<LogEntry> getEntriesForEntity(String entityId) {
        return logs.stream().filter(l -> l.getEntityId().equals(entityId)).collect(Collectors.toList());
    }

    public List<LogEntry> getAllLogs() {
        return new ArrayList<>(logs);
    }

    public static class LogEntry {
        private String userId;
        private String action;
        private String entityId;
        private String description;
        private LocalDateTime timestamp;

        public LogEntry(String userId, String action, String entityId, String description, LocalDateTime timestamp) {
            this.userId = userId;
            this.action = action;
            this.entityId = entityId;
            this.description = description;
            this.timestamp = timestamp;
        }

        public String getUserId() { return userId; }
        public String getAction() { return action; }
        public String getEntityId() { return entityId; }
        public String getDescription() { return description; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return "[" + timestamp + "] " + userId + " -> " + action + " (" + entityId + "): " + description;
        }
    }
}
