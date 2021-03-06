/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ejb.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.foi.nwtis.vbencek.ejb.eb.SpremistePoruka;

/**
 * Klasa koja služi za primanje JMS poruka vezanih uz poruke dodavanja aerodroma iz Web socketa
 * @author vbencek
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_vbencek_1"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class PrimateljPoruka implements MessageListener {
    
    
    
    @Override
    public void onMessage(Message message) {
        try {
            String poruka=message.getBody(String.class);
            System.out.println("ejb modul 3: stigla poruka: "+poruka);
            SpremistePoruka.dodajPoruku(poruka);
        } catch (JMSException ex) {
             System.out.println(ex.getMessage());
        }
    }
    
    
    
}
