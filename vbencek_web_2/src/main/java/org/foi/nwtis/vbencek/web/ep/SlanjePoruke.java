/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.web.ep;

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
 * Klasa koja služi za slanje JMS poruke
 * @author NWTiS_2
 */
public class SlanjePoruke {
    @Resource(mappedName = "jms/NWTiS_vbencek_1")
    private Queue nWTIS_vbencek_1;
    
    @Resource(mappedName = "jms/NWTiS_vbencek")
    private ConnectionFactory nWTIS_vbencek;
    
    /**
     * Metoda koja šalje poruku
     * @param adresa 
     */
    public void saljiPoruku(String adresa){
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
    private Message kreirajJMSPoruku(Session session, Object messageData) throws JMSException{
        TextMessage tm=session.createTextMessage();
        tm.setText(messageData.toString());
        return tm;
    }
    
    /**
     * Metoda koja šalje JMS poruku
     * @param messageData
     * @throws NamingException
     * @throws JMSException 
     */
    private void saljiJMSPoruku(Object messageData) throws NamingException, JMSException{
        Context c = new InitialContext();
        Connection conn=null;
        Session s=null;
        try{
            conn=nWTIS_vbencek.createConnection();
            s=conn.createSession(false,s.AUTO_ACKNOWLEDGE);
            MessageProducer mp=s.createProducer(nWTIS_vbencek_1);
            mp.send(kreirajJMSPoruku(s,messageData));
        }finally{
            if(s !=null){
                try{
                    s.close();
                }catch(JMSException e){
                    System.out.println(e.getMessage());
                }
            }if(conn!=null){
                conn.close();
            }
        }
    }
}
