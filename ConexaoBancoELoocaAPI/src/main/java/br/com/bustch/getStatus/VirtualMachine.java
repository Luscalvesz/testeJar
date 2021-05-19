package br.com.bustch.getStatus;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.util.Conversor;

import java.util.List;

public class VirtualMachine {
    public static void main(String[] args) {
        Conversor conversor = new Conversor();
        Looca looca = new Looca();

        Integer qualDisco = 1;
        List<Volume> loocaDiscos = (looca.getGrupoDeDiscos().getVolumes());
        // Pega o volume dos discos.
        for (Volume disco : loocaDiscos){
            System.out.println("Volume total do disco "+qualDisco+ ": "+ disco);
            qualDisco++;
        }
        System.out.println("---------------------------------------------");
        //Tamanho total dos discos

        //System.out.println(" Tamanho total dos discos: "+ conversor.formatarBytes(looca.getGrupoDeDiscos().getTamanhoTotal()));
        System.out.println("---------------------------------------------");
        // Pega a memoria em uso.
        System.out.println("Memoria em Uso: "+ looca.getMemoria().getEmUso());
        System.out.println("---------------------------------------------");
        //Pega a memoria total
        System.out.println("Memoria Total: " +conversor.formatarBytes(looca.getMemoria().getTotal()));
        System.out.println("---------------------------------------------");
        //Pega o uso do processador.
        System.out.println("Processador em Uso: "+ looca.getProcessador().getUso());
        System.out.println("---------------------------------------------");

    }
}
