package fr.univlr.cri.conges;
// Generated by the WOLips TemplateEngine Plug-in at ${WOLipsContext.getDate()}

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

import fr.univlr.cri.conges.constantes.ConstsOccupation;
import fr.univlr.cri.conges.eos.modele.planning.EOCalculAffectationAnnuelle;
import fr.univlr.cri.conges.eos.modele.planning.EOMouvement;
import fr.univlr.cri.conges.eos.modele.planning.EOOccupation;
import fr.univlr.cri.conges.eos.modele.planning.EOPeriodeFermeture;
import fr.univlr.cri.conges.objects.I_Absence;
import fr.univlr.cri.conges.objects.Planning;
import fr.univlr.cri.conges.objects.occupations.HeuresSupplementaires;
import fr.univlr.cri.conges.utils.DateCtrlConges;
import fr.univlr.cri.conges.utils.TimeCtrl;
import fr.univlr.cri.util.StringCtrl;
import fr.univlr.cri.util.wo5.DateCtrl;

public class PersoFicheRose extends YCRIWebPage {

  // bindings
	public NSArray lesAbsences;
	public Planning lePlanning;
  public boolean isVisuAbsences = true;
  public I_Absence uneAbsence;
  private int leSoldeConges;
  private int leSoldeReliquat;
  public boolean isDisabled = false;

  
  public PersoFicheRose(WOContext context) {
    super(context);
  }
  
 // METHODES DE NAVIGATION

  /**
   * passer en mode visu fiche rose
   */
  public WOComponent goPageAbsences() {
    isVisuAbsences = true;
    return null;
  }

  /**
   * passer en mode visu hsup / ccomp
   */
  public WOComponent goPageHsupCcomp() {
    isVisuAbsences = false;
    return null;
  }

  public WOComponent detaillerUneAbsence() {
    ((Plannings) this.parent()).uneAbsence = uneAbsence;
    return ((Plannings) this.parent()).detaillerUneAbsence();
  }

  // CALCULS

  public NSArray lesAbsences() {
    lePlanning.setType("R");
    EOSortOrdering dateDebutOrdering = EOSortOrdering.sortOrderingWithKey("dateDebut", EOSortOrdering.CompareAscending);
    NSArray orderings = new NSArray(dateDebutOrdering);

    // init du solde conges
    leSoldeConges = 
    	(hasJrti() ? g_minutesCongesInitialesApresJrti() :
    		hasDecompteLegal() ? d_minutesCongesInitiales() :
    			isReliquatNegatif() ? c_minutesCongesInitiales() :
    				hasRegularisation() ? b_minutesCongesInitiales() :
    					lePlanning.congesInitiauxEnMinutes());
 
    // init du solde reliquats
    //leSoldeReliquat = f_minutesReliquatInitialesApresJrti();
    leSoldeReliquat = h_minutesReliquatInitialesApresBlocageReliquatsCet();
    
    // leSolde = compte.minutesRestantes().intValue();
    lesAbsences = lePlanning.absences();
    //TODO il y un NullPointerException ici de temps en temps
    lesAbsences = EOSortOrdering.sortedArrayUsingKeyOrderArray(lesAbsences, orderings);

    return lesAbsences;
  }

  public String soldeConges() {
    String leSoldeAAfficher = "";
 
    if (uneAbsence.dureeEnMinutes() > 0) {
      leSoldeConges -= uneAbsence.leDebitConges();
    }
    
    leSoldeAAfficher = TimeCtrl.stringHeureToDuree(TimeCtrl.stringForMinutes(leSoldeConges));

    if (uneAbsence.equals(lesAbsences.lastObject())) {
      EOCalculAffectationAnnuelle compte = lePlanning.affectationAnnuelle().calculAffAnn(lePlanning.type());
      leSoldeConges = compte.minutesTravaillees().intValue() - compte.minutesDues().intValue();
    }

    //LRLog.log("leSoldeAAfficher="+leSoldeAAfficher);
    //LRLog.log("leSoldeConges="+leSoldeConges);

    return leSoldeAAfficher;
  }

