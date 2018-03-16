package com.nhnent.eat.common.Config;

/**
 * Created by NHNEnt on 2017-02-02.
 */
public class CustomScenarioAPI {

    private boolean use;
    private String jarFile;
    private String apiClassName;

    public String getJarFile() {
        return jarFile;
    }

    public void setJarFile(String jarFile) {
        this.jarFile = jarFile;
    }

    public String getApiClassName() {
        return apiClassName;
    }

    public void setApiClassName(String apiClassName) {
        this.apiClassName = apiClassName;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }
}
