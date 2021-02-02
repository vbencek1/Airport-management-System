/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.vbencek.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.vbencek.ejb.eb.Airplanes;

/**
 *
 * @author NWTiS_2
 */
@Local
public interface AirplanesFacadeLocal {

    void create(Airplanes airplanes);

    void edit(Airplanes airplanes);

    void remove(Airplanes airplanes);

    Airplanes find(Object id);

    List<Airplanes> findAll();

    List<Airplanes> findRange(int[] range);

    int count();
    List<Airplanes> dohvatiAvioneOdDo(String icao,long odVr,long doVr);
    List<Airplanes> dohvatiLetAviona(String icao24,long odVr,long doVr);
    int dohvatiBrojAerodroma(String aerodrom);
    
}
