package br.com.bustch.metodos;
import br.com.bustch.core.Looca;
import br.com.bustch.getStatus.PegarDados;
import br.com.bustch.util.Conversor;
import br.com.bustch.controller.Colaborador;
import br.com.bustch.controller.Diagnosticos;
import br.com.bustch.controller.Maquinas;
import br.com.bustch.controller.Usuario;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscosGroup;
import com.github.britooo.looca.api.group.discos.Volume;

import java.text.DecimalFormat;
import java.util.List;

public class ChamarMetodos {

    public static void main(String[] args){

        PegarDados pd = new PegarDados();
        Colaborador ci = new Colaborador();
        Maquinas maq = new Maquinas();
        Diagnosticos dig = new Diagnosticos();
        Looca looca = new Looca();
        Conversor conversor = new Conversor();

        Usuario user = new Usuario();

        DiscosGroup grupoDeDiscos = looca.getGrupoDeDiscos();
        Long volumes = (grupoDeDiscos.getVolumes().get(0).getDisponivel() / 1000000);
        Double memoriaRam = (looca.getMemoria().getEmUso()) * 0.1;
        Double processador = looca.getProcessador().getUso();

//        maq.list("select * from tb_maquinas;");

        user.autenticar("usuario1", "senha1");



//        dig.createOnDocker(memoriaRam, volumes, processador, 1);
//        dig.create(memoriaRam, volumes, processador, 1);
//       dig.list("select * from diagnosticos");
//        System.out.println("------------------------------------");
//        dig.list("SELECT TOP 1 *\n" +
//                    "  FROM diagnosticos\n" +
//                    " ORDER\n" +
//                    "    BY idDiagnostico DESC;");
//        System.out.println("------------------------------------");
//
//        for (int i = 0; i < 10; i++) {
//              dig.create(memoriaRam, volumes, processador, 1);
//        }

    }
}
