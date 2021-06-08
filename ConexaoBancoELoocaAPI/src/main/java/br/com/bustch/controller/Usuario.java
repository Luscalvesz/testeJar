package br.com.bustch.controller;

import br.com.bustch.getStatus.PegarDados;
import br.com.bustch.connectionFactory.ConnectionFactory;
import br.com.bustch.core.Looca;
import br.com.bustch.util.Conversor;
import com.github.britooo.looca.api.group.discos.DiscosGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import oshi.util.FormatUtil;

public class Usuario {

    Diagnosticos dig = new Diagnosticos();

    private String usuario;
    private String senha;

    private Integer idDiagnostico = 0;
    private Integer fkMaquina = 0;
    private Boolean userAutenticated = false;
    Looca looca = new Looca();
    Conversor conversor = new Conversor();
    
        private Connection connection;
         PegarDados pg = new PegarDados();

        public Usuario(){
            ConnectionFactory connectionFactory = new ConnectionFactory();
            try {
                this.connection = connectionFactory.retornarConexao();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

        public void create(String nome, String login, String senha){
            PreparedStatement stm = null;
            try {
                String sql = "insert into maquinas (maxRAM, maxDisco, maxCPU, hostName) values(?,?,?)";
                stm = connection.prepareStatement(sql);
                stm.setString(1, nome);
                stm.setString(2, login);
                stm.setString(3, senha);
                Boolean register = stm.execute();
                if(!register){
                    System.out.println("Usuario registrado com sucesso!");
                }else{
                    System.out.println("Erro ao cadastrar usuario!");
                }
                connection.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

        public void delete(Integer id){
            PreparedStatement stm = null;
            try{
                String sql = "delete from usuario where idUsuario = ?";
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
                        Integer id = rs.getInt("id");
                        String nome = rs.getString("nome");
                        String login = rs.getString("login");
                        String senha = rs.getString("senha");
                        System.out.println("Id: "+ id + " Nome: " + nome + " Login: "+login+ " Senha: " + senha);
                        System.out.println("   ");
                    }
                }else{
                    System.out.println("Erro ao listar usuarios");
                }
                connection.close();
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }

        public void autenticar(String usuario, String senha){
            PreparedStatement stm = null;
            try{
                stm = connection.prepareStatement("select * from tb_maquinas where usuario = ?");
                stm.setString(1, usuario);
                Boolean userExist = stm.executeQuery().next();
                ResultSet rs = stm.getResultSet();
                if(userExist){
                    System.out.println("Login efetuado com sucesso!");
                    userAutenticated = true;
                    Integer id = rs.getInt("id");
                    fkMaquina = id;
                    this.usuario = usuario;
                    this.senha = senha;
                    System.out.println(fkMaquina);
                    
                    
                    
                    
                    
                    DiscosGroup grupoDeDiscos = looca.getGrupoDeDiscos();
                    Long volumes = (grupoDeDiscos.getVolumes().get(0).getDisponivel() / 1000000);
                    Double memoriaRam = (looca.getMemoria().getEmUso()) * 0.1;
                    Double processador = looca.getProcessador().getUso();

                    dig.update(memoriaRam, volumes, processador, getFkMaquina());

                    
                }else{
                    System.out.println("Erro ao tentar fazer o login");
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }

    public Boolean getUserAutenticated() {
        return userAutenticated;
    }

    public Integer getFkMaquina() {
        return fkMaquina;
    }

    public void setFkMaquina(Integer fkMaquina) {
        this.fkMaquina = fkMaquina;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}