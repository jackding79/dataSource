package com.example.datasource.mlw;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class MlwDataSource implements DataSource {

    static final String [] DRIVERS = {"com.mysql.cj.jdbc.Driver"};

    private int initialSize = 5;
    private int maxSize = 10;
    private String username;
    private String password;
    private String driverClassName;
    private String jdbcUrl;
    private int index = -1;
    private int activeCount = 0;
    private boolean initialed = false;
    private Connection [] connections;
    private int timeout;

    public MlwDataSource(MlwConfiguration configuration){
        validate(configuration);
    }


    private void validate(MlwConfiguration configuration) {
        String username = configuration.getUsername();
        if (StringUtils.isEmpty(username)) {
            throw new RuntimeException("properties for username not be blank!");
        }
        this.username = username;
        this.password = configuration.getPassword();
        String jdbcUrl = configuration.getJdbcUrl();
        if (StringUtils.isEmpty(jdbcUrl)) {
            throw new RuntimeException("properties for jdbcUrl not be blank!");
        }
        this.jdbcUrl = jdbcUrl;
        String driverClassName = configuration. getDriverClassName();
        if (StringUtils.isEmpty(driverClassName)) {
            throw new RuntimeException("properties for driverClassName not be blank!");
        }
        this.driverClassName = driverClassName;
        if(!ArrayUtils.contains(DRIVERS,driverClassName)){
            throw new RuntimeException("unsupported driverClassName :" + driverClassName);
        }
        this.initialSize = configuration.getInitSize() == 0 ? initialSize : configuration.getInitSize();
        if(initialSize <= 0){
            throw new RuntimeException("initialSize must more than 0");
        }
        this.maxSize = configuration.getMaxSize() == 0 ? maxSize : configuration.getMaxSize();
        if(maxSize <= 0){
            throw new RuntimeException("maxSize must more than 0");
        }
        if(maxSize < initialSize){
            maxSize = initialSize;
        }
        this.timeout = configuration.getTimeout() < 0 ? 0 : configuration.getTimeout();
        connections = new Connection[maxSize];
    }

    public Connection getConnection() throws SQLException {
        init();
        return getLast();
    }

    private Connection getLast(){
        if(index < 0 ){
            if(activeCount < maxSize){
                Connection connection = createRealConnection();
                activeCount ++;
                return connection;
            }else{
                if(timeout <=0 ){
                    throw new RuntimeException("active count : "+ activeCount +" maxsize : "+maxSize);
                }else{
                    long startTime = System.currentTimeMillis();
                    while(System.currentTimeMillis() - startTime < timeout){
                        if(activeCount < maxSize){
                            Connection connection = connections[index];
                            activeCount ++;
                            index --;
                            return connection;
                        }
                    }
                    throw new RuntimeException("time out : "+ timeout);
                }
            }
        }
        Connection connection = connections[index];
        connections[index] = null;
        activeCount ++;
        index --;
        return connection;
    }



    public void init(){
        if(false == initialed){
            while(index < initialSize-1){
                Connection connection = createRealConnection();
                index ++;
                connections[index] = connection;
            }
            initialed = true;
        }
    }

    private Connection createRealConnection(){
        try{
            Class.forName(driverClassName);
        }catch (ClassNotFoundException e){
            throw new RuntimeException("can not find this driver class!");
        }
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            MlwConnectionHolder holder = new MlwConnectionHolder(connection,this);
            return new MlwConnection(holder);
        }catch (SQLException e){
            throw new RuntimeException("can not get connection!");
        }
    }

    private class DestoryTask implements Runnable{

        @Override
        public void run() {
            if(index > initialSize){
                for (Connection connection : connections){

                }
            }
        }
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("unsupported operation!");
    }


    public void recycle(Connection connection){
        connections[++index] = connection;
        System.out.println("recycle connection : " + connection);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
