package fr.univlr.cri.conges.objects.occupations;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.conges.eos.modele.planning.EOOccupation;
import fr.univlr.cri.conges.eos.modele.planning.EOTypeOccupation;
import fr.univlr.cri.conges.objects.Planning;

/**
 * Gestion des decharges syndicales (cong�s DRH qui d�cr�mente le compte
 * d�charge syndicale de l'agent en priorit�, puis reliquat et cong�s)
 * 
 * @author ctarade
 */
public class DechargeSyndicale extends Occupation {

	/** le code du type d'occupation a la minute */
	public final static String LIBELLE_COURT_MINUTE = "C_DECH_SYND";
	/** le code du type d'occupation a la demi journ�e */
	public final static String LIBELLE_COURT_JOUR = "C_DECH_SYND_J";
 
  public DechargeSyndicale(EOTypeOccupation unType, Planning unPlanning, NSTimestamp debutTS, NSTimestamp finTS, String unMotif, EOEditingContext ec) {
    super(unType, unPlanning, debutTS, finTS, unMotif, ec);
  }

  public DechargeSyndicale(EOOccupation uneOccupation, Planning unPlanning, EOEditingContext ec) {
    super(uneOccupation, unPlanning, ec);
  }

  public DechargeSyndicale(EOOccupation uneOccupation, EOEditingContext ec) {
    super(uneOccupation, ec);
  }
  
  /**
   * Cas specifique, l'acceptation de la decharge syndicale 
   * ne recredite pas ...
   * TODO prevoir un nouvel attribut dans PLNG_TYP_OCC pour �a
   */
  public void accepter() {

  }
}
