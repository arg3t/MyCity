/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.yigitcolakoglu.master_app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JFrame;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.json.JSONObject;

/**
 *
 * @author yigit
 */
public class managementForm extends javax.swing.JFrame {

    /**
     * Creates new form managementForm
     */
    public managementForm() {
        initComponents();
    }
    public final SSHClient ssh = new SSHClient();
    
    private JSONObject ip_json;
    private JSONObject usernames;
    private JSONObject passwords;
    private JSONObject commands = new JSONObject();
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        ambulance_ip = new javax.swing.JTextField();
        ambulance_start = new javax.swing.JButton();
        ambulance_state = new javax.swing.JLabel();
        intersection_start = new javax.swing.JButton();
        intersection_ip = new javax.swing.JTextField();
        qr_start = new javax.swing.JButton();
        qr_ip = new javax.swing.JTextField();
        intersection_state = new javax.swing.JLabel();
        qr_state = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        refresh_button = new javax.swing.JButton();

        jFrame1.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextField2.setText("127.0.0.1");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jButton2.setText("Start Process");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Running");

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap(154, Short.MAX_VALUE))
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jLabel2))
                .addContainerGap(656, Short.MAX_VALUE))
        );

        jButton4.setText("jButton4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ambulance_ip.setText("127.0.0.1");
        ambulance_ip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ambulance_ipActionPerformed(evt);
            }
        });

        ambulance_start.setText("Start");
        ambulance_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ambulance_startActionPerformed(evt);
            }
        });

        ambulance_state.setText("Running");

        intersection_start.setText("Start");
        intersection_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intersection_startActionPerformed(evt);
            }
        });

        intersection_ip.setText("127.0.0.1");
        intersection_ip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intersection_ipActionPerformed(evt);
            }
        });

        qr_start.setText("Start");
        qr_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qr_startActionPerformed(evt);
            }
        });

        qr_ip.setText("127.0.0.1");

        intersection_state.setText("Running");

        qr_state.setText("Running");

        jLabel5.setText("Ambulance:");

        jLabel6.setText("Intersection:");

        jLabel7.setText("QR Reader:");

        refresh_button.setText("Refresh");
        refresh_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(qr_start)
                                .addGap(18, 18, 18)
                                .addComponent(qr_ip))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(intersection_start)
                                    .addComponent(ambulance_start))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ambulance_ip)
                                    .addComponent(intersection_ip, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ambulance_state)
                            .addComponent(intersection_state)
                            .addComponent(qr_state)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(refresh_button)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ambulance_ip, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ambulance_start)
                    .addComponent(ambulance_state)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intersection_start)
                    .addComponent(intersection_ip, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(intersection_state)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(qr_start)
                    .addComponent(qr_ip, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(qr_state)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(refresh_button)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ambulance_ipActionPerformed(java.awt.event.ActionEvent evt) {
        
    }//GEN-LAST:event_ambulance_ipActionPerformed
    private void execSSH(String ip,String uname,String passwd, String cmd){
        Session session = null;
        try{
            ssh.connect(ip, 22);
            ssh.authPassword(uname, passwd);
            session = ssh.startSession();
            final Session.Command command = session.exec(cmd);
        }catch(Exception e){
            System.out.println(e.toString());
        }finally {
            try {
                if (session != null) {
                    session.close();
                }
                ssh.disconnect();
            } catch (IOException e) {
                // Do Nothing
            }
        }
    }
    private void ambulance_startActionPerformed(java.awt.event.ActionEvent evt) {
        execSSH(ambulance_ip.getText(),
                usernames.getString("ambulance"),
                passwords.getString("ambulance"),
                commands.getString("ambulance"));
    }

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void intersection_startActionPerformed(java.awt.event.ActionEvent evt) {
        execSSH(ambulance_ip.getText(),
                usernames.getString("intersection"),
                passwords.getString("intersection"),
                commands.getString("intersection"));
    }

    private void intersection_ipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intersection_ipActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_intersection_ipActionPerformed

    private void qr_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qr_startActionPerformed
        execSSH(ambulance_ip.getText(),
                usernames.getString("qr"),
                passwords.getString("qr"),
                commands.getString("qr"));
    }//GEN-LAST:event_qr_startActionPerformed

    private void refresh_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_buttonActionPerformed
        checkState();
    }//GEN-LAST:event_refresh_buttonActionPerformed
    
    
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
            java.util.logging.Logger.getLogger(managementForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(managementForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(managementForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(managementForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new managementForm().setVisible(true);
            }
        });
    }
    
    public void initIp(){
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      commands.put("ambulance","speaker-test -t sine -f 1000 -l 1");
      commands.put("intersection","echo 1");
      commands.put("qr","echo 1");
      FileInputStream in = null;
      String ips = "";
      String userdata = "";
     try {
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        in = new FileInputStream("./ips.json");
        int c;
        while ((c = in.read()) != -1) {
            ips += (char)c;
        }

        in = new FileInputStream("./userinfo.json");
        while ((c = in.read()) != -1) {
            userdata += (char)c;
        }
        usernames = new JSONObject(userdata).getJSONObject("usernames");
        passwords = new JSONObject(userdata).getJSONObject("passwords");
        ip_json = new JSONObject(ips);
        ambulance_ip.setText(ip_json.getString("ambulance"));
        intersection_ip.setText(ip_json.getString("intersection"));
        qr_ip.setText(ip_json.getString("qr"));
      }catch(Exception e){
          System.out.println("[ERROR]: "+e.toString());
          return;
      }
      checkState();
    }
    
    public void checkState(){
        try{
            boolean ip_correct = InetAddress.getByName(ambulance_ip.getText()).isReachable(1000);
            if(!ip_correct){
                throw new Exception("IP Not Reachable");
            }
            new Socket(ambulance_ip.getText(), 22).close();
            ambulance_start.setEnabled(true);
        }catch(Exception e){
            System.out.println(e.toString());
            ambulance_start.setEnabled(false);
        }
        
        try{
            boolean ip_correct = InetAddress.getByName(ambulance_ip.getText()).isReachable(100);
            if(!ip_correct){
                throw new Exception("IP Not Reachable");
            }
            new Socket(ambulance_ip.getText(), 8385).close();
            ambulance_state.setText("Running");
        }catch(Exception e){
            ambulance_state.setText("Not Running");
        }
        
        try{
            boolean ip_correct = InetAddress.getByName(intersection_ip.getText()).isReachable(100);
            if(!ip_correct){
                throw new Exception("IP Not Reachable");
            }
            new Socket(intersection_ip.getText(), 22).close();
            intersection_start.setEnabled(true);
        }catch(Exception e){
            intersection_start.setEnabled(false);
        }
        
        try{
            boolean ip_correct = InetAddress.getByName(intersection_ip.getText()).isReachable(100);
            if(!ip_correct){
                throw new Exception("IP Not Reachable");
            }
            new Socket(intersection_ip.getText(), 8386).close();
            intersection_state.setText("Running");
        }catch(Exception e){
            intersection_state.setText("Not Running");
        }
        
        try{
            boolean ip_correct = InetAddress.getByName(qr_ip.getText()).isReachable(100);
            if(!ip_correct){
                throw new Exception("IP Not Reachable");
            }
            new Socket(qr_ip.getText(), 22).close();
            qr_start.setEnabled(true);
        }catch(Exception e){
            qr_start.setEnabled(false);
        }
        
        try{
            boolean ip_correct = InetAddress.getByName(qr_ip.getText()).isReachable(100);
            if(!ip_correct){
                throw new Exception("IP Not Reachable");
            }
            new Socket(qr_ip.getText(), 8385).close();
            qr_state.setText("Running");
        }catch(Exception e){
            qr_state.setText("Not Running");
        }
        
        
    }
    
    @Override
    public void dispose(){
        JSONObject contentJSON = new JSONObject();
        contentJSON.put("ambulance", ambulance_ip.getText());
        contentJSON.put("intersection", intersection_ip.getText());
        contentJSON.put("qr", qr_ip.getText());
        String fileContent = contentJSON.toString(4);
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("./ips.json"));
            writer.write(fileContent);
            writer.close();
        }catch(Exception er){
            System.out.println("[ERROR]: "+er.toString());
        }
        super.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ambulance_ip;
    private javax.swing.JButton ambulance_start;
    private javax.swing.JLabel ambulance_state;
    private javax.swing.JTextField intersection_ip;
    private javax.swing.JButton intersection_start;
    private javax.swing.JLabel intersection_state;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField qr_ip;
    private javax.swing.JButton qr_start;
    private javax.swing.JLabel qr_state;
    private javax.swing.JButton refresh_button;
    // End of variables declaration//GEN-END:variables
}