  public String soldeReliquats() {
    String leSoldeAAfficher = "";

    if (uneAbsence.dureeEnMinutes() > 0)
      leSoldeReliquat -= uneAbsence.leDebitReliquats();

    leSoldeAAfficher = TimeCtrl.stringHeureToDuree(TimeCtrl.stringForMinutes(leSoldeReliquat));

    if (uneAbsence.equals(lesAbsences.lastObject())) {
      EOCalculAffectationAnnuelle compte = lePlanning.affectationAnnuelle().calculAffAnn(lePlanning.type());
      leSoldeReliquat = compte.minutesReliquatRestantes().intValue();
    }

    if (leSoldeAAfficher.startsWith("-")) {
      leSoldeAAfficher = "00h00";
    }

    // si la date est > a la date max des reliquats -> 0
    if (DateCtrlConges.isAfter(uneAbsence.dateDebut(), lePlanning.affectationAnnuelle().dateFinReliquat())) {
      leSoldeAAfficher = "00h00";
    }

    return leSoldeAAfficher;
  }

  /**
   * recuperer la duree comptabilisée d'une heure sup
   * 
   * @return
   */
  public String dureeHSup() {
    HeuresSupplementaires hSup = (HeuresSupplementaires)uneAbsence;
    return TimeCtrl.stringHeureToDuree(TimeCtrl.stringForMinutes(hSup.laValeur()));
  }


  /**
   * faut-il afficher la ligne de regularisation
   */
  public boolean hasRegularisation() {
    return lePlanning.regularistationEnMinutes() != 0;
  }
  
  /**
   * Le signe precedent la mention Regularisation de solde
   * @return
   */
  public String getSigneRegularisation() {
  	if (lePlanning.regularistationEnMinutes() > 0) {
  		return "-";
  	} else {
  		return "";
  	}
  }
  
  /*============================================
   * les différentes valeurs des conges restants 
   * selon la position dans la fiche rose 
   ============================================*/

  /**
   * Heures sans regul, reliquat, decompte legal
   */
  public String a_heuresCongesInitiales() {
    int h = lePlanning.congesInitiauxEnMinutes();
    // regul
    h -= lePlanning.regularistationEnMinutes();
    // on reinjecte le reliquat s'il est negatif
    if (lePlanning.reliquatInitialEnMinutes() < 0)
      h -= lePlanning.reliquatInitialEnMinutes();
    return DateCtrlConges.to_duree(h);
  }

  /**
   * Inclusion regularisation
   * @return
   */
  public String b_heuresCongesInitiales() {
     return DateCtrlConges.to_duree(b_minutesCongesInitiales());
  }
  
  private int b_minutesCongesInitiales() {
    int h = lePlanning.congesInitiauxEnMinutes();
    // on reinjecte le reliquat s'il est negatif
    if (lePlanning.reliquatInitialEnMinutes() < 0)
      h -= lePlanning.reliquatInitialEnMinutes();
    return h;
  }
  
  /**
   * Inclusion reliquat
   * @return
   */
  public String c_heuresCongesInitiales() {
  	return DateCtrlConges.to_duree(c_minutesCongesInitiales());
  }
  
  private int c_minutesCongesInitiales() {
  	return lePlanning.congesInitiauxEnMinutes();
  }

  /**
   * Inclusion decompte legal
   * @return
   */
  public String d_heuresCongesInitiales() {
  	return DateCtrlConges.to_duree(d_minutesCongesInitiales() );
  }
  
  private int d_minutesCongesInitiales() {
    int h = lePlanning.congesInitiauxEnMinutes();
    // decompte legal
    h -= lePlanning.decompteLegalEnMinutes();
    return h;
  }

  /**
   * Après passage des divers decomptes, on affiche le 
   * reliquat initial si positif, si negatif il a deja
   * ete decompte, donc il est nul
   */
  public String e_reliquatInitialPositif() {
  	return DateCtrlConges.to_duree(e_reliquatInitialPositifEnMinutes());
  }
  
