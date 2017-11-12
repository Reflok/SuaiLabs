package ui;

import ui.Interface;


public interface Chat {
    public void send(String message) throws Exception;
    public void setName(String name);
    public void closeSocket();
    public String getName();
    public void  setExit(boolean b);
}
