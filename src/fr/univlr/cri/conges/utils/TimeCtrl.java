/*
 * Copyright Universit� de La Rochelle 2004
 *
 * ctarade@univ-lr.fr
 *
 * Ce logiciel est un programme informatique servant � g�rer les comptes
 * informatiques des utilisateurs. 
 * 
 * Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilit� au code source et des droits de copie,
 * de modification et de redistribution accord�s par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limit�e.  Pour les m�mes raisons,
 * seule une responsabilit� restreinte p�se sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les conc�dants successifs.

 * A cet �gard  l'attention de l'utilisateur est attir�e sur les risques
 * associ�s au chargement,  � l'utilisation,  � la modification et/ou au
 * d�veloppement et � la reproduction du logiciel par l'utilisateur �tant 
 * donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � 
 * manipuler et qui le r�serve donc � des d�veloppeurs et des professionnels
 * avertis poss�dant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invit�s � charger  et  tester  l'ad�quation  du
 * logiciel � leurs besoins dans des conditions permettant d'assurer la
 * s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement, 
 * � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�. 

 * Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accept� les
 * termes.
 */

package fr.univlr.cri.conges.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.util.StringCtrl;

/**
 * @author ctarade
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class TimeCtrl {

  // GET MINUTES : Retourne le nombre de minutes correspondant � la chaine string au format %H:%M (l'inverse de stringFor: )
  public static int getMinutes(String chaine) {
    NSArray str = NSArray.componentsSeparatedByString(chaine, ":");
    int nombre = 0;

    if (StringCtrl.isEmpty(chaine) || ("00:00".equals(chaine)) || ("".equals(chaine)) || ("..:..".equals(chaine)))
      return 0;

    if (chaine.length() == 0)
      return 0;

    if (str.count() == 1)
      nombre = ((Number) str.objectAtIndex(0)).intValue() * 60;
    else {
      if ((((Number) new Integer((String) str.objectAtIndex(0))).intValue()) < 0)
        nombre = (-((((Number) new Integer((String) str.objectAtIndex(0))).intValue()) * 60) + ((((Number) new Integer((String) str.objectAtIndex(1)))
            .intValue())));
      else
        nombre = (((((Number) new Integer((String) str.objectAtIndex(0))).intValue()) * 60) + ((((Number) new Integer((String) str.objectAtIndex(1)))
            .intValue())));
    }

    if ((((Number) new Integer((String) str.objectAtIndex(0))).intValue()) < 0)
      nombre = -nombre; // on a passe une valeur negative

    return nombre;
  }

  // STRING FOR MINUTES
  // Formatte le nombre de minutes en une chaine au format %H:%M (l'inverse de numberOfMinutesFor: )
  public static String stringForMinutes(int numberOfMinutes) {
    String formattedString;
    int hours, minutes;
    boolean negatif = false;

    if (numberOfMinutes == 0)
      return "00:00";

    if (numberOfMinutes < 0) {
      negatif = true;
      numberOfMinutes = -numberOfMinutes;
    }

    hours = numberOfMinutes / 60;
    minutes = numberOfMinutes % 60;

    if (hours < 10)
      formattedString = "0" + hours;
    else
      formattedString = "" + hours;

    if (minutes < 10)
      formattedString = formattedString + ":0" + minutes;
    else
      formattedString = formattedString + ":" + minutes;

    if (negatif)
      formattedString = "-" + formattedString;

    return formattedString;
  }

  /**
   * retourne le nombre de minutes ecoul�es dans la journ�e
   * 
   * @param date
   * @return
   */
  public static int getMinutesOfDay(NSTimestamp aDate) {

    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(aDate);
    return calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;

  }

  /**
   * mettre une date a minuit (le jour meme)
   * 
   * @param aDate
   * @return
   */
  public static NSTimestamp dateToMinuit(NSTimestamp aDate) {
    GregorianCalendar nowGC = new GregorianCalendar();
    nowGC.setTime(aDate);
    return aDate.timestampByAddingGregorianUnits(0, 0, 0, -nowGC.get(GregorianCalendar.HOUR_OF_DAY), -nowGC.get(GregorianCalendar.MINUTE), -nowGC
        .get(GregorianCalendar.SECOND));
  }

  /**
   * permet de transformer une heure en dur�e (remplace le : par un h
   * 
   * @param heure
   * @return
   */
  public static String stringHeureToDuree(String heure) {
    if (heure != null)
      return heure.replace(':', 'h');
    else
      return "00h00";
  }

  /**
   * permet de transformer une dur�e en heure (remplace le h par un :
   * 
   * @param heure
   * @return
   */
  public static String stringDureeToHeure(String heure) {
    if (heure != null)
      return heure.replace('h', ':');
    else
      return "00:00";
  }

  // TODO controler si null ou po
  public static int getHeuresFromString(String heuresMinutes) {
    return (int) ((float) TimeCtrl.getMinutes(heuresMinutes) / (float) 60);
  }

  public static int getMinutesFromString(String heuresMinutes) {
    return TimeCtrl.getMinutes(heuresMinutes) % 60;
  }

  public static String to_duree_en_jours(int minutes, String dureeJour) {
    String to_duree = "";
    double lesJours = (double) minutes / (double) (TimeCtrl.getMinutes(TimeCtrl.stringDureeToHeure(dureeJour)));
    Double unNombre = new Double(lesJours);
    NSNumberFormatter numberFormat = new NSNumberFormatter("0.00");
    to_duree += numberFormat.format(unNombre) + "j";
    return to_duree;
  }
}
