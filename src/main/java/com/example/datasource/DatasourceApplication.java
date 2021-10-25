package com.example.datasource;


import com.example.datasource.mlw.MlwConfiguration;
import com.example.datasource.mlw.MlwDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatasourceApplication {

    public static void main(String[] args) throws SQLException {
        MlwConfiguration configuration = new MlwConfiguration();
        configuration.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/ds0?serverTimezone=UTC");
        configuration.setDriverClassName("com.mysql.cj.jdbc.Driver");
        configuration.setFastInit(true);
        configuration.setInitSize(5);
        configuration.setMaxSize(10);
        configuration.setUsername("root");
        configuration.setPassword("123456");

        MlwDataSource dataSource = new MlwDataSource(configuration);
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("select * from user where id_card = ?");
        preparedStatement.setString(1,"31060319940124003X");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            System.out.println(resultSet.getString(3));
        }
        connection.close();
    }

}
