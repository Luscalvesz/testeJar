package br.com.bustch.getStatus;

import br.com.bustch.core.Looca;
import br.com.bustch.util.Conversor;
import br.com.bustch.controller.Colaborador;
import br.com.bustch.controller.Diagnosticos;
import br.com.bustch.controller.Maquinas;
import com.github.britooo.looca.api.group.discos.DiscosGroup;

import java.sql.SQLOutput;

public class PegarDados {

    Colaborador ci = new Colaborador();
    Maquinas maq = new Maquinas();
    Diagnosticos dig = new Diagnosticos();
    Looca looca = new Looca();
    Conversor conversor = new Conversor();

    public void pegarDados(){
        DiscosGroup grupoDeDiscos = looca.getGrupoDeDiscos();
        Long volumes = (grupoDeDiscos.getVolumes().get(0).getDisponivel() / 1000000);
        Double memoriaRam = (looca.getMemoria().getEmUso()) * 0.1;
        Double processador = looca.getProcessador().getUso();



        dig.create(memoriaRam, volumes, processador, 1);
        dig.createOnDocker(memoriaRam, volumes, processador, 1);
    }
}
