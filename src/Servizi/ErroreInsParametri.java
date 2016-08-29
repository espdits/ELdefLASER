/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servizi;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author gianni
 */
public class ErroreInsParametri extends JFrame{
    
    private JPanel contentPane;
    int newSize = 14;
    Font newFont = new Font("Georgia", Font.ITALIC, newSize);
    Color sfondo = new Color(255,153,0);

    public ErroreInsParametri(final JFrame winErrore) {
        getContentPane().setEnabled(false);
        setTitle("Attenzione!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(30, 30, 350, 220);
        getContentPane().setLayout(null);
        JButton button = new JButton("Esci");
        button.setFont(newFont);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                winErrore.dispose();
                ErroreInsParametri.this.dispose();

            }
        });
/*
        JButton indietro = new JButton("Indietro");
        indietro.setFont(newFont);
        indietro.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                InterfacciaUtente interfaccia = new InterfacciaUtente();
                interfaccia.setVisible(true);
                setVisible(false);
                dispose();
            }
        });
*/
        button.setBounds(122, 100, 90, 22);
        getContentPane().add(button);
    //    indietro.setBounds(122, 130, 90, 22);
    //    getContentPane().add(indietro);
        JEditorPane finestraTesto = new JEditorPane();
        finestraTesto.setEditable(false);
        finestraTesto.setText("          Hai inserito dei parametri \n                  non conformi !!!");
        finestraTesto.setFont(newFont);
        finestraTesto.setBounds(40, 30, 260, 40);
        getContentPane().add(finestraTesto);
        //All'interno della finestra è presente il contentPane
        contentPane = new JPanel();
        getContentPane().setBackground(sfondo);

    }
}
    
    



/*


public class FinestraPosizioniEsterne extends JFrame {


*/