  private int e_reliquatInitialPositifEnMinutes() {
    int minutesReliquat = lePlanning.reliquatInitialEnMinutes();
    if (minutesReliquat < 0)
      minutesReliquat = 0;
    return minutesReliquat;
  }
  
  /**
   * RELIQUAT : Inclusion du rachat de conges
   */
  public String f_heuresReliquatInitialesApresJrti() {
  	return DateCtrlConges.to_duree(f_minutesReliquatInitialesApresJrti());
  }
  
  private int f_minutesReliquatInitialesApresJrti() {
  	int minutesReliquat = e_reliquatInitialPositifEnMinutes();
  	if (hasJrti()) {
  		minutesReliquat -= lePlanning.getJrti().leDebitReliquats();
  	}
  	return minutesReliquat;
  }
  
  /**
   * CONGES : Inclusion du rachat de conges
   */
  public String g_heuresCongesInitialesApresJrti() {
  	return DateCtrlConges.to_duree(g_minutesCongesInitialesApresJrti());
  }
  
  private int g_minutesCongesInitialesApresJrti() {
  	int minutesConges = d_minutesCongesInitiales();
  	if (hasJrti()) {
  		minutesConges -= lePlanning.getJrti().leDebitConges();
  	}
  	return minutesConges;
  }
  
  /**
   * RELIQUAT : Inclusion du rachat de conges
   */
  public String h_heuresReliquatInitialesApresBlocageReliquatsCet() {
  	return DateCtrlConges.to_duree(h_minutesReliquatInitialesApresBlocageReliquatsCet());
  }
  
  private int h_minutesReliquatInitialesApresBlocageReliquatsCet() {
  	int minutesReliquat = f_minutesReliquatInitialesApresJrti();
  	if (hasBlocageReliquatsCet()) {
  		minutesReliquat -= lePlanning.getBlocageReliquatsCet().leDebitReliquats();
  	}
  	return minutesReliquat;
  }
  
  /**
   * CONGES : Inclusion du rachat de conges
   */
  public String i_heuresCongesInitialesApresBlocageReliquatsCet() {
  	return DateCtrlConges.to_duree(i_minutesCongesInitialesApresBlocageReliquatsCet());
  }
  
  private int i_minutesCongesInitialesApresBlocageReliquatsCet() {
  	int minutesConges = g_minutesCongesInitialesApresJrti();
  	if (hasBlocageReliquatsCet()) {
  		minutesConges -= lePlanning.getBlocageReliquatsCet().leDebitConges();
  	}
  	return minutesConges;
  }
  
  /**
   * savoir s'il faut generer une ligne dans la fiche rose (conges comp > heures sup)
   */
  public boolean isBilanHSupCCompNegatif() {
    EOCalculAffectationAnnuelle compte = lePlanning.affectationAnnuelle().calculAffAnn(lePlanning.type());
    return compte.minutesBilan().intValue() < 0;
  }

  /**
   * faut-il afficher la ligne de reliquat negatif ?
   * 
   * @return
   */
  public boolean isReliquatNegatif() {
    return lePlanning.reliquatInitial().startsWith("-");
  }

  /**
   * faut-il afficher la ligne de decompte de conges legal (70 ou 140 heures) ?
   * 
   * @return
   */
  public boolean hasDecompteLegal() {
    return lePlanning.decompteLegalEnMinutes() > 0;
  }

  /**
   * Faut-il afficher la ligne de rachat de conges (JRTI)
   */
  public boolean hasJrti() {
  	return lePlanning.getJrti() != null;
  }
  
  public String debitJrti() {
  	return lePlanning.getJrti().debit();
  }

  /**
   * Faut-il afficher la ligne de blocage de reliquats pour bascule CET
   */
  public boolean hasBlocageReliquatsCet() {
  	return lePlanning.getBlocageReliquatsCet() != null;
  }
  
