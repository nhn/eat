package com.nhnent.eat.common.Config;

/**
 * Object for Ket/Value
 */
public class KeyValueObject {
    private String key;
    private String value;

    /**
     * Default constructor
     */
    public KeyValueObject() {}

    /**
     * Default constructor for give default value
     * @param key Key
     * @param value Value
     */
    public KeyValueObject(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Get Key
     * @return Key
     */
    public String getKey() {
        return key;
    }

    /**
     * Set Key
     * @param key Key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Get Value
     * @return Value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set Value
     * @param value Value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
