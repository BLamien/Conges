package fr.univlr.cri.conges.databus;
import com.webobjects.appserver.WOApplication;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSMutableDictionary;

import fr.univlr.cri.conges.Application;
import fr.univlr.cri.conges.Session;
import fr.univlr.cri.webapp.CRIBasicDataBus;
import fr.univlr.cri.webapp.LRLog;

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

public class CngDataCenter {

  /** La cache local des gestionnaire d'acces a la base de donnees. */
  private NSMutableDictionary dataBusCache = new NSMutableDictionary();
  
  /** La session en cours */
  protected Session cngSession;
  
  /** Le message d'erreur (s'il existe) */
  protected String errorMessage;
  
  /**
   * Cree une nouvelle instance de gestionnaire central pour la session donnee.
   */
  public CngDataCenter(Session session) {
    cngSession = session;
  }

  /**
   * Cree une nouvelle instance de gestionnaire central sessionless
   */
  public CngDataCenter() {    
  }
  
  /**
   * Retourne la reference vers l'application en cours d'execution.
   */
  protected Application cngApp() {
    return (Application)WOApplication.application();
  }
// ============= Gestion des BUS ==================
  
  /**
   * Cree et retourne une instance du gestionnaire avec le nom
   * <code>busName</code> (le nom de la classe). Si une nouvelle instance est
   * creee, elle est ajoutee dans le cache local des gestionnaires. Elle sera
   * ensuite reutilisee prochaine fois que l'acces au bus <code>busName</code>
   * sera demande.
   */
  private CRIBasicDataBus getBusForName(String busName) {
    errorMessage = null;
    Object aBus = dataBusCache.objectForKey(busName);
    // Si le bus n'est pas dans le cache, on le cree
    if (aBus == null) {
      try {
        // On utilise la reflexion de Java
        Object arguments[] = {cngApp().dataBus().editingContext()};
        Class argumentTypes[] = {EOEditingContext.class};
        Class busClass = Class.forName(busName);
        aBus = busClass.getConstructor(argumentTypes).newInstance(arguments);
        // On memorise dans le cache
        dataBusCache.setObjectForKey(aBus, busName);
      } catch (Throwable e) {
        e.printStackTrace();
        errorMessage = LRLog.getMessageForException(e);
      }
    }
    // On definit les objets cles pour le nouveau bus
    if (aBus instanceof CngDataBus) {
      ((CngDataBus)aBus).setCngSession(cngSession);
      ((CngDataBus)aBus).setCngDataCenter(this);
    }
    return (CRIBasicDataBus)aBus;
  }
  

  /**
   * Retourne une instance du bus de la gestion des changements
   * intervenus dans la base de donnees <code>CngDbTodoBus</code>.
   */
  public CngDbTodoBus dbTodoBus() {
    return (CngDbTodoBus)getBusForName(CngDbTodoBus.class.getName());
  }
  
  /**
   * Retourne une instance du bus de la gestion des ordres
   * par email <code>CngMailTodoBus</code>.
   */
  public CngMailTodoBus mailTodoBus() {
    return (CngMailTodoBus)getBusForName(CngMailTodoBus.class.getName());
  }
  
  /**
   * Retourne une instance du bus de la gestion du planning
   * <code>CngPlanningBus</code>.
   */
  public CngPlanningBus planningBus() {
    return (CngPlanningBus)getBusForName(CngPlanningBus.class.getName());
  }

  /**
   * Retourne une instance du bus de la gestion des indivuds
   * <code>PlanningBus</code>.
   */
  public CngIndividuBus individuBus() {
    return (CngIndividuBus)getBusForName(CngIndividuBus.class.getName());
  }
  
  /**
   * Retourne une instance du bus de la gestion des structures
   * <code>CngStructureBus</code>.
   */
  public CngStructureBus structureBus() {
    return (CngStructureBus)getBusForName(CngStructureBus.class.getName());
  }
  
  /**
   * Retourne une instance du bus de la gestion des alertes
   * <code>CngAlerteBus</code>.
   */
  public CngAlerteBus alerteBus() {
    return (CngAlerteBus)getBusForName(CngAlerteBus.class.getName());
  }
  
  /**
   * Retourne une instance du bus de la gestion des horaires
   * <code>CngHoraireBus</code>.
   */
  public CngHoraireBus horaireBus() {
    return (CngHoraireBus)getBusForName(CngHoraireBus.class.getName());
  }
  
  /**
   * Retourne une instance du bus de la gestion des parametres
   * <code>CngParametreBus</code>.
   */
  public CngParametreBus paramsBus() {
    return (CngParametreBus)getBusForName(CngParametreBus.class.getName());
  }
  
  /**
   * Retourne une instance du bus de la gestion des occupations
   * <code>CngOccupationBus</code>.
   */
  public CngOccupationBus occupationBus() {
    return (CngOccupationBus)getBusForName(CngOccupationBus.class.getName());
  }
  
  /**
   * Retourne une instance du bus de la gestion des droits
   * <code>CngDroitBus</code>.
   */
  public CngDroitBus droitBus() {
    return (CngDroitBus)getBusForName(CngDroitBus.class.getName());
  }
  
  /**
   * Retourne une instance du bus de la gestion des preferences personnelles
   * <code>CngDroitBus</code>.
   */
  public CngPreferenceBus preferenceBus() {
    return (CngPreferenceBus)getBusForName(CngPreferenceBus.class.getName());
  }
}