  public String debitBlocageReliquatsCet() {
  	return lePlanning.getBlocageReliquatsCet().debit();
  }
  
  /**
   * affichage de la valeur du reliquat dans la colonne debit s'il est negatif (-1h00 -> 1h00)
   */
  public String debitReliquatNegatif() {
    return lePlanning.reliquatInitial().substring(1);
  }

  /**
   * derniere ligne : si le reliquat initial > 0
   */
  public String reliquatFinal() {
    String reliquat = lePlanning.reliquatRestant();
    if (reliquat.startsWith("-")) {
      reliquat = "00h00";
    }
    return reliquat;
  }

  /**
   * affichage en heures du bilan hsup / ccomp
   * 
   * @return
   */
  public String heuresBilan() {
    EOCalculAffectationAnnuelle compte = lePlanning.affectationAnnuelle().calculAffAnn(lePlanning.type());
    return TimeCtrl.stringHeureToDuree(TimeCtrl.stringForMinutes(compte.minutesBilan().intValue()));
  }

  /**
   * affichage en heures du bilan hsup / ccomp dans la fiche rose (debit : on enleve le '-')
   */
  public String heuresBilanNegatif() {
    return StringCtrl.replace(heuresBilan(), "-", "");
  }
  

  private boolean wasShownLigneCET = false;

  /**
   * Affichage de la ligne qui indique l'etat du CET pour l'annee.
   * Elle est affichee si : 
   * - elle n'a pas deja ete affichees
   * - la date max de reliquat est depassee
   * - il y a des reliquats non consomm�s.
   */
  public boolean showLigneCET() {
  	// pas deja affichee
  	if (!wasShownLigneCET) {
  		// date depassee
    	if (DateCtrlConges.isAfter(DateCtrlConges.now(), 
    			lePlanning.affectationAnnuelle().dateFinReliquat())) {
    		// reliquat non consomme
    	}
  	}
  	return false;
  }
  
  
  /**
   * solde apres le bilan hsup / ccomp negatif
   */
  public String soldeApresBilan() {
    EOCalculAffectationAnnuelle compte = lePlanning.affectationAnnuelle().calculAffAnn(lePlanning.type());
    return TimeCtrl.stringHeureToDuree(TimeCtrl.stringForMinutes(compte.minutesRestantes().intValue()));
  }

  public String hSupTotal() {
    int total = 0;
    for (int i = 0; i < lesAbsences.count(); i++) {
      I_Absence uneAbs = (I_Absence) lesAbsences.objectAtIndex(i);
      if (uneAbs.isAbsenceBilan() && ((EOOccupation) uneAbs).typeOccupation().isHeuresSupplementaires()) {
        total += uneAbs.dureeEnMinutes();
      }
    }
    return TimeCtrl.stringHeureToDuree(TimeCtrl.stringForMinutes(total));
  }

  public String hSupValid() {
    int total = 0;
    for (int i = 0; i < lesAbsences.count(); i++) {
      I_Absence uneAbs = (I_Absence) lesAbsences.objectAtIndex(i);
      if (uneAbs.isAbsenceBilan() && ((EOOccupation) uneAbs).typeOccupation().isHeuresSupplementaires() && 
      		ConstsOccupation.LIBELLE_VALIDEE.equals(uneAbs.libelleStatut())) {
        total += uneAbs.dureeEnMinutes();
      }
    }
    return TimeCtrl.stringHeureToDuree(TimeCtrl.stringForMinutes(total));
  }

  public String cCompTotal() {
    int total = 0;
    for (int i = 0; i < lesAbsences.count(); i++) {
      I_Absence uneAbs = (I_Absence) lesAbsences.objectAtIndex(i);
      if (uneAbs.isAbsenceBilan() && ((EOOccupation) uneAbs).typeOccupation().isCongesCompensateurs()) {
        total += -uneAbs.dureeEnMinutes();
      }
    }
    return TimeCtrl.stringHeureToDuree(TimeCtrl.stringForMinutes(total));
  }

