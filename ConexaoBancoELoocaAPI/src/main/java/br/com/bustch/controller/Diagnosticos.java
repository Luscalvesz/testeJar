package br.com.bustch.controller;

import br.com.bustch.connectionFactory.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Diagnosticos {

        private Connection connection;

        public Diagnosticos(){

            ConnectionFactory connectionFactory = new ConnectionFactory();
            try {
                this.connection = connectionFactory.retornarConexao();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

        public void create(Double ram, Long disco, Double processador, Integer fkMaquina){
            PreparedStatement stm = null;
            try {
                String sql = "insert into diagnosticos (ram, disco, processador, fkMaquina) values(?,?,?,?)";
                stm = connection.prepareStatement(sql);
                stm.setDouble(1, ram);
                stm.setDouble(2, disco);
                stm.setDouble(3, processador);
                stm.setInt(4, fkMaquina);
                Boolean register = stm.execute();
                if(!register){
                    System.out.println("Registrado com sucesso!");
                }else{
                    System.out.println("Erro ao cadastrar");
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

        public void delete(Integer id){
            PreparedStatement stm = null;
            try{
                String sql = "delete from diagonosticos where idDiagonosticos = ?";
                stm = connection.prepareStatement(sql);
                stm.setInt(1, id);
                Boolean delete = stm.execute();
                if(!delete){
                    System.out.println("O id "+id+" foi deletado com sucesso!");
                }else{
                    System.out.println("Erro ao deletar!");
                }
                connection.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }
        public void list(String sqlCode){
            PreparedStatement stm = null;
            try{
                String sql = sqlCode;
                stm = connection.prepareStatement(sql);
                Boolean list = stm.execute();
                ResultSet rs = stm.getResultSet();
                if(list){
                    while(rs.next()){
                        Double ram = rs.getDouble("ram");
                        Double disco = rs.getDouble("disco");
                        Double processador = rs.getDouble("processador");
                        String fkmaquina = rs.getString("fkMaquina");
                        System.out.println("Ram    |   Disco |    Processador |    fkMaquina");
                        System.out.println(ram+ "            " + disco + "         " + processador + "       " + fkmaquina);
                        System.out.println("   ");
                    }
                }else{
                    System.out.println("Erro ao listar diagnosticos!");
                }
                connection.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

}


