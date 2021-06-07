/*
 * The MIT License
 *
 * Copyright 2021 Admin.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.bustch.telasApp;

import br.com.bustch.controller.Colaborador;
import br.com.bustch.controller.Diagnosticos;
import br.com.bustch.controller.Maquinas;
import br.com.bustch.controller.Usuario;
import br.com.bustch.core.Looca;
import br.com.bustch.getStatus.PegarDados;
import br.com.bustch.util.Conversor;
import com.github.britooo.looca.api.group.discos.DiscosGroup;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import oshi.util.FormatUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import br.com.bustch.telasApp.Slack;

/**
 *
 * @author Admin
 */
public class TelaDashboards extends javax.swing.JFrame {
    
    PegarDados pd = new PegarDados();
    Colaborador ci = new Colaborador();
    Maquinas maq = new Maquinas();
    Diagnosticos dig = new Diagnosticos();
    Looca looca = new Looca();
    Conversor conversor = new Conversor();
    Usuario user = new Usuario();
    Looca apiLooca = new Looca();
    Timer timer = new Timer();
    JSONObject json = new JSONObject();
    
    
    final long SEGUNDOS = (1000 * 3);
    
    
    public TelaDashboards() {
        initComponents();
        timer.scheduleAtFixedRate(timerDados, 0, SEGUNDOS);
    }
 
