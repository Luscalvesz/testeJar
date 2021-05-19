package br.com.bustch.controller;

import br.com.bustch.connectionFactory.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Maquinas {

        private Connection connection;

        public Maquinas(){

            ConnectionFactory connectionFactory = new ConnectionFactory();
            try {
                this.connection = connectionFactory.retornarConexao();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

        public void create(Double maxRam, Double maxDisco, Double maxCpu, String hostname){
            PreparedStatement stm = null;
            try {
                String sql = "insert into maquinas (maxRAM, maxDisco, maxCPU, hostName) values(?,?,?,?)";
                stm = connection.prepareStatement(sql);
                stm.setDouble(1, maxRam);
                stm.setDouble(2, maxDisco);
                stm.setDouble(3, maxCpu);
                stm.setString(4, hostname);
                Boolean register = stm.execute();
                if(!register){
                    System.out.println("Registrado com sucesso!");
                }else{
                    System.out.println("Erro ao cadastrar");
                }
                connection.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

        public void delete(Integer id){
            PreparedStatement stm = null;
            try{
                String sql = "delete from maquinas where idMaquina = ?";
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
                        Double maxram = rs.getDouble("maxRAM");
                        Double maxdisco = rs.getDouble("maxDisco");
                        Double maxcpu = rs.getDouble("maxCPU");
                        String hostname = rs.getString("hostName");
                        System.out.println("maxRam    |   maxDisco |    maxCPU |    hostName");
                        System.out.println(maxram+ "            " + maxdisco + "         " + maxcpu + "       " + hostname);
                        System.out.println("   ");
                    }
                }else{
                    System.out.println("Erro ao listar colaboradores!");
                }
                connection.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

}


