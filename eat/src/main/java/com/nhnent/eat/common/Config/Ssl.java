package com.nhnent.eat.common.Config;

public class Ssl {
    private String keyCertChainPath;
    private String privateKeyPath;
    private String keyPassword;

    /**
     * get Key of certification chain path
     * @return Key of certification chain path
     */
    public String getKeyCertChainPath() {
        return keyCertChainPath;
    }

    /**
     * get Key of certification chain path
     * @param keyCertChainPath Key of certification chain path
     */
    public void setKeyCertChainPath(String keyCertChainPath) {
        this.keyCertChainPath = keyCertChainPath;
    }

    /**
     * Get private key of SSL
     * @return private key of SSL
     */
    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    /**
     * Set private key of SSL
     * @param privateKeyPath private key of SSL
     */
    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    /**
     * Get SSL Password
     * @return SSL Password
     */
    public String getKeyPassword() {
        return keyPassword;
    }

    /**
     * Set SSL Password
     * @param keyPassword SSL Password
     */
    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }
}
