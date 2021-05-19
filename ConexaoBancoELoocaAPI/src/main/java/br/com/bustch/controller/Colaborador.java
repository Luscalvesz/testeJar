package br.com.bustch.controller;
import br.com.bustch.connectionFactory.ConnectionFactory;

import java.sql.*;

public class Colaborador{

        private Connection connection;

        public Colaborador(){

            ConnectionFactory connectionFactory = new ConnectionFactory();
            try {
                this.connection = connectionFactory.retornarConexao();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

        public void create(String nome, String usuario, String senha, Integer fkMaquina){
            PreparedStatement stm = null;
            try {
                String sql = "insert into colaboradores (nome, usuario, senha, fkMaquina) values(?,?,?,?)";
                stm = connection.prepareStatement(sql);
                stm.setString(1, nome);
                stm.setString(2, usuario);
                stm.setString(3, senha);
                stm.setInt(4, fkMaquina);
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
                String sql = "delete from colaboradores where idColaborador = ?";
                stm = connection.prepareStatement(sql);
                stm.setInt(1, id);
                Boolean delete = stm.execute();
                if(!delete){
                    System.out.println("O id "+id+" foi deletado com sucesso!");
                }else{
                    System.out.println("Erro ao cadastrar!");
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
                        String nome = rs.getString("nome");
                        String usuario = rs.getString("usuario");
                        String senha = rs.getString("senha");
                        Integer fkMaquina = rs.getInt("fkMaquina");
                        System.out.println("Nome    |   Usuario |    Senha |    fkMaquina");
                        System.out.println(nome+ " " + usuario + " " + senha + "    " + fkMaquina);
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