  public String cCompValid() {
    int total = 0;
    for (int i = 0; i < lesAbsences.count(); i++) {
      I_Absence uneAbs = (I_Absence) lesAbsences.objectAtIndex(i);
      if (uneAbs.isAbsenceBilan() && ((EOOccupation) uneAbs).typeOccupation().isCongesCompensateurs() && 
      		ConstsOccupation.LIBELLE_VALIDEE.equals(uneAbs.libelleStatut())) {
        total += -uneAbs.dureeEnMinutes();
      }
    }
    return TimeCtrl.stringHeureToDuree(TimeCtrl.stringForMinutes(total));
  }

  public boolean isPlanningValide() {
    boolean isPlanningValide = false;

    if (lePlanning.isValide()) {
      isPlanningValide = true;
    }
    return isPlanningValide;
  }

  /**
   * saisie occupation depuis la fiche rose
   * 
   * @return
   */
  public PageSaisieOccupation saisirOccupationFicheRose() {
    return ((Plannings) this.parent()).saisirOccupationPourTypeEtDate(
        laSession.typeCongeAnnuel(), DateCtrlConges.now());
  }

  /**
   * saisie heure sup
   */
  public PageSaisieOccupation saisirOccupationHeureSup() {
    return ((Plannings) this.parent()).saisirOccupationPourTypeEtDate(
        laSession.typeHeuresSupplementaires(), DateCtrlConges.now());
  }

  /**
   * saisie conges compensateurs
   */
  public PageSaisieOccupation saisirOccupationCongesComp() {
    return ((Plannings) this.parent()).saisirOccupationPourTypeEtDate(
        laSession.typeCongesCompensateurs(), DateCtrlConges.now());
  }

  
  // GETTER
  
  
  public NSArray lesAbsencesFicheRose() {
    EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("isAbsenceBilan = %@", new NSArray(new Boolean(false)));
    return EOQualifier.filteredArrayWithQualifier(lesAbsences(), qual);
  }
  
  public NSArray lesAbsencesCComp() {
   EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("isAbsenceBilan = %@ AND typeOccupation.isCongesCompensateurs = %@", 
        new NSArray(new Boolean[]{new Boolean(true), new Boolean(true)}));
    return EOQualifier.filteredArrayWithQualifier(lesAbsences(), qual);
  }
  
  public NSArray lesAbsencesHSup() {
  	EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("isAbsenceBilan = %@ AND typeOccupation.isHeuresSupplementaires = %@", 
  			new NSArray(new Boolean[]{new Boolean(true), new Boolean(true)}));
    return EOQualifier.filteredArrayWithQualifier(lesAbsences(), qual);
  }
  
  public int indexAbsence;
  
  // SETTER
  public void setDisabled(boolean value) {
    isDisabled = value;
  }
  
  
  // BOOLEAN INTERFACE
 
  public boolean isValidee() {
    return ConstsOccupation.LIBELLE_VALIDEE.equals(uneAbsence.libelleStatut());
  }

  public boolean isEnCoursDeValidation() {
    return ConstsOccupation.LIBELLE_EN_COURS_DE_VALIDATION.equals(uneAbsence.libelleStatut());
  }

  public boolean isEnCoursDeSuppression() {
    return ConstsOccupation.LIBELLE_EN_COURS_DE_SUPPRESSION.equals(uneAbsence.libelleStatut());
  }
  
  public boolean isVisee() {
    return ConstsOccupation.LIBELLE_VISEE.equals(uneAbsence.libelleStatut());
  }
  
  
  // alternance des couleurs des lignes

  private final static String TR_LIGNE_CLASS_PAIR 		= "pair";
  private final static String TR_LIGNE_CLASS_IMPAIR 	= "impair";
  
  /**
   * La classe CSS associée à une des lignes de la fiche rose (et verte pour les heures supp.)
   */
  public String getTrLigneClass() {
    String trLigneClass = null;
    if (indexAbsence % 2 == 0)
      trLigneClass = TR_LIGNE_CLASS_PAIR;     
    else
    	trLigneClass = TR_LIGNE_CLASS_IMPAIR;
    return trLigneClass;
  }
  
  
 // infobulles
 