    TimerTask timerDados = new TimerTask() {
        @Override
        public void run() {
            try {
                Double restanteCPU = 100 - apiLooca.getProcessador().getUso();
                Integer qtdDiscos = apiLooca.getGrupoDeDiscos().getQuantidadeDeDiscos();
                
                DiscosGroup grupoDeDiscos = looca.getGrupoDeDiscos();
                Long volumes = (grupoDeDiscos.getVolumes().get(0).getDisponivel() / 1000000);
                Double memoriaRam = (looca.getMemoria().getEmUso()) * 0.1;
                Double processador = looca.getProcessador().getUso();
                FormatUtil.formatBytes(apiLooca.getMemoria().getEmUso());
                
                System.out.println(apiLooca.getMemoria().getDisponivel());
                
                
                dig.update(memoriaRam, volumes, processador, user.getFkMaquina());
                
                lblProcesadorTotal.setText(apiLooca.getProcessador().getNumeroCpusFisicas().toString());
                lblProcesadorEmUso.setText(String.format("%.2f", apiLooca.getProcessador().getUso()));
                lblProcesadorRestante.setText(String.format("%.2f", restanteCPU));
                
                if (restanteCPU <= 20.0) {
                    lblStatusCPU.setText("Crítico");
                    lblProcesadorRestante.setForeground(Color.red);
                    lblStatusCPU.setForeground(Color.red);
                    
                    json.put("text", ":exclamation::exclamation::exclamation:CPU crítico:exclamation::exclamation::exclamation:");
                    Slack.sendMessage(json);
                    
                    String caminhoPasta = String.format("/home/%s/logBusTech/logBusTech.dat", System.getProperty("user.name"));
                    try {
                        RandomAccessFile log = new RandomAccessFile(caminhoPasta, "rw");

                        log.seek(log.length());

                        log.writeBytes("\nTeste" + 2 + "oba");

                        log.close();
                    } catch (FileNotFoundException e) {
                        System.err.printf("Arquivo no encontrado: %s.\n", e.getMessage());
                    }
                } else {
                    lblStatusCPU.setText("Ok");
                    lblProcesadorRestante.setForeground(Color.blue);
                    lblStatusCPU.setForeground(Color.blue);
                }
                
                lblDiscoTotal.setText(FormatUtil.formatBytes(apiLooca.getGrupoDeDiscos().getTamanhoTotal()));
                lblQtdDisco.setText(apiLooca.getGrupoDeDiscos().getQuantidadeDeDiscos().toString());
                
                    Long discoUsado = apiLooca.getGrupoDeDiscos().getTamanhoTotal() - apiLooca.getGrupoDeDiscos().getVolumes().get(0).getDisponivel();
                    String discoRestante = FormatUtil.formatBytes(apiLooca.getGrupoDeDiscos().getVolumes().get(0).getDisponivel());
                    
                    lblRestanteDisco.setText(discoRestante);
                            
                    if (discoRestante.contains(" GiB")) {
                        discoRestante = discoRestante.replace(" GiB", "");
                        discoRestante = discoRestante.replace(",", ".");
                    } else if (discoRestante.contains(" MiB")) {
                        discoRestante = discoRestante.replace(" GiB", "");
                        discoRestante = discoRestante.replace(",", ".");
                    } else {
                        discoRestante = discoRestante.replace(" bytes", "");
                        discoRestante = discoRestante.replace(",", ".");
                    }
                    
                    Double valueDouble = Double.parseDouble(discoRestante);
                    
                    if (valueDouble <= 10.0) {
                        lblStatusDisco.setText("Crítico");
                        lblStatusDisco.setForeground(Color.red);
                        lblRestanteDisco.setForeground(Color.red);
                        
                        json.put("text", ":exclamation::exclamation::exclamation:Disco crítico:exclamation::exclamation::exclamation:");
                        Slack.sendMessage(json);
                        
                        String caminhoPasta = String.format("/home/%s/logBusTech/logBusTech.dat", System.getProperty("user.name"));
                        try {
                            RandomAccessFile log = new RandomAccessFile(caminhoPasta, "rw");

                            log.seek(log.length());

                            log.writeBytes("\nTeste");

                            log.close();
                        } catch (FileNotFoundException e) {
                            System.err.printf("Arquivo n?o encontrado: %s.\n", e.getMessage());
                        }
                    } else {
                        lblStatusDisco.setText("Ok");
                        lblRestanteDisco.setForeground(Color.blue);
                        lblStatusDisco.setForeground(Color.blue);
                    }
                    
                    lblCPU9.setText("Disponível do Disco " + 1 + ":");
                    lblDiscoUsado.setText(FormatUtil.formatBytes(discoUsado));
                
                
                lblRAMTotal.setText(FormatUtil.formatBytes(apiLooca.getMemoria().getTotal()));
                lblRAMUso.setText(FormatUtil.formatBytes(apiLooca.getMemoria().getEmUso()));
                lblRAMRestante.setText(FormatUtil.formatBytes(apiLooca.getMemoria().getDisponivel()));
                
                if (apiLooca.getMemoria().getDisponivel() <= 400000000) {
                    lblStatusRAM.setText("Crítico");
                    lblStatusRAM.setForeground(Color.red);
                    lblRAMRestante.setForeground(Color.red);
                    
                    json.put("text", ":exclamation::exclamation::exclamation:RAM crítica:exclamation::exclamation::exclamation:");
                    Slack.sendMessage(json);
                    
                    String caminhoPasta = String.format("/home/%s/logBusTech/logBusTech.dat", System.getProperty("user.name"));
                    try {
                        RandomAccessFile log = new RandomAccessFile(caminhoPasta, "rw");

                        log.seek(log.length());

                        log.writeBytes("\nTeste");

                        log.close();
                    } catch (FileNotFoundException e) {
                        System.err.printf("Arquivo n?o encontrado: %s.\n", e.getMessage());
                    }
                } else {
                    lblStatusRAM.setText("Ok");
                    lblRAMRestante.setForeground(Color.blue);
                    lblStatusRAM.setForeground(Color.blue);
                }
            } catch (IOException ex) {
                Logger.getLogger(TelaDashboards.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(TelaDashboards.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblRAMUso = new javax.swing.JLabel();
        lblCPU3 = new javax.swing.JLabel();
        lblCPU6 = new javax.swing.JLabel();
        lblRAMTotal = new javax.swing.JLabel();
        lblCPU8 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblRAMRestante = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblStatusRAM = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lblQtdDisco = new javax.swing.JLabel();
        lblCPU5 = new javax.swing.JLabel();
        lblCPU4 = new javax.swing.JLabel();
        lblDiscoTotal = new javax.swing.JLabel();
        lblCPU9 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblDiscoUsado = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblStatusDisco = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblRestanteDisco = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblCPU2 = new javax.swing.JLabel();
        lblCPU1 = new javax.swing.JLabel();
        lblCPU7 = new javax.swing.JLabel();
        lblProcesadorEmUso = new javax.swing.JLabel();
        lblProcesadorRestante = new javax.swing.JLabel();
        lblProcesadorTotal = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblStatusCPU = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(255, 193, 49));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Bustech - Soluções de Hardware Inteligente");
        jPanel5.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(52, 16, -1, 30));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 3, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 11, 3, 40));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 60));

        jPanel4.setBackground(new java.awt.Color(73, 73, 73));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(73, 73, 73));
        jLabel2.setText("Dashboard");
        jLabel2.setToolTipText("");

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semilight", 0, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(244, 208, 52));
        jLabel1.setText("                Dashboard");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(64, 64, 64))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(416, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 250, 500));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 20)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(73, 73, 73));
        jLabel4.setText("Tempo Real");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, -1, -1));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(73, 73, 73), 2));

        lblRAMUso.setBackground(new java.awt.Color(255, 193, 49));
        lblRAMUso.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblRAMUso.setForeground(new java.awt.Color(73, 73, 73));

        lblCPU3.setBackground(new java.awt.Color(255, 193, 49));
        lblCPU3.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblCPU3.setForeground(new java.awt.Color(73, 73, 73));
        lblCPU3.setText("Em uso:");

        lblCPU6.setBackground(new java.awt.Color(255, 193, 49));
        lblCPU6.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblCPU6.setForeground(new java.awt.Color(73, 73, 73));
        lblCPU6.setText("Total:");

        lblRAMTotal.setBackground(new java.awt.Color(255, 193, 49));
        lblRAMTotal.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblRAMTotal.setForeground(new java.awt.Color(73, 73, 73));

        lblCPU8.setBackground(new java.awt.Color(255, 193, 49));
        lblCPU8.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblCPU8.setForeground(new java.awt.Color(73, 73, 73));
        lblCPU8.setText("Restantes:");

        jPanel11.setBackground(new java.awt.Color(255, 193, 49));

        jLabel6.setBackground(new java.awt.Color(255, 193, 49));
        jLabel6.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(73, 73, 73));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("RAM");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        lblRAMRestante.setBackground(new java.awt.Color(255, 193, 49));
        lblRAMRestante.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblRAMRestante.setForeground(new java.awt.Color(73, 73, 73));

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(73, 73, 73));
        jLabel12.setText("Status:");

        lblStatusRAM.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblStatusRAM.setForeground(new java.awt.Color(73, 73, 73));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(lblCPU8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRAMRestante)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 353, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatusRAM))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(lblCPU6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblRAMTotal))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(lblCPU3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblRAMUso)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCPU6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRAMTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblRAMUso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCPU3))
                .addGap(12, 12, 12)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRAMRestante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCPU8)
                        .addComponent(jLabel12)
                        .addComponent(lblStatusRAM, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 530, 140));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(73, 73, 73), 2));

        lblQtdDisco.setBackground(new java.awt.Color(255, 193, 49));
        lblQtdDisco.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblQtdDisco.setForeground(new java.awt.Color(73, 73, 73));

        lblCPU5.setBackground(new java.awt.Color(255, 193, 49));
        lblCPU5.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblCPU5.setForeground(new java.awt.Color(73, 73, 73));
        lblCPU5.setText("Quantidade de Discos: ");

        lblCPU4.setBackground(new java.awt.Color(73, 73, 73));
        lblCPU4.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblCPU4.setForeground(new java.awt.Color(73, 73, 73));
        lblCPU4.setText("Total todos os Discos:");

        lblDiscoTotal.setBackground(new java.awt.Color(255, 193, 49));
        lblDiscoTotal.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblDiscoTotal.setForeground(new java.awt.Color(73, 73, 73));

        lblCPU9.setBackground(new java.awt.Color(255, 193, 49));
        lblCPU9.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblCPU9.setForeground(new java.awt.Color(73, 73, 73));
        lblCPU9.setText("Disponível:");

        jPanel10.setBackground(new java.awt.Color(255, 193, 49));

        jLabel7.setBackground(new java.awt.Color(255, 193, 49));
        jLabel7.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(73, 73, 73));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("DISCO");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        lblDiscoUsado.setBackground(new java.awt.Color(255, 193, 49));
        lblDiscoUsado.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblDiscoUsado.setForeground(new java.awt.Color(73, 73, 73));

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(73, 73, 73));
        jLabel10.setText("Status:");

        lblStatusDisco.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblStatusDisco.setForeground(new java.awt.Color(73, 73, 73));

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(73, 73, 73));
        jLabel13.setText("Restante:");

        lblRestanteDisco.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblRestanteDisco.setForeground(new java.awt.Color(73, 73, 73));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(lblCPU4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDiscoTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRestanteDisco, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(lblCPU5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQtdDisco)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(lblCPU9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDiscoUsado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatusDisco)))
                .addContainerGap())
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblCPU4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDiscoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(lblRestanteDisco, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblQtdDisco, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCPU5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatusDisco, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCPU9)
                        .addComponent(jLabel10))
                    .addComponent(lblDiscoUsado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 530, 140));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(73, 73, 73), 2));

        jPanel9.setBackground(new java.awt.Color(255, 193, 49));

        jLabel5.setBackground(new java.awt.Color(255, 193, 49));
        jLabel5.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(73, 73, 73));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("CPU");
        jLabel5.setToolTipText("");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(73, 73, 73));
        jLabel11.setText("Status:");

        lblCPU2.setBackground(new java.awt.Color(73, 73, 73));
        lblCPU2.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblCPU2.setForeground(new java.awt.Color(73, 73, 73));
        lblCPU2.setText("Número de cores do processador:");

        lblCPU1.setBackground(new java.awt.Color(73, 73, 73));
        lblCPU1.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblCPU1.setForeground(new java.awt.Color(73, 73, 73));
        lblCPU1.setText("Em uso:");

        lblCPU7.setBackground(new java.awt.Color(73, 73, 73));
        lblCPU7.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblCPU7.setForeground(new java.awt.Color(73, 73, 73));
        lblCPU7.setText("Restantes:");

        lblProcesadorEmUso.setBackground(new java.awt.Color(255, 193, 49));
        lblProcesadorEmUso.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblProcesadorEmUso.setForeground(new java.awt.Color(73, 73, 73));

        lblProcesadorRestante.setBackground(new java.awt.Color(255, 193, 49));
        lblProcesadorRestante.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblProcesadorRestante.setForeground(new java.awt.Color(73, 73, 73));

        lblProcesadorTotal.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblProcesadorTotal.setForeground(new java.awt.Color(73, 73, 73));

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(73, 73, 73));
        jLabel8.setText("%");

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(73, 73, 73));
        jLabel9.setText("%");

        lblStatusCPU.setFont(new java.awt.Font("Dialog", 0, 19)); // NOI18N
        lblStatusCPU.setForeground(new java.awt.Color(73, 73, 73));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(lblCPU7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblProcesadorRestante)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatusCPU))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(lblCPU1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblProcesadorEmUso)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(lblCPU2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblProcesadorTotal)))
                        .addGap(0, 217, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCPU2)
                    .addComponent(lblProcesadorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(lblCPU1)
                    .addComponent(lblProcesadorEmUso, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(lblCPU7)
                    .addComponent(lblProcesadorRestante, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(lblStatusCPU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 530, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 550, 500));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaDashboards.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaDashboards.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaDashboards.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaDashboards.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDashboards().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblCPU1;
    private javax.swing.JLabel lblCPU2;
    private javax.swing.JLabel lblCPU3;
    private javax.swing.JLabel lblCPU4;
    private javax.swing.JLabel lblCPU5;
    private javax.swing.JLabel lblCPU6;
    private javax.swing.JLabel lblCPU7;
    private javax.swing.JLabel lblCPU8;
    private javax.swing.JLabel lblCPU9;
    private javax.swing.JLabel lblDiscoTotal;
    private javax.swing.JLabel lblDiscoUsado;
    private javax.swing.JLabel lblProcesadorEmUso;
    private javax.swing.JLabel lblProcesadorRestante;
    private javax.swing.JLabel lblProcesadorTotal;
    private javax.swing.JLabel lblQtdDisco;
    private javax.swing.JLabel lblRAMRestante;
    private javax.swing.JLabel lblRAMTotal;
    private javax.swing.JLabel lblRAMUso;
    private javax.swing.JLabel lblRestanteDisco;
    private javax.swing.JLabel lblStatusCPU;
    private javax.swing.JLabel lblStatusDisco;
    private javax.swing.JLabel lblStatusRAM;
    // End of variables declaration//GEN-END:variables
}
