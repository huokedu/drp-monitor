package me.sfeer.domain;

public class Host {
    private Long id;
    private String rssId;
    private String host;
    private String name;
    private Integer type;
    private String ip;
    private String port;
    private Long groupId;
    private Long templateId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRssId() {
        return rssId;
    }

    public void setRssId(String rssId) {
        this.rssId = rssId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return "Host{" +
                "id=" + id +
                ", rssId='" + rssId + '\'' +
                ", host='" + host + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", groupId=" + groupId +
                ", templateId=" + templateId +
                '}';
    }
}
