package me.friwi.jcefmaven;

/**
 * Configuration that is read from config.json
 *
 * @author Fritz Windisch
 */
public class Configuration {
    String workspaceDir;
    long crawlInterval;

    public Configuration(String workspaceDir, long crawlInterval) {
        this.workspaceDir = workspaceDir;
        this.crawlInterval = crawlInterval;
    }

    public String getWorkspaceDir() {
        return workspaceDir;
    }

    public void setWorkspaceDir(String workspaceDir) {
        this.workspaceDir = workspaceDir;
    }

    public long getCrawlInterval() {
        return crawlInterval;
    }

    public void setCrawlInterval(long crawlInterval) {
        this.crawlInterval = crawlInterval;
    }
}
