import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.sound.sampled.*;

public class AimSharp extends JFrame implements MouseListener {

    private JPanel cards;
    private CardLayout c;
    private JPanel menu, field, field2, Settings;
    private BufferedImage image, image2, image3;
    //menu stuff
    private JButton goToField, goToField2,goToSettings;
    private JLabel title;
    //field stuff
    private JLabel time, hits, accuracy, leaderboard;
    private JButton returnToMenu;
    private JButton returnToMenu2;
    private JButton returnToMenu3;
    private ArrayList<Attempt> leader;
    private int t=0, hit, clicks;
    private int acc=100,acc2=100;
    private Timer timer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            t--;
            if(flickVis && timeAtFlick - t >= 2){
                moveFlick(false);
                clicks++;
                hits.setText("Score: " + hit + "/" + clicks);
                acc2 =(int) (((double) hit / clicks) * 100);
                accuracy2.setText("Accuracy: " + acc2 + "%");
                repaint();
            }
            if(open == 2) {
                time.setText("Time: " + t);
            }
            if(open == 3){
                time2.setText("Time: " + t);
            }
            if(t == 0){
                stopField();
            }
        }
    });
    private int open;//WHAT FRAME WERE ON
    //field 2 stuff
    private JLabel time2, hits2, accuracy2;
    //Settings
    private JSlider timeSlider;
    private AudioInputStream ais, ais2, ais3;
    private Clip clip, clip2, clip3;
    private JLabel sliderLBL, howToPlay;
    private JCheckBox soundBox, soundBox2;
    private Color orangeWood = new Color(183,73,35);
    private Color walnut = new Color(67,39,15);
    private Color tortilla = new Color(153, 121, 80);

    public AimSharp(){
        this.addMouseListener(this);
        c = new CardLayout();
        cards = new JPanel(c);
        this.getContentPane().add(cards);
        cards.setVisible(true);

        try{
            image = ImageIO.read(new File("gridWall.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            image2 = ImageIO.read(new File("gridWall2.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            image3 = ImageIO.read(new File("crossedGuns.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //menu initialization
        menu = new JPanel();
        menu.setLayout(null);
        menu.setBackground(orangeWood);
        title = new JLabel("Aim Sharp");
        title.setFont(new Font("GOUDY STOUT", Font.PLAIN, 100));
        title.setForeground(walnut);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setSize(1150, 100);
        title.setLocation(200, 50);

        //Settings Stuff
        goToSettings = new JButton("Settings");
        goToSettings.setSize(350,100);
        goToSettings.setLocation(50,550);
        goToSettings.setFont(new Font("GOUDY STOUT",Font.PLAIN,30));
        goToSettings.setBackground(tortilla);
        goToSettings.setForeground(walnut);
        goToSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.show(cards,"4");
                runSettings();
            }
        });

        title.setVisible(true);
        Settings = new JPanel();
        Settings.setLayout(null);
        Settings.setBackground(orangeWood);

        returnToMenu3 = new JButton("Menu");
        returnToMenu3.setSize(200,50);
        returnToMenu3.setFont(new Font("Raleway", Font.PLAIN, 30));
        returnToMenu3.setLocation(1300,10);
        returnToMenu3.setBackground(tortilla);
        returnToMenu3.setForeground(walnut);
        returnToMenu3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                c.show(cards, "1");
                menuMusic();
                open = 1;
                repaint();
            }
        });
        Settings.add(returnToMenu3);
        menu.add(title);
        menu.add(goToSettings);

        timeSlider = new JSlider(JSlider.HORIZONTAL, 10, 60, 30);
        timeSlider.setSize(400,100);
        timeSlider.setLocation(100, 200);
        timeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(t == 30 && timeSlider.getValue() != 30){
                    JOptionPane.showMessageDialog(null, "Warning, times other than 30 won't be considered for the leaderboard.");
                }
                t = timeSlider.getValue();
            }
        });
        timeSlider.setMajorTickSpacing(10);
        timeSlider.setMajorTickSpacing(5);
        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);
        timeSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        timeSlider.setBackground(orangeWood);
        timeSlider.setForeground(walnut);
        Settings.add(timeSlider);

        sliderLBL = new JLabel("Time");
        sliderLBL.setSize(200,50);
        sliderLBL.setLocation(200,150);
        sliderLBL.setFont(new Font("GOUDY STOUT", Font.PLAIN, 30));
        sliderLBL.setForeground(walnut);
        sliderLBL.setHorizontalAlignment(SwingConstants.CENTER);
        Settings.add(sliderLBL);

        howToPlay = new JLabel("<html><p>Aim Sharp is a tool to assists gamers with practicing their aim. There are 2 modes to choose from; random targets and flicking. Random spawns targets randomly while flicking requires you to shoot one target in the middle before flicking to another target.<html>");
        howToPlay.setSize(800,500);
        howToPlay.setLocation(600,300);
        howToPlay.setFont(new Font("GOUDY STOUT", Font.PLAIN, 20));
        howToPlay.setForeground(walnut);
        howToPlay.setHorizontalAlignment(SwingConstants.CENTER);
        Settings.add(howToPlay);

        soundBox = new JCheckBox("Music", true);
        soundBox.setFont(new Font("GOUDY STOUT", Font.PLAIN, 30));;
        soundBox.setSize(300,50);
        soundBox.setLocation(100, 400);
        soundBox.setForeground(walnut);
        soundBox.setBackground(orangeWood);
        soundBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                menuMusic();
            }
        });
        Settings.add(soundBox);

        soundBox2 = new JCheckBox("Sound", true);
        soundBox2.setFont(new Font("GOUDY STOUT", Font.PLAIN, 30));;
        soundBox2.setSize(300,50);
        soundBox2.setLocation(100, 600);
        soundBox2.setForeground(walnut);
        soundBox2.setBackground(orangeWood);
        soundBox2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            }
        });
        Settings.add(soundBox2);

        goToField = new JButton("Random");
        goToField.setSize(350,100);
        goToField.setLocation(50,250);
        goToField.setFont(new Font("GOUDY STOUT", Font.PLAIN, 30));
        goToField.setBackground(tortilla);
        goToField.setForeground(walnut);
        goToField.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                c.show(cards, "2");
                runField();
            }
        });
        menu.add(goToField);
        open = 1;

        //field initialization
        field = new JPanel();
        field.setLayout(null);
        field.setBackground(orangeWood);

        time = new JLabel("Time: ");
        time.setFont(new Font("Raleway", Font.PLAIN, 30));
        time.setForeground(walnut);
        time.setSize(200,50);
        time.setLocation(800,10);
        field.add(time);

        hits = new JLabel("Score: 0/0");
        hits.setFont(new Font("Raleway", Font.PLAIN, 30));
        hits.setForeground(walnut);
        hits.setSize(250,50);
        hits.setLocation(100,10);
        field.add(hits);

        accuracy = new JLabel("Accuracy: 0%");
        accuracy.setFont(new Font("Raleway", Font.PLAIN, 30));
        accuracy.setForeground(walnut);
        accuracy.setSize(300,50);
        accuracy.setLocation(400,10);
        field.add(accuracy);

        leaderboard = new JLabel("");
        leaderboard.setFont(new Font("GOUDY STOUT", Font.PLAIN, 25));
        leaderboard.setForeground(walnut);
        leaderboard.setSize(400,700);
        leaderboard.setLocation(1100,200);
        leaderboard.setHorizontalAlignment(SwingConstants.CENTER);
        leaderboard.setVerticalAlignment(1);
        leaderboard.setBackground(orangeWood);
        leaderboard.setOpaque(true);
        menu.add(leaderboard);

        leader = new ArrayList<>();
        ArrayList<String> file;
        try{
            file = new ArrayList<>(Files.readAllLines(Paths.get("leaderBoard.txt")));
        } catch (IOException e) {
            file = new ArrayList<>();
        }
        for(int i = 0;i < file.size();i++){
            String[] data = file.get(i).split(" ");
            Attempt temp = new Attempt(Integer.parseInt(data[0]),Integer.parseInt(data[1]));
            leader.add(temp);
        }
        leaderboard.setText(leaderToString());

        returnToMenu = new JButton("Menu");
        returnToMenu.setSize(200,50);
        returnToMenu.setFont(new Font("Raleway", Font.PLAIN, 30));
        returnToMenu.setLocation(1300,10);
        returnToMenu.setBackground(tortilla);
        returnToMenu.setForeground(walnut);
        returnToMenu.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                c.show(cards, "1");
                menuMusic();
                open = 1;
                repaint();
            }
        });

        field.add(returnToMenu);

        //FIELD2 STUFF
        field2 = new JPanel();
        field2.setLayout(null);
        field2.setBackground(orangeWood);

        returnToMenu2= new JButton("Menu");
        returnToMenu2.setSize(200,50);
        returnToMenu2.setFont(new Font("Raleway", Font.PLAIN, 30));
        returnToMenu2.setBackground(tortilla);
        returnToMenu2.setForeground(walnut);
        returnToMenu2.setLocation(1300,10);
        returnToMenu2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                c.show(cards, "1");
                menuMusic();
                open = 1;
                repaint();
            }
        });
        goToField2 = new JButton("Flicks");
        goToField2.setSize(350,100);
        goToField2.setLocation(50,400);
        goToField2.setFont(new Font("GOUDY STOUT", Font.PLAIN, 30));
        goToField2.setBackground(tortilla);
        goToField2.setForeground(walnut);
        goToField2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                c.show(cards, "3");
                runField2();
            }
        });
        menu.add(goToField2);
        field2.add(returnToMenu2);

        time2 = new JLabel("Time: ");
        time2.setFont(new Font("Raleway", Font.PLAIN, 30));
        time2.setForeground(walnut);
        time2.setSize(200,50);
        time2.setLocation(800,10);
        field2.add(time2);

        hits2 = new JLabel("Score: 0/0");
        hits2.setFont(new Font("Raleway", Font.PLAIN, 30));
        hits2.setForeground(walnut);
        hits2.setSize(250,50);
        hits2.setLocation(100,10);
        field2.add(hits2);

        accuracy2 = new JLabel("Accuracy: 0%");
        accuracy2.setFont(new Font("Raleway", Font.PLAIN, 30));
        accuracy2.setForeground(walnut);
        accuracy2.setSize(300,50);
        accuracy2.setLocation(400,10);
        field2.add(accuracy2);

        time.setVisible(false);
        hits.setVisible(false);
        accuracy.setVisible(false);
        time2.setVisible(false);
        hits2.setVisible(false);
        accuracy2.setVisible(false);

        try{
           ais = AudioSystem.getAudioInputStream(new File("ClayPotBreak.wav"));
           ais2 = AudioSystem.getAudioInputStream(new File("Gun.wav"));
           ais3 = AudioSystem.getAudioInputStream(new File("Western.wav"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            clip = AudioSystem.getClip();
            clip2 = AudioSystem.getClip();
            clip3 = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        try {
            clip.open(ais);
            clip2.open(ais2);
            clip3.open(ais3);
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }

        cards.add(menu, "1");
        cards.add(field, "2");
        cards.add(field2, "3");
        cards.add(Settings,"4");
        c.show(cards, "1");
        open = 1;
        menuMusic();
        repaint();
    }

    private void menuMusic(){
        clip3.stop();
        clip3.setFramePosition(0);
        if(soundBox.isSelected()) {
            clip3.start();
            clip3.loop(clip3.LOOP_CONTINUOUSLY);
        }
    }

    private void runField(){
        clip3.stop();
        time.setVisible(true);
        hits.setVisible(true);
        accuracy.setVisible(true);
        time2.setVisible(false);
        hits2.setVisible(false);
        accuracy2.setVisible(false);
        hits.setText("Score: 0/0");
        acc = 0;
        accuracy.setText("Accuracy: " + acc + "%");
        t = timeSlider.getValue();
        time.setText("Time: " + t);
        open = 2;
        hit = 0;
        clicks = 0;
        timer.start();
        moveTarget();
        repaint();
    }

    private void runField2(){
        clip3.stop();
        time2.setVisible(true);
        hits2.setVisible(true);
        accuracy2.setVisible(true);
        time.setVisible(false);
        hits.setVisible(false);
        accuracy.setVisible(false);
        hits2.setText("Score: 0/0");
        acc2 = 0;
        accuracy2.setText("Accuracy: " + acc2 + "%");
        t = timeSlider.getValue();
        time2.setText("Time: " + t);
        open = 3;
        hit = 0;
        clicks = 0;
        timer.start();
        repaint();
    }
    private void runSettings(){
        // opens Settings JPanel to custome Game
        open = 4;
        repaint();
    }

    private void stopField(){
        timer.stop();
        if(timeSlider.getValue() == 30) {
            addToLeaderBoard();
        }
        leaderboard.setText(leaderToString());
    }

    private void addToLeaderBoard(){//STORE LEADERBOARD IN FILE
        Attempt score = new Attempt(hit, clicks);
        if(open == 2) {
            if (score.hit() > leader.get(0).hit()) {
                leader.set(0, score);
            }
            leader.set(1, score);
        }
        if(open == 3){
            if(score.hit() > leader.get(2).hit()){
                leader.set(2, score);
            }
            leader.set(3, score);
        }
        try {
            FileWriter writer = new FileWriter("leaderBoard.txt");
            for(int i = 0;i < leader.size();i++){
                writer.write(leader.get(i).hit() + " " + leader.get(i).total());
                if(i < leader.size() - 1){
                    writer.write("\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private String leaderToString(){//REPLACE WITH BEST AND MOST RECENT
        String temp = "<html>Leaderboard";
        temp += "<br>-----------------------------<br>";
        for(int i = 0;i < 2;i++) {
            if(i == 0){
                temp += "<br>Random<br>";
            }
            if(i == 1){
                temp += "<br>Flick<br>";
            }
            temp += "<br>Best: ";
            temp += leader.get(2 * i).hit();
            temp += "/";
            temp += leader.get(2 * i).total();
            temp += " - ";
            temp += (int) (((double) leader.get(2 * i).hit() / leader.get(2 * i).total()) * 100);
            temp += "%<br>";
            temp += "<br>Last: ";
            temp += leader.get(2 * i + 1).hit();
            temp += "/";
            temp += leader.get(2 * i + 1).total();
            temp += " - ";
            temp += (int) (((double) leader.get(2 * i + 1).hit() / leader.get(2 * i + 1).total()) * 100);
            temp += "%<br>";
        }
        temp += "<html";
        return temp;
    }

    int x = 100, y = 100;
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(open == 1){
            g.drawImage(image3, 450, 200, 600, 500, this);
        }
        if(open == 2) {
            g.drawImage(image, 0, 100, getWidth(), getHeight() - 100, this);
            for (int i = 0; i < 5; i++) {
                g.setColor(i % 2 == 0 ? walnut : tortilla);
                g.fillOval(x + 10 * i, y + 10 * i, 100 - 20 * i, 100 - 20 * i);
            }
        }
        if(open == 3){
            g.drawImage(image2, 0, 100, getWidth(), getHeight() - 100, this);
            g.setColor(walnut);
            g.fillOval(752, 460, 50, 50);
            if(flickVis){
                g.setColor(tortilla);
                g.fillOval(x, y, 50, 50);
            }
            //DRAW TARGET FIELD
        }
        title.setText(title.getText());
    }

    private void moveTarget(){
        x = (int)(Math.random() * 1325 + 75);
        y = (int)(Math.random() * 580 + 120);
    }

    private boolean flickVis = false;
    private int timeAtFlick;
    private void moveFlick(boolean vis){
        flickVis = vis;
        x = (int)(Math.random() * 600 + 75);
        if(Math.random() > .5){
            x = 1500 - x;
        }
        y = 460;
        if(flickVis){
            timeAtFlick = t;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(open == 2 && t > 0) {
            clicks++;
            if (e.getX() > x && e.getX() < x + 100 && e.getY() > y && e.getY() < y + 100) {
                hit++;
                if(soundBox2.isSelected()) {
                    clip.setFramePosition(0);
                    clip.start();
                }
            }
            if(acc<30 && clicks>4){
                t=0;
                timer.stop();
                JOptionPane.showMessageDialog(null, "Game ended! Accuracy fell below 30%");
            }
            hits.setText("Score: " + hit + "/" + clicks);
            acc=(int) (((double) hit / clicks) * 100);
            accuracy.setText("Accuracy: " + acc + "%");
            moveTarget();
            repaint();
        }
        if(open == 3 && t > 0){
            if(!flickVis && e.getX() > 752 && e.getX() < 802 && e.getY() > 460 && e.getY() < 510){
                moveFlick(true);
            }
            else if(flickVis){
                if(e.getX() > x && e.getX() < x + 50 && e.getY() > y && e.getY() < y + 50){
                    hit++;
                    if(soundBox2.isSelected()) {
                        clip2.setFramePosition(0);
                        clip2.start();
                    }
                }
                clicks++;
                moveFlick(false);
            }
            hits2.setText("Score: " + hit + "/" + clicks);
            acc2 = (int) (((double) hit / clicks) * 100);
            accuracy2.setText("Accuracy: " + acc2 + "%");
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
