package br.com.bustch.discos;

import java.util.List;
import java.util.stream.Collectors;

import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.Volume;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

/**
 *
 * @author Diego Brito diego.lima@bandtec.com.br  @Britooo on Github
 * @author Giuliana Miniguiti giuliana.franca@bandtec.com.br  @miniguiti on Github
 */
public class DiscosGroup {

    private SystemInfo system = new SystemInfo();
    private final OperatingSystem os = system.getOperatingSystem();
    private final HardwareAbstractionLayer hal = system.getHardware();

    public List<com.github.britooo.looca.api.group.discos.Volume> getVolumes() {

        return this.os.getFileSystem().getFileStores()
                .stream()
                .map(DiscosGroup::of)
                .collect(Collectors.toList());
    }

    public List<com.github.britooo.looca.api.group.discos.Disco> getDiscos() {
        return this.hal.getDiskStores().stream()
                .map(DiscosGroup::of)
                .collect(Collectors.toList());
    }

    public Long getTamanhoTotal() {
        return this.getDiscos().stream()
                .mapToLong(disco -> disco.getTamanho()).sum();
    }

    public Integer getQuantidadeDeDiscos() {
        return this.getDiscos().size();
    }

    public Integer getQuantidadeDeVolumes() {
        return this.getVolumes().size();
    }

    private static com.github.britooo.looca.api.group.discos.Volume of(OSFileStore volume) {
        if (volume == null) {
            return null;
        }
        return new Volume(volume);
    }

    private static com.github.britooo.looca.api.group.discos.Disco of(HWDiskStore disco) {
        if (disco == null) {
            return null;
        }
        return new Disco(disco);
    }
}
