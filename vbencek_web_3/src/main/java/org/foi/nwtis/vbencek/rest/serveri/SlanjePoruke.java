/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.rest.serveri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Klasa koja služi za slanje JMS poruka
 * @author NWTiS_2
 */
public class SlanjePoruke {

    @Resource(mappedName = "jms/NWTiS_vbencek_2")
    private Queue nWTIS_vbencek_2;

    @Resource(mappedName = "jms/NWTiS_vbencek")
    private ConnectionFactory nWTIS_vbencek;

    static int brojac = 0;
    
    /**
     * Metoda koja šalje poruku
     * @param adresa 
     */
    public void saljiPoruku(String adresa) {
        try {
            saljiJMSPoruku(adresa);
        } catch (NamingException ex) {
            Logger.getLogger(SlanjePoruke.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(SlanjePoruke.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Metoda koja kreira JMS poruku
     * @param session
     * @param messageData
     * @return
     * @throws JMSException 
     */
    private Message kreirajJMSPoruku(Session session, Object messageData) throws JMSException {
        TextMessage tm = session.createTextMessage();
        tm.setText(messageData.toString());
        return tm;
    }
    
    /**
     * Metoda koja salje JMS poruku
     * @param messageData
     * @throws NamingException
     * @throws JMSException 
     */
    private void saljiJMSPoruku(Object messageData) throws NamingException, JMSException {
        Context c = new InitialContext();
        Connection conn = null;
        Session s = null;
        try {
            conn = nWTIS_vbencek.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            MessageProducer mp = s.createProducer(nWTIS_vbencek_2);
            mp.send(kreirajJMSPoruku(s, messageData));
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    /**
     * Metoda koja formira poruku za slanje 
     * @param korisnik
     * @param komanda
     * @param vrijeme
     * @return 
     */
    public String formirajForuku(String korisnik, String komanda, String vrijeme) {
        brojac++;
        String vrijemeSlanja = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSS").format(new Date());
        String vrijemePrijema = vrijeme;

        String poruka = "{\"id\": " + brojac + ", \"korisnik\": \"" + korisnik + "\", \"komanda\": \""
                + komanda + "\", \"vrijemePrijema\": \"" + vrijemePrijema + "\", \"vrijemeSlanja\": \"" + vrijemeSlanja + "\"}";
        return poruka;
    }
}
