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

        public void create(Boolean ativo, String latitude, String longitude, Double max_cpu, Double max_disco, Double max_ram, String senha, String tipo, String usuario, Integer empresa_id){
            PreparedStatement stm = null;
            try {
                String sql = "insert into tb_maquinas (ativo, latitude, longitude, max_cpu, max_disco, max_ram, senha, tipo, usuario, empresa_id) values(?,?,?,?,?,?,?,?,?,?)";
                stm = connection.prepareStatement(sql);
                stm.setBoolean(1, ativo);
                stm.setString(2, latitude);
                stm.setString(3, longitude);
                stm.setDouble(4, max_cpu);
                stm.setDouble(5, max_disco);
                stm.setDouble(6, max_ram);
                stm.setString(7, senha);
                stm.setString(8, tipo);
                stm.setString(9, usuario);
                stm.setInt(10, empresa_id);
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
                String sql = "delete from tb_maquinas where idMaquina = ?";
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
                        Boolean ativo = rs.getBoolean("ativo");
                        String latitude = rs.getString("latitude");
                        String longitude = rs.getString("longitude");
                        Double maxram = rs.getDouble("max_ram");
                        Double maxdisco = rs.getDouble("max_disco");
                        Double maxcpu = rs.getDouble("max_cpu");
                        String senha = rs.getString("senha");
                        String usuario = rs.getString("usuario");
                        String tipo = rs.getString("tipo");
                        Integer fkEmpresa = rs.getInt("empresa_id");

                        System.out.println("Usuario: " + usuario +
                                            "\n Senha: " + senha +
                                            "\n Tipo: " + tipo +
                                            "\n Latitude: " + latitude +
                                            "\n Longitude: "+ longitude +
                                            "\n Ativo: " + ativo +
                                            "\n Ram Maxima: " + maxram +
                                            "\n Cpu maxima: " + maxcpu +
                                            "\n Disco maximo: " + maxdisco +
                                            "\n fk Empresa: " + fkEmpresa);
                        System.out.println(" ---------------------------  ");
                    }
                }else{
                    System.out.println("Erro ao listar maquinas!");
                }
                connection.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

}


