package br.com.bustch.discos;

import oshi.software.os.OSFileStore;

/**
 *
 * @author Diego Brito diego.lima@bandtec.com.br  @Britooo on Github
 * @author Giuliana Miniguiti giuliana.franca@bandtec.com.br  @miniguiti on Github
 */
public class Volume {

    private final OSFileStore volume;

    public Volume(OSFileStore volume) {
        this.volume = volume;
    }

    public String getUUID() {
        return this.volume.getUUID();
    }

    public Long getTotal() {
        return this.volume.getTotalSpace();
    }

    public Long getDisponivel() {
        return this.volume.getFreeSpace();
    }

    public String getTipo() {
        return this.volume.getType();
    }

    public String getPontoDeMontagem() {
        return this.volume.getMount();
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();

      //  str.append("UUID: " + this.getUUID() + "\n");
     //   str.append("total: " + this.getTotal() + "\n");
        str.append(this.getDisponivel());
    //    str.append("tipo: " + this.getTipo() + "\n");
     //   str.append("ponto de montagem: " + this.getPontoDeMontagem() + "\n");

        return str.toString();
    }
}
