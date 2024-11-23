package com.identitye2e.runner;

public interface RunnableItem {

    void run() throws BrowserException;

    String getDescription();

    boolean getStatus();

    void setStatus(boolean status);

    String getResult();

    void setResult(String result);
}
