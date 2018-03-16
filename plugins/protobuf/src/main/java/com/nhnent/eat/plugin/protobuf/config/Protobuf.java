package com.nhnent.eat.plugin.protobuf.config;

/**
 * Created by NHNEnt on 2016-11-04.
 */
public class Protobuf {

    private String protobufLibraryJarPath;
    private String serviceId;
    private String headerPackageClassName;
    private String java8BinPath;
    private ProtoBufInfo[] protoBufInfos;

    public String getProtobufLibraryJarPath() {
        return protobufLibraryJarPath;
    }

    public void setProtobufLibraryJarPath(String protobufLibraryJarPath) {
        this.protobufLibraryJarPath = protobufLibraryJarPath;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    /**
     * Get Header Package name
     * @return Header Package name
     */
    public String getHeaderPackageClassName() {
        return headerPackageClassName;
    }

    /**
     * Set Header Package name
     * @param headerPackageClassName Header Package name
     */
    public void setHeaderPackageClassName(String headerPackageClassName) {
        this.headerPackageClassName = headerPackageClassName;
    }

    public ProtoBufInfo[] getProtoBufInfos() {
        return protoBufInfos;
    }

    public void setProtoBufInfos(ProtoBufInfo[] protoBufInfos) {
        this.protoBufInfos = protoBufInfos;
    }

    public String getJava8BinPath() {
        return java8BinPath;
    }

    public void setJava8BinPath(String java8BinPath) {
        this.java8BinPath = java8BinPath;
    }
}
