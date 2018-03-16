package com.nhnent.eat.common.Config;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Packet {

    private String pluginPackage;

    private String classPackage;

    private String pluginClass;

    private String[] bytePacketTypes;

    private PacketPackage[] packetPackages;

    @Expose(serialize = false, deserialize = false)
    private final HashMap<String, String> packages = new HashMap<>();

    /**
     * Get protocol buffer package name by given type
     * @param type type of protocol buffer package name(such as NGT, SampleGame or Sudda)
     * @return Packages
     */
    public String getPackage(String type) {
        if(packages.size() == 0) {
            for (PacketPackage packetPackage : packetPackages) {
                packages.put(packetPackage.getKey(), packetPackage.getPackageName());
            }
        }
        return packages.get(type);
    }


    public List<String> getPackageKeys()
    {
        List<String> packageKeys = new LinkedList<>();

        for (PacketPackage packetPackage : packetPackages) {
            packageKeys.add(packetPackage.getKey());
        }

        return packageKeys;
    }

    public String[] getBytePacketTypes() {
        return bytePacketTypes;
    }

    public void setBytePacketTypes(String[] bytePacketTypes) {
        this.bytePacketTypes = bytePacketTypes;
    }

    public PacketPackage[] getPacketPackages() {
        return packetPackages;
    }

    public void setPacketPackages(PacketPackage[] packetPackages) {
        this.packetPackages = packetPackages;
    }

    public String getPluginPackage() {
        return pluginPackage;
    }

    public void setPluginPackage(String pluginPackage) {
        this.pluginPackage = pluginPackage;
    }

    public String getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }

    public String getClassPackage() {
        return classPackage;
    }

    public void setClassPackage(String classPackage) {
        this.classPackage = classPackage;
    }
}