  /**
   * L'infobulle sur le debit de l'absence {@link #uneAbsence}
   * On met en gras les valeur non vides.
   * on affiche tout le temps le reliquat et le conges.
   * Le CET et la decharge syndicale uniquement s'ils sont non vides
   */
  public String getDebitHtmlTip() {
 	 int debitReliquat = 0;
 	 int debitConges = 0;
 	 int debitCet = 0;
 	 int debitDechargeSyndicale = 0;
 	 if (!uneAbsence.isCongeLegal()) {
 		 if (uneAbsence.isFermeture()) {
 			 EOPeriodeFermeture uneEOPeriodeFermeture = (EOPeriodeFermeture) uneAbsence;
 			 debitReliquat 					= uneEOPeriodeFermeture.leDebitReliquats();
 			 debitConges		 				= uneEOPeriodeFermeture.leDebitConges();
 			 debitCet 							= uneEOPeriodeFermeture.leDebitCET();
 			 debitDechargeSyndicale = uneEOPeriodeFermeture.leDebitDechargeSyndicale();
 		 } else {
 			 EOOccupation uneEOOccupation = (EOOccupation) uneAbsence;
 			 debitReliquat 					= uneEOOccupation.leDebitReliquats();
 			 debitConges		 				= uneEOOccupation.leDebitConges();
 			 debitCet 							= uneEOOccupation.leDebitCET();
 			 debitDechargeSyndicale = uneEOOccupation.leDebitDechargeSyndicale();
 		 }
 	 }
 	 StringBuffer sb = new StringBuffer();
 	 sb.append("<table><tr><th colspan=2 nowrap>D&eacute;tail des d&eacute;bits</th>");
 	 sb.append("<tr><th>Compte</th><th>Valeur</th><tr>");
 	 // ligne de reliquats
 	 sb.append("<tr><td>Reliquats</td><td align=right>");
 	 if (debitReliquat > 0) {
 		 sb.append("<font color=red><b>");
 	 }
 	 sb.append(TimeCtrl.stringForMinutes(debitReliquat));
 	 if (debitReliquat > 0) {
 		 sb.append("</b></font>");
 	 }
 	 sb.append("</td></tr>");
 	 // ligne de conges
 	 sb.append("<tr><td>Cong&eacute;s</td><td align=right>");
 	 if (debitConges > 0) {
 		 sb.append("<font color=red><b>");
 	 }
 	 sb.append(TimeCtrl.stringForMinutes(debitConges));
 	 if (debitConges > 0) {
 		 sb.append("</b></font>");
 	 }
 	 sb.append("</td></tr>");
 	 // ligne de CET
 	 if (debitCet > 0) {
 		 sb.append("<tr><td><i>CET</i></td><td align=right><font color=#009933><b>");
 		 sb.append(TimeCtrl.stringForMinutes(debitCet)).append("</b></font></td></tr>");
 	 }
 	 // ligne de decharge syndicale
 	 if (debitDechargeSyndicale > 0) {
 		 sb.append("<tr><td><i>D&eacute;charge syndicale</i></td><td align=right><font color=#009933><b>");
 		 sb.append(TimeCtrl.stringForMinutes(debitDechargeSyndicale)).append("</b></font></td></tr>");
 	 }
 	 // totaux
 	 sb.append("<tr><td colspan=2><hr/></td></tr>");
 	 sb.append("<tr><td><i><b>Totaux</b></i></td><td align=right><b>");
 	 sb.append(TimeCtrl.stringForMinutes(debitReliquat + debitConges + debitCet + debitDechargeSyndicale));
 	 sb.append("</b></font></td></tr>");
 	 sb.append("</table>");
 	 return sb.toString();
  }
  
