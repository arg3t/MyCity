/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.yigitcolakoglu.master_app;
import java.awt.AlphaComposite;
import java.io.*;
import java.net.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.schmizz.sshj.common.IOUtils;
import org.json.JSONObject;

/**
 *
 * @author yigit
 */
public class cameraForm extends javax.swing.JFrame implements ChangeListener{

    /**
     * Creates new form cameraForm
     */
    public cameraForm() {
        initComponents();
        initialize();
    }
    private ServerSocket server;
    private Socket client;
    private Thread running = null;
    private boolean listening = false;
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        camera_full_label = new javax.swing.JLabel();
        ambulance_button = new javax.swing.JButton();
        camera_cut_label = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        gpu_usage = new javax.swing.JLabel();
        gpu_temp = new javax.swing.JLabel();
        cpu_usage = new javax.swing.JLabel();
        cpu_temp = new javax.swing.JLabel();
        ram_usage = new javax.swing.JLabel();
        ram_temp = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        fan_rpm = new javax.swing.JLabel();
        fps_label = new javax.swing.JLabel();
        manage_button = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        intersect_1_Y = new javax.swing.JLabel();
        intersect_1_G = new javax.swing.JLabel();
        intersect_1_R = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        intersect_2_Y = new javax.swing.JLabel();
        intersect_2_G = new javax.swing.JLabel();
        intersect_2_R = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        robot_cam_label = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setName(""); // NOI18N
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseExited(evt);
            }
        });

        camera_full_label.setText(" ");

        ambulance_button.setText("Start");
        ambulance_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ambulance_buttonActionPerformed(evt);
            }
        });

        jTabbedPane1.addTab("Intersection & Ambulance", jPanel5);

        light_1_label.setText("Light 1:");

        light_2_label.setText("Light 2: ");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(light_1_label)
                    .addComponent(light_2_label))
                .addContainerGap(1624, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(light_1_label)
                .addGap(18, 18, 18)
                .addComponent(light_2_label)
                .addContainerGap(918, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Traffic Lights", jPanel6);

        fan_rpm.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        fan_rpm.setText("2500 RPM");

        fps_label.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        fps_label.setText("60 FPS");

        manage_button.setText("Management");
        manage_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(camera_full_label, javax.swing.GroupLayout.PREFERRED_SIZE, 1280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ambulance_button, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(475, 475, 475)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fan_rpm, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(fps_label, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(86, 86, 86)))
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(camera_cut_label, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(manage_button, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(95, 95, 95))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(camera_full_label, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ambulance_button)
                            .addComponent(manage_button))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(camera_cut_label, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(180, 180, 180)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(fan_rpm, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fps_label, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(110, 110, 110))))
        );

        jTabbedPane1.addTab("Loads", jPanel7);

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        intersect_1_Y.setBackground(new java.awt.Color(204, 204, 204));
        intersect_1_Y.setForeground(new java.awt.Color(204, 204, 204));
        intersect_1_Y.setOpaque(true);

        intersect_1_G.setBackground(new java.awt.Color(204, 204, 204));
        intersect_1_G.setForeground(new java.awt.Color(204, 204, 204));
        intersect_1_G.setOpaque(true);

        intersect_1_R.setBackground(new java.awt.Color(204, 204, 204));
        intersect_1_R.setForeground(new java.awt.Color(204, 204, 204));
        intersect_1_R.setOpaque(true);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(109, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(intersect_1_G, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(intersect_1_R, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(intersect_1_Y, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(107, 107, 107))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(intersect_1_R, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(intersect_1_Y, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(intersect_1_G, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        intersect_2_Y.setBackground(new java.awt.Color(204, 204, 204));
        intersect_2_Y.setForeground(new java.awt.Color(204, 204, 204));
        intersect_2_Y.setOpaque(true);

        intersect_2_G.setBackground(new java.awt.Color(204, 204, 204));
        intersect_2_G.setForeground(new java.awt.Color(204, 204, 204));
        intersect_2_G.setOpaque(true);

        intersect_2_R.setBackground(new java.awt.Color(204, 204, 204));
        intersect_2_R.setForeground(new java.awt.Color(255, 153, 153));
        intersect_2_R.setOpaque(true);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(159, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(intersect_2_G, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(intersect_2_R, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(intersect_2_Y, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(163, 163, 163))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(intersect_2_R, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(intersect_2_Y, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(intersect_2_G, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );

        jLabel8.setText("Intersection 1");

        jLabel9.setText("Intersection 2");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(276, 276, 276)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(241, 241, 241))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 526, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addGap(4, 4, 4)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Lights", jPanel4);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(449, 449, 449)
                .addComponent(robot_cam_label)
                .addContainerGap(1173, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(243, 243, 243)
                .addComponent(robot_cam_label)
                .addContainerGap(573, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Robot Cam", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1624, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 841, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public void initialize() {
        jTabbedPane1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("Tab: " + jTabbedPane1.getSelectedIndex());
                if(running!=null){
                    try{
                        server.close();
                        client.close();
                        running.stop();
                    }catch(IOException ex){
                        System.out.println("IO Exception occured");
                    }catch(Exception ex){
                        System.out.println(e.toString());
                    }
                }
                switch (jTabbedPane1.getSelectedIndex()) {
                    case 0:
                        running = new Thread(() -> {
                            try{
                                onCreate(8485,"cams");
                            }catch(Exception ex){
                                System.out.println(e.toString());
                            }
                        });
                        running.start();
                        break;
                    case 1:
                        running = new Thread(() -> {
                            try{
                                onCreate(8484,"lights");
                            }catch(Exception ex){
                                System.out.println(e.toString());
                            }
                        });
                        running.start();
                        break;
                    case 2:
                        running = new Thread(() -> {
                            try{
                                onCreate(8483,"Loads");
                            }catch(Exception ex){
                                System.out.println(e.toString());
                            }
                        });
                        running.start();
                        break;
                }
            }
        });
    }
    
    private void jTabbedPane1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jTabbedPane1MouseExited

    private void jTabbedPane1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseEntered
        if(!listening){
            jTabbedPane1.addChangeListener(this);
            System.out.println("Added listener");
            listening = true;
        }
    }//GEN-LAST:event_jTabbedPane1MouseEntered

    private void manage_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_buttonActionPerformed
        managementForm settings = new managementForm();
        settings.setVisible(true);
        settings.initIp();
    }//GEN-LAST:event_manage_buttonActionPerformed

    private void ambulance_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ambulance_buttonActionPerformed
        if(running!=null){
            try{
                server.close();
                client.close();
                running.stop();
            }catch(IOException e){
                System.out.println("IO Exception occured");
            }catch(Exception e){
                System.out.println(e.toString());
            }
        }else{
            running = new Thread(() -> {
                try{
                    onCreate(8485,"Ambulance");
                }catch(Exception e){
                    System.out.println(e.toString());
                }
            });
            running.start();
        }
    }//GEN-LAST:event_ambulance_buttonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(cameraForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(cameraForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(cameraForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(cameraForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        System.out.println("Reading: ");

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new cameraForm().setVisible(true);

        }});
    }

    public void onCreate(int port, String name) throws Exception{
        boolean run = true;
        if (name == "robot") {
            while (run) {
                BufferedImage image = null;
                URL img_url = new URL("http://10.10.26.141:8080/?action=snapshot");
                image = ImageIO.read(img_url);
                robot_cam_label.setIcon(new ImageIcon(image));
                
                URL obj = new URL("http://10.10.26.164:5001/ai");
                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                
                con.setRequestMethod("POST");
                
                byte[] imageBytes = IOUtils.toByteArray(img_url);
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                
                String params = "type=coco&img=" + base64;
                
                con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(params);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		JSONObject json = new JSONObject(response.toString());
                Graphics2D g2d = image.createGraphics();
            }
        }
        this.camera_cut_label.setIcon(new ImageIcon());
        this.camera_full_label.setIcon(new ImageIcon());
        String fromClient = "";
        String toClient;

        server = new ServerSocket(port);
        System.out.println("wait for connection on port " + port);
        jTabbedPane1.addChangeListener(this);
        client = server.accept();
        System.out.println("got connection on port " + port);
        BufferedImage image = null;
        byte[] imageByte;
        int null_reps = 0;
        int fps_sum = 0;
        int reps = 0;
        if(name.equals("cams")){
            while(run) {
                try{
                    long start = System.currentTimeMillis();
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    fromClient = in.readLine();

                    if(fromClient != null) {
                        if(fromClient.trim().equals("Bye")) {
                            run = false;
                            System.out.println("socket closed");
                        }else{
                            System.out.println("received data in size: " + fromClient.length());
                            JSONObject json = new JSONObject(fromClient);
                            byte[] decodedBytes = Base64.getDecoder().decode(json.getString("image_full"));
                            ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
                            image = ImageIO.read(bis);
                            bis.close();
                            JSONObject dims = json.getJSONObject("image_sizes");
                            this.camera_full_label.setIcon(new ImageIcon(resizeImage(image,1280,720)));
                            this.camera_cut_label.setIcon(new ImageIcon(resizeImage(image.getSubimage(dims.getInt("x"), dims.getInt("y"), dims.getInt("width"), dims.getInt("height")),300,300)));
                            JSONObject data = json.optJSONObject("load");
                            this.gpu_temp.setText(data.getString("gpu_temp"));
                            this.gpu_usage.setText(data.getString("gpu_load"));
                            this.cpu_temp.setText(data.getString("cpu_temp"));
                            this.cpu_usage.setText(data.getString("cpu_load"));
                            this.ram_temp.setText(data.getString("mem_temp"));
                            this.ram_usage.setText(data.getString("mem_load"));
                            this.fan_rpm.setText(data.getString("fan_speed"));
                            null_reps=0;
                            long end = System.currentTimeMillis();
                            float sec = (end - start) / 1000F;
                            fps_sum += Math.round(1/sec);
                            reps+=1;
                            if(reps%10==0){
                                this.fps_label.setText(fps_sum/10 + " FPS");
                                fps_sum=0;
                            }
                        }
                    }else{
                        null_reps +=1;
                    }
                }
                catch(Exception e){
                    System.out.println(fromClient);
                    System.out.println(e.toString());
                    null_reps+=1;
                 }
                if (null_reps >= 1000000000){
                    run = false;
                    System.out.println("socket closed");
                }
            }
            server.close();
            client.close();
            this.camera_cut_label.setIcon(new ImageIcon());
            this.camera_full_label.setIcon(new ImageIcon());
            JOptionPane.showMessageDialog(this, name +" socket server down!");
            running.stop();
        }else if(name.equals("lights")){
            int[][] colors = {{204,0,0},{204,204,0},{0,204,0}};
            javax.swing.JLabel[][] labels = {{intersect_1_R,intersect_1_Y,intersect_1_G},{intersect_2_R,intersect_2_Y,intersect_2_G}};
            int[] lights = {0,0};
            while(run) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    fromClient = in.readLine();

                    if(fromClient != null) {
                        if(fromClient.trim().equals("Bye")) {
                            run = false;
                            System.out.println("socket closed");
                        }else{
                            System.out.println("received data in size: " + fromClient.length());
                            System.out.println(fromClient);
                            lights[0] = Character.getNumericValue(fromClient.charAt(0));
                            lights[1] = Character.getNumericValue(fromClient.charAt(2));
                            for(int i = 0;i < 2;i++){
                                for(int j = 0;j<3;j++){
                                    if(lights[i] == j){
                                        labels[i][j].setBackground(new java.awt.Color(colors[j][0], colors[j][1], colors[j][2]));
                                        continue;
                                    }
                                    labels[i][j].setBackground(new java.awt.Color(204,204,204));
                                }
                            }
                        }
                }
        
        
    }
        }
    }

    public static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
    @Override
    public void stateChanged(ChangeEvent e){
        System.out.println("Tab: " + jTabbedPane1.getSelectedIndex());
        if(running!=null){
           try{
                server.close();
                        client.close();
                        running.stop();
                    }catch(IOException ex){
                        System.out.println("IO Exception occured");
                    }catch(Exception ex){
                        System.out.println(ex.toString());
                    }
        }
        switch (jTabbedPane1.getSelectedIndex()) {
            case 0:
                running = new Thread(() -> {
                try{
                    onCreate(8485,"cams");
                }catch(Exception ex){
                    System.out.println(e.toString());
                }
            });
            running.start();
            break;
            case 1:
                running = new Thread(() -> {
                try{
                    onCreate(69,"lights");
                }catch(Exception ex){
                    System.out.println(e.toString());
                }
                });
                running.start();
            break;
            case 2:
                running = new Thread(() -> {
                try{
                    onCreate(0,"robot");
                }catch(Exception ex){
                    System.out.println(e.toString());
                }
                });
                running.start();
            break;
        }
            
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ambulance_button;
    private javax.swing.JLabel camera_cut_label;
    private javax.swing.JLabel camera_full_label;
    private javax.swing.JLabel cpu_temp;
    private javax.swing.JLabel cpu_usage;
    private javax.swing.JLabel fan_rpm;
    private javax.swing.JLabel fps_label;
    private javax.swing.JLabel gpu_temp;
    private javax.swing.JLabel gpu_usage;
    private javax.swing.JLabel intersect_1_G;
    private javax.swing.JLabel intersect_1_R;
    private javax.swing.JLabel intersect_1_Y;
    private javax.swing.JLabel intersect_2_G;
    private javax.swing.JLabel intersect_2_R;
    private javax.swing.JLabel intersect_2_Y;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton manage_button;
    private javax.swing.JLabel ram_temp;
    private javax.swing.JLabel ram_usage;
    private javax.swing.JLabel robot_cam_label;
    // End of variables declaration//GEN-END:variables


}
