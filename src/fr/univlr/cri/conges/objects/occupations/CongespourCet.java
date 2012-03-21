/*
 * Copyright Universit� de La Rochelle 2006
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
package fr.univlr.cri.conges.objects.occupations;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.conges.eos.modele.planning.EOOccupation;
import fr.univlr.cri.conges.eos.modele.planning.EOTypeOccupation;
import fr.univlr.cri.conges.objects.JourReliquatCongesNode;
import fr.univlr.cri.conges.objects.Planning;

public class CongespourCet extends Occupation  {

  public final static String LIBELLE_COURT = "C_CET";

  public CongespourCet(EOTypeOccupation unType, Planning unPlanning, NSTimestamp debutTS, NSTimestamp finTS, String unMotif, EOEditingContext ec) {
    super(unType, unPlanning, debutTS, finTS, unMotif, ec);
  }

  public CongespourCet(EOOccupation uneOccupation, Planning unPlanning, EOEditingContext ec) {
    super(uneOccupation, unPlanning, ec);
  }

  public CongespourCet(EOOccupation uneOccupation, EOEditingContext ec) {
    super(uneOccupation, ec);
  }
  
  /**
   * 
   */
  public boolean isValide() {
    boolean isValide = super.isValide();
/*
    // 5 jours ouvrables minimun
    if (isValide) {
      NSArray lesJoursOccupes = lePlanning.lesJours(dateDebut(), dateFin());
      int nbJourOuvrable = 0;

      for (int i = 0; i < lesJoursOccupes.count(); i++) {
        Jour unJour = (Jour) lesJoursOccupes.objectAtIndex(i);
        if (unJour.dureeTravailleeEnMinutes() > 0) {
          nbJourOuvrable++;
        }
      }

      if (nbJourOuvrable < 5) {
        isValide = false;
        setErrorMsg("La dur�e d'un conges CET ne peut-�tre inf�rieure � 5 jours ouvrables.");
      }
    }

    // la duree ne doit pas depasse le total du CET restant
    /*if (isValide) {
      calculerValeur();
      if (laValeur() > lePlanning.toCET().minutesRestantes().intValue()) {
        isValide = false;
        setErrorMsg("La dur�e de ce cong� d�passe votre cr�dit de CET (" +
            TimeCtrl.stringForMinutes(laValeur()) + " > " + 
            TimeCtrl.stringForMinutes(lePlanning.toCET().minutesRestantes()) + ")"
        );
      }
      
    }*/
    
    
    return isValide;
  }
  
  /**
   * 
   */
  public void calculerValeur() {
  	super.calculerValeur();
  }

  
  /**
   * Cas particulier pour le CET, lors de l'acceptation,
   * le droit a cong� n'est pas recredit�, c'est juste 
   * un d�calage des debits vers le CET (tout se fait dans
   * la methode {@link JourReliquatCongesNode#accepter(String)}
   */
  public void accepter() {
   	for (int i = 0; i < lesNodesJours().count(); i++) {
      JourReliquatCongesNode unNode = (JourReliquatCongesNode) lesNodesJours().objectAtIndex(i);
      unNode.accepter(lePlanning.type());
    }
    // on remet a jour les valeurs de debit reliquats / conges et cet qui ont forcement
   	// change entre la confirmation et l'acceptation
    setLeDebitReliquats(((Number) (lesNodesJours().valueForKeyPath("@sum."+JourReliquatCongesNode.DEBIT_RELIQUATS_KEY))).intValue());
    setLeDebitConges(((Number) (lesNodesJours().valueForKeyPath("@sum."+JourReliquatCongesNode.DEBIT_CONGES_KEY))).intValue());
    setLeDebitCET(((Number) (lesNodesJours().valueForKeyPath("@sum."+JourReliquatCongesNode.DEBIT_CET_KEY))).intValue());

  }
}