  /**
   * L'infobulle sur le statut de l'absence {@link #uneAbsence} :
   * - la date de saisie
   * - la date de visa et par qui
   * - la date de validation et par qui
   */
  public String getStatutHtmlTip() {

 	 StringBuffer sb = new StringBuffer();

 	 boolean isAbsenceSaisieParRh = uneAbsence.isFermeture() || uneAbsence.isCongeLegal();
 	 
	 sb.append("<table><tr><td nowrap>");
 	 
 	 // -- saisie
 	 sb.append("<b>Saisie</b> : ");
 	 // qui
 	 if (uneAbsence.delegue() != null) {
 		 sb.append(uneAbsence.delegue().nomComplet());
 	 } else if (!isAbsenceSaisieParRh) {
 		 sb.append(lePlanning.affectationAnnuelle().individu().nomComplet());
 	 } else {
 		 sb.append("service RH");
 	 }
 	 sb.append(" (le ");
 	 // quand
 	 sb.append(DateCtrl.dateToString(uneAbsence.dateCreation()));
 	 sb.append(")<br>");
 	 

 	 if (!isAbsenceSaisieParRh) {

 		 // -- visa
 		 sb.append("<b>Visa</b> : ");
 		 // qui
 		 if (uneAbsence.viseur() != null) {
 			 sb.append(uneAbsence.viseur().nomComplet());
 		 }
 	 
 		 // quand
 		 if (uneAbsence.viseur() != null) {
 			 sb.append(" (le ");
 			 sb.append(DateCtrl.dateToString(uneAbsence.dateVisa()));
 			 sb.append(")");
 		 }
 		 sb.append("<br>");
 	 
 		 // -- validation
 		 sb.append("<b>Validation</b> : ");
 		 // qui
 		 if (uneAbsence.valideur() != null) {
 			 sb.append(uneAbsence.valideur().nomComplet());
 		 }
 	 
 		 // quand
 		 if (uneAbsence.valideur() != null) {
 			 sb.append(" (le ");
 			 sb.append(DateCtrl.dateToString(uneAbsence.dateValidation()));
 			 sb.append(")");
 		 }
 	 }
 		 
 	 sb.append("</td></tr></table>");
	 
	 return sb.toString();
  }
  
  /**
   * L'infobulle sur la régularisation de solde :
   * - affiche la liste des régularisations
   */
  public String getRegularisationHtmlTip() {

 	 StringBuffer sb = new StringBuffer();

	 sb.append("<table><tr><th colspan = 2>D&eacute;tail</th></tr>");
	 sb.append("<tr><th>Motif</th><th>Cr&eacute;dit</th></tr>");
	 
	 // listes des régularisations
	 NSArray regularisationList = lePlanning.affectationAnnuelle().tosMouvementRegularisationSoldeConges();
	 
	 for (int i=0; i<regularisationList.count(); i++) {
		 
		 EOMouvement regularisation = (EOMouvement) regularisationList.objectAtIndex(i);
	
		 sb.append("<tr><td align = left>").append(regularisation.mouvementLibelle()).append("</td><td align = right>");
		 sb.append(regularisation.mouvementHeures()).append("</td></tr>");
	 }

	 sb.append("<tr><td colspan = 2><hr></td></tr>");
 
	 sb.append("<tr><th align = right>TOTAL</th><th align = right>");
	 sb.append(lePlanning.regularistationEnHeures()).append("<th></tr>");
	 
 	 sb.append("</table>");
	 
	 return sb.toString();
  }
  
  /**
   * Le formatteur des dates de début des absences (détail des heures pour
   * les occupations à la minute, sinon mention AM/PM)
   * @return
   */
  public String getDateFormatUneAbsenceFicheRose() {
  	String dateFormat = null;
  	
  	if (uneAbsence.isOccupationMinute()) {
  		dateFormat = ConstsOccupation.DATE_FORMAT_OCCUPATION_MINUTE;
  	} else {
  		dateFormat = ConstsOccupation.DATE_FORMAT_OCCUPATION_DEMI_JOURNEE;
  	}
  	
  	return dateFormat;
  }  
 
}