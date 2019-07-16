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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.io.IOUtils;
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
    }
    private ServerSocket server;
    private Socket client;
    private Thread running = null;
    private boolean listening = false;
    private String ROBOT_IP = "10.42.0.9";
    private String AI_IP = "10.10.26.161";
    private Socket robotSocket;
    private DataOutputStream out;
    private BufferedReader in;
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
        jPanel5 = new javax.swing.JPanel();
        intersection_label = new javax.swing.JLabel();
        ambulance_label = new javax.swing.JLabel();
        fps_label = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        light_1_label = new javax.swing.JLabel();
        light_2_label = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        robot_cam_label = new javax.swing.JLabel();
        battery_voltage_label = new javax.swing.JLabel();
        current_drawn_label = new javax.swing.JLabel();
        latitude_label = new javax.swing.JLabel();
        longitude_label = new javax.swing.JLabel();
        robot_stop = new javax.swing.JButton();
        move_robot = new javax.swing.JButton();

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

        intersection_label.setAlignmentY(0.0F);

        ambulance_label.setName(""); // NOI18N

        fps_label.setText("0 FPS");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(intersection_label, javax.swing.GroupLayout.PREFERRED_SIZE, 1024, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addComponent(ambulance_label, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(204, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fps_label)
                        .addGap(168, 168, 168))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(fps_label)
                        .addGap(88, 88, 88)
                        .addComponent(ambulance_label, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(intersection_label, javax.swing.GroupLayout.PREFERRED_SIZE, 768, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(181, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1693, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 978, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Loads", jPanel7);

        battery_voltage_label.setText("Battery Voltage");

        current_drawn_label.setText("Current Drawn");

        latitude_label.setText("Latitude");

        longitude_label.setText("Longitude");

        robot_stop.setText("Stop Robot");
        robot_stop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                robot_stopMousePressed(evt);
            }
        });
        robot_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                robot_stopActionPerformed(evt);
            }
        });

        move_robot.setText("Move Robot");
        move_robot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                move_robotMousePressed(evt);
            }
        });
        move_robot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                move_robotActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(robot_cam_label, javax.swing.GroupLayout.DEFAULT_SIZE, 1469, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(move_robot)
                            .addComponent(robot_stop))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(current_drawn_label)
                            .addComponent(longitude_label))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(battery_voltage_label)
                            .addComponent(latitude_label))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(robot_cam_label, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(move_robot)
                        .addGap(18, 18, 18)
                        .addComponent(robot_stop)))
                .addGap(18, 18, 18)
                .addComponent(battery_voltage_label)
                .addGap(18, 18, 18)
                .addComponent(current_drawn_label, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(longitude_label)
                .addGap(18, 18, 18)
                .addComponent(latitude_label)
                .addContainerGap(182, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Robot", jPanel2);

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

    private void robot_stopMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_robot_stopMousePressed
        try {
            out.writeUTF("s");
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }//GEN-LAST:event_robot_stopMousePressed

    private void move_robotMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_move_robotMousePressed
        try {
            out.writeUTF("m");
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }//GEN-LAST:event_move_robotMousePressed

    private void move_robotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_move_robotActionPerformed
        try {
            out.writeUTF("m");
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }//GEN-LAST:event_move_robotActionPerformed

    private void robot_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_robot_stopActionPerformed
       try {
            out.writeUTF("s");
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }//GEN-LAST:event_robot_stopActionPerformed

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
        if (name.equals("robot")) {
            robotSocket = new Socket(ROBOT_IP, 3131);
            out = new DataOutputStream(robotSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(robotSocket.getInputStream()));
            while (run) {
                out.writeUTF("i");
                String resp = in.readLine();
                System.out.println(resp);
                JSONObject values = new JSONObject(resp);
                System.out.println(values.getString("lat"));
                latitude_label.setText("Latitude: " + values.getString("lat"));
                longitude_label.setText("Longitude: " + values.getString("lng"));
                battery_voltage_label.setText("Battery Voltage: " + values.getString("battery_voltage"));
                current_drawn_label.setText("Current Drawn: " + values.getString("current_drawn"));
                
                BufferedImage image;
                URL img_url = new URL(String.format("http://%s:8080/?action=snapshot", ROBOT_IP));
                image = ImageIO.read(img_url);
                int width = image.getWidth();
                int height = image.getHeight();
                BufferedImage dest = new BufferedImage(height, width, image.getType());
                Graphics2D graphics2D = dest.createGraphics();
                graphics2D.translate((height - width) / 2, (height - width) / 2);
                graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
                graphics2D.drawRenderedImage(image, null);
                robot_cam_label.setIcon(new ImageIcon(dest));
                
                URL obj = new URL(String.format("https://%s:5001/ai", AI_IP));
                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                
                con.setRequestMethod("POST");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(dest, "jpg", out);
                byte[] bytes = out.toByteArray();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                
                String params = "type=damage&img=" + base64;
                
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
            }
            return;
        }
        /*this.camera_cut_label.setIcon(new ImageIcon());
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
        }*/
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
                    System.out.println(ex.toString());
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
            case 3:
                running = new Thread(() -> {
                try{
                    onCreate(0,"robot");
                }catch(Exception ex){
                    System.out.println(ex.toString());
                }
                });
                running.start();
            break;
        }
            
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ambulance_label;
    private javax.swing.JLabel battery_voltage_label;
    private javax.swing.JLabel current_drawn_label;
    private javax.swing.JLabel fps_label;
    private javax.swing.JLabel intersection_label;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel latitude_label;
    private javax.swing.JLabel light_1_label;
    private javax.swing.JLabel light_2_label;
    private javax.swing.JLabel longitude_label;
    private javax.swing.JButton move_robot;
    private javax.swing.JLabel robot_cam_label;
    private javax.swing.JButton robot_stop;
    // End of variables declaration//GEN-END:variables


}
