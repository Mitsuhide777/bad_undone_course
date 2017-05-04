import sun.java2d.cmm.Profile;
import sunw.demo.buttons.OurButton;
import sunw.demo.juggler.Juggler;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ICC_Profile;
import java.awt.event.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;


/**
 * Created by User on 18.02.2017.
 */


public class Main {

    public static void main(String[] args) {
        Rectangle rec = new Rectangle(0, 0, 200, 200);
        Juggler juggler = new Juggler();
        juggler.setVisible(true);
        juggler.setAnimationRate(200);
        juggler.start();
        Juggler j = null;

        for (int k = 0; k < 100000000; ++k) { }


        YoungTrainer tr = new YoungTrainer();
        tr.setNickname("Kirill");
        tr.setYouth(28);
/*        tr.setDolphins(new Dolphin[] {new Dolphin(), new Dolphin()});
        tr.getDolphins(0).setName("Rick");
        tr.getDolphins(1).setName("Morty");*/
        tr.setDol(new Dolphin());
        tr.getDol().setName("Tom");


//        YoungTrainer tr1 = (YoungTrainer)des;

//        juggler.start();
//        juggler.run();

//        ICC_Profile p = new ICC_Profile(new Profile());
        OurButton buttonStart = new OurButton("start");
        OurButton buttonStop = new OurButton("stop");

/*
        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juggler.stopJuggling();
            }
        });

        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juggler.startJuggling();
            }
        });*/
        buttonStop.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                juggler.stopJuggling();

            }

            @Override
            public void mousePressed(MouseEvent e) {

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
        });

        buttonStart.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                juggler.startJuggling();

            }

            @Override
            public void mousePressed(MouseEvent e) {

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
        });

        Object des = null;
        try {
            JSONEncoder js = new JSONEncoder();
            js.saveJSON(juggler);
            System.out.println(js.JSONasString());
/*            Sample ser = new Sample();
            ser.setArr(new Object[] {juggler, buttonStart, buttonStop});
            js.saveJSON(ser);
            System.out.println(js.JSONasString());*/

            JSONDecoder jd = new JSONDecoder(js.JSONasString());
            des = jd.readJSON();

            System.out.println("A");
//            System.out.println(juggler.equals(des.getArr(0)));
        }
        catch (JSONSerializationException ex)
        {
//            System.out.println("V");
//            System.out.println(ex.getMessage());
        }
        catch (JSONDeserializationException ex)
        {
//            System.out.println("V");
            System.out.println(ex.getMessage());
        }

        JFrame frame = new JFrame("JB");
/*        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });*/
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Создается панель,
        //которая будет содржать информацию о IP адресе
        JPanel panel = new JPanel();
//        frame.setSize(600, 600);
//        j = (Juggler)des;
//        j.start();
//        j.run();
//        j.startJuggling();
//        frame.getContentPane().add(j, 0);
//        frame.getComponent(0).setBounds(0, 0, 200, 200);
//        frame.getContentPane().getComponent(0).setBounds(0, 0, 150, 150);
//        juggler.paint();
/*        frame.getContentPane().add(buttonStart, 1);
        frame.getContentPane().getComponent(1).setBounds(0, 150, 150, 150);

        frame.getContentPane().add(buttonStop, 2);
        frame.getContentPane().getComponent(2).setBounds(0, 300, 150, 150);*/
        frame.setVisible(true);
//        j.start();
//        frame.getContentPane().add(new Button(), 1);
        //метод рack(); сообщает Swing о том,
        //что нужно придать компонентам необходимые размеры для
        //правильного помещения их в форму.
        //Другой способ - вызвать setSize(int width, int height).
//        frame.pack();
        //Для того, чтобы увидеть окно на экране
        //вы должны вызвать метод setVisible(true)
//        frame.setVisible(true);
//        frame.pack();
/*        JFrame frame2 = new JFrame("Tch!");
        frame.getContentPane().add(new Button("Good"));
        frame.getContentPane().add(new Button("or"));
        frame.getContentPane().add(new Button("Evil"));*/
//        frame2.getContentPane().add(buttonStart);

        XMLEncoder e = null;
        ObjectOutputStream st = null;
        try {
/*            e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream("D:\\Test.xml")));*/
            st = new ObjectOutputStream(new FileOutputStream("D:\\t.out"));
            st.writeObject(frame);
            st.flush();
            st.close();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
/*        e.writeObject(*//*frame*//**//*new JButton(":(")*//*(OurButton)buttonStart);
        e.close();*/
        frame = null;
        System.out.println("It is done!");

/*        XMLDecoder d = null;
        try {
            d = new XMLDecoder(
                    new BufferedInputStream(
                            new FileInputStream("D:\\Test.xml")));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        JFrame result = new JFrame();
        result.setLayout(new FlowLayout());
        result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        result.add((Juggler)d.readObject());
        result.getContentPane().getComponent(0).setBounds(0, 0, 150, 150);
        d.close();

        result.setVisible(true);*/
        JFrame result = null;
        ObjectInputStream str = null;
        try {
/*            e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream("D:\\Test.xml")));*/
            str = new ObjectInputStream(new FileInputStream("D:\\t.out"));
            result = (JFrame)str.readObject();
            st.flush();
            st.close();

        }
        catch (Exception ex)
        {
            result = new JFrame();
            ex.printStackTrace();
        }
        result.setLayout(new FlowLayout());
        result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

/*        result.add((Juggler)d.readObject());
        result.getContentPane().getComponent(0).setBounds(0, 0, 150, 150);
        d.close();*/

//        ((Juggler)result.getContentPane().getComponent(0)).start();
        result.setVisible(true);
    }
}
