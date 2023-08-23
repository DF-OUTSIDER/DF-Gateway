package com.easyusing.gateway.driver;

/**
 * @author outsider
 * @date 2023/7/27
 */
public interface ClientBase {

    /**
     * description: connect 连接
     * @param: host
     * @param: port
     * @return boolean
     * */
    boolean connect(String host, int port);

    /**
     * description: close 关闭连接
     * @return boolean
     * */
    boolean close();

    /**
     * description: isReady
     * @return boolean
     * */
    boolean isReady();

}
