package br.com.bustch.controller;

import br.com.bustch.connectionFactory.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Diagnosticos {

        private Connection connection;
        private Connection dockerConnection;

        public Diagnosticos(){

            ConnectionFactory connectionFactory = new ConnectionFactory();
            try {
//                this.dockerConnection = connectionFactory.dockerConnection();
                this.connection = connectionFactory.retornarConexao();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }
        public void update(Double ram, Long disco, Double processador, Integer maquina_id){
            PreparedStatement stm = null;
            try{
                String sql = "UPDATE tb_diagnosticos SET ram = ?, disco = ?, processador = ? WHERE maquina_id = ?";
                stm = connection.prepareStatement(sql);
                stm.setDouble(1, ram);
                stm.setLong(2, disco);
                stm.setDouble(3, processador);
                stm.setDouble(4, maquina_id);
                Boolean update = stm.execute();
                if(!update){
                    System.out.println("Registrado com sucesso! deu certo");
                }else{
                    System.out.println("Ocorreu um erro para registrar o dado!");
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }

        }

        public void createOnDocker(Double ram, Long disco, Double processador, Integer maquina_id){
            PreparedStatement stm = null;
            try {
                String sql = "insert into diagnosticos (ram, disco, processador, maquina_id) values(?,?,?,?)";
                stm = dockerConnection.prepareStatement(sql);
                stm.setDouble(1, ram);
                stm.setDouble(2, disco);
                stm.setDouble(3, processador);
                stm.setInt(4, maquina_id);
                Boolean register = stm.execute();
                if(!register){
                    System.out.println("Registrado com sucesso no docker!");
                }else{
                    System.out.println("Erro ao cadastrar");
                }
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


