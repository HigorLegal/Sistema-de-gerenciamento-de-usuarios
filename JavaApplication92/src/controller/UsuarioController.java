/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import javax.swing.JOptionPane;
import model.Usuario;
import static sun.security.krb5.Confounder.bytes;
import utils.Utils;

/**
 *
 * @author s.lucas
 */
public class UsuarioController {

    public boolean autenticar(String email, String senha) {
        String sql = "SELECT * from TBUSUARIO "
                + "WHERE email = ? and senha = ? ";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;
        ResultSet resultado = null;

        try {
            comando = gerenciador.prepararComando(sql);

            comando.setString(1, email);
            comando.setString(2, senha);

            resultado = comando.executeQuery();
//se passar para a proxima linha ou seja achar um usuario ele retorna true
            if (resultado.next()) {
                return true;
            }

        } catch (SQLException e) {
            //mostra o erro
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            //fecha conxao
            gerenciador.fecharConexao(comando, resultado);
        }
        return false;
    }

    
    
    
    
    
    
    
    
    
    
    public boolean criarUsuario(Usuario usu) {
        //comando do sql
        String Sql = "insert into tbusuario (nome,email,senha,datanasc,ativo,image)value(?,?,?,?,?,?)";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;

        try {
            //usa o metodo para converter a imagem para bytes e coloca na variavel do tipo bytes iconBytes
            byte[]iconBytes = Utils.converterImagenToBytes(usu.getImage());
            
            //prepara a String sql e tranforma em um pra o uso, tambem é usado para alterar  o comando quando ha variaveis
            comando = gerenciador.prepararComando(Sql);

            //aqui esta sendo colocado cada atributo em cada variavel os "?" 1,2,3,4...                                    
            comando.setString(1, usu.getNome());
            comando.setString(2, usu.getEmail());
            comando.setString(3, usu.getSenha());
            comando.setDate(4, new java.sql.Date(usu.getDataNasc().getTime()));
            comando.setBoolean(5, usu.isAtivo());
            comando.setBytes(6, iconBytes);

            //vai executar o comando sql
            comando.executeUpdate();
            return true;

        } catch (SQLException e) {
//mostra o erro
            JOptionPane.showMessageDialog(null, "Erro" + e.getMessage());
        } finally {
//fecha a conexao 
            gerenciador.fecharConexao(comando);
        }
        return false;
    }
//lista todos os usuarios da DataBase

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public List<Usuario> listarUsuarios(int tipoFiltro, String filtro) {
        String sql = "SELECT pkusuario, nome, email, senha, datanasc, ativo from tbusuario ";

        if (!filtro.equals("")) {
            switch (tipoFiltro) {
                case 0:
                case 1:
                    sql += " where  nome  like ? ";
                    break;

                case 2:
                    sql += " where  email like ?";
                    break;

                default:
                    sql += " where  datanasc like ?";
                    break;

            }
        }
        List<Usuario> listaUsuarios = new ArrayList<>();

//cria um gerenciador de conexão
        GerenciadorConexao gerenciador = new GerenciadorConexao();
        //criar as variaveis vazias antes do try para preenchelas depois

        //ele é usado para complementar a pesquisa caso use uma variavel (os "?")
        PreparedStatement comando = null;

        //ele e o resultado do comando sql usado so quando precisa pegar algo do resultado como um select
        ResultSet resultado = null;

        try {
            //prepara a String sql e tranforma em um comando sql pra o uso, tambem é usado para alterar  o comando quando ha variaveis
            comando = gerenciador.prepararComando(sql);

            //atribuindo coisas nas variaveis
            if (!filtro.equals("")) {
                switch (tipoFiltro) {
                    case 0:
                        //nome
                        comando.setString(1, filtro + "%");
                        break;

                    case 1:
                        //contendo
                        comando.setString(1, "%" + filtro + "%");
                        break;

                    case 2:
                        //email
                        comando.setString(1, "%" + filtro + "%");
                        break;
                    default:
                        //data nascimento
                        comando.setString(1, "%" + filtro + "%");
                        break;
                }

            }
//isso vai executar o comando e guardar na variavel resultado
            resultado = comando.executeQuery();
            //resultado.next() vai passar para a proxima linha no resultado do sql e ele tambem retorna true se tiver outra linha para ser passada e false se nao tiver
            while (resultado.next()) {

                //cria um usuario para preche-lo
                Usuario usu = new Usuario();

//adicionando os atributos para o usuario
                usu.setPkusuario(resultado.getInt("pkusuario"));
                usu.setNome(resultado.getString("nome"));
                usu.setEmail(resultado.getString("email"));
                usu.setSenha("confidencial");
                usu.setDataNasc(resultado.getDate("dataNasc"));
                usu.setAtivo(resultado.getBoolean("ativo"));

//adicionando o usu na lista
                listaUsuarios.add(usu);

            }
//ele mostra o erro em um JOptinPane se algo der errado no sql
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());

            //esse finally sempre  vai ser executado no final mesmo se der errado 
        } finally {
            // ele fecha a conexao com o sql
            gerenciador.fecharConexao(comando, resultado);
        }
        return listaUsuarios;
    }

    
    
    
    
    
    
    
    
    
    
    
    public Usuario buscaPk(int id) {
        String sql = "SELECT pkusuario, nome, email, senha, datanasc, ativo , image from tbusuario where pkusuario = ?";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;
        ResultSet resultado = null;
        
        Usuario usu = new Usuario();
        try {
            comando = gerenciador.prepararComando(sql);
            
comando.setInt(1, id);
resultado = comando.executeQuery();
             if(resultado.next()) {

                      
                usu.setPkusuario(resultado.getInt("pkusuario"));
                usu.setNome(resultado.getString("nome"));
                usu.setEmail(resultado.getString("email"));
                usu.setSenha(resultado.getString("senha"));
                usu.setDataNasc(resultado.getDate("dataNasc"));
                usu.setAtivo(resultado.getBoolean("ativo"));
                
                byte [] bytes = resultado.getBytes("image");
                if(bytes != null){
                ByteArrayInputStream bis = new  ByteArrayInputStream(bytes);
                
                BufferedImage image = ImageIO.read(bis);
                
                usu.setImage(new ImageIcon (image));
                }
            }
        } catch (SQLException | IOException ex ) {
          Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE,null , ex);
          
        } finally {

            gerenciador.fecharConexao(comando, resultado);
        }
        return usu;
    }
    
    
     public Usuario buscaEmail(String email) {
        String sql = "SELECT pkusuario, nome, email, senha, datanasc, ativo , image from tbusuario where email = ?";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;
        ResultSet resultado = null;
        
        Usuario usu = new Usuario();
        try {
            comando = gerenciador.prepararComando(sql);
            
comando.setString(1, email);
resultado = comando.executeQuery();
             if(resultado.next()) {

                usu.setPkusuario(resultado.getInt("pkusuario"));
                usu.setNome(resultado.getString("nome"));
                usu.setEmail(resultado.getString("email"));
                usu.setSenha(resultado.getString("senha"));
                usu.setDataNasc(resultado.getDate("dataNasc"));
                usu.setAtivo(resultado.getBoolean("ativo"));
                
             byte [] bytes = resultado.getBytes("image");
                if(bytes != null){
                ByteArrayInputStream bis = new  ByteArrayInputStream(bytes);
                
                BufferedImage image = ImageIO.read(bis);
                
                usu.setImage(new ImageIcon (image));
                }

            }
         } catch (SQLException | IOException ex ) {
          Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE,null , ex);
          
        }finally {

            gerenciador.fecharConexao(comando, resultado);
        }
        return usu;
    }
    

    public boolean deletarUsuario(int pkusuario) {
        String sql = "DELETE FROM tbusuario WHERE pkusuario = ?";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;

        try {
            comando = gerenciador.prepararComando(sql);

            comando.setInt(1, pkusuario);

            comando.executeUpdate();

            return true;

        } catch (SQLException ex) {
            //mostra o erro
            JOptionPane.showMessageDialog(null, "erro ao excluir " + ex);
        } finally {
            //fecha conxao
            gerenciador.fecharConexao(comando);
        }
        return false;
    }
    
    
    
    
    
    
    
    public boolean AlterarUsuario(Usuario usu) {
        //comando do sql
        String Sql = "UPDATE tbusuario SET nome = ? ,  email = ? , datanasc =? , ativo = ?,senha = ? , image = ? WHERE pkusuario = ?";

        GerenciadorConexao gerenciador = new GerenciadorConexao();
        PreparedStatement comando = null;

        try {
            byte[]iconBytes = Utils.converterImagenToBytes(usu.getImage());
            //prepara a String sql e tranforma em um pra o uso, tambem é usado para alterar  o comando quando ha variaveis
            comando = gerenciador.prepararComando(Sql);

            //aqui esta sendo colocado cada atributo em cada variavel os "?" 1,2,3,4...                                    
            comando.setString(1, usu.getNome());
            comando.setString(2, usu.getEmail());
            comando.setDate(3, new java.sql.Date(usu.getDataNasc().getTime()));
            comando.setBoolean(4, usu.isAtivo());
            comando.setString(5, usu.getSenha());
            comando.setBytes(6, iconBytes);
            comando.setInt(7, usu.getPkusuario());
            

            //vai executar o comando sql
            comando.executeUpdate();
            return true;
        } catch (SQLException e) {
//mostra o erro
            JOptionPane.showMessageDialog(null, "Erro" + e.getMessage());
        } finally {
//fecha a conexao 
            gerenciador.fecharConexao(comando);
        }
        return false;
    }
}
//UPDATE `dbprojeto`.`tbusuario` SET `email` = 'rh@a', `senha` =  WHERE (`pkusuario` = '1');
