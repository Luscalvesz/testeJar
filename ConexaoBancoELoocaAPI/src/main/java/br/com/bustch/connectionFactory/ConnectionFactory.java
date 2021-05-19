package br.com.bustch.connectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public Connection retornarConexao() throws SQLException {

        Connection connection = DriverManager
                .getConnection("jdbc:sqlserver://bustech.database.windows.net;databaseName=bd-bustech","bustech","#Gfgrupo2");

        return connection;
    }
}
