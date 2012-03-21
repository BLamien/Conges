package fr.univlr.cri.conges.databus;


import java.util.Hashtable;

import com.webobjects.appserver.WOApplication;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import fr.univlr.cri.conges.Application;
import fr.univlr.cri.conges.Session;
import fr.univlr.cri.webapp.CRIDataBus;
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

public class CngDataBus extends CRIDataBus {

  /** La session en cours, si elle existe. */
  private Session _cngSession;
  
  /** Le gestionnaire des "dataBus" gerant cet objet. */
  private CngDataCenter _cngDataCenter;
  
  /** Le message de la derniere erreur */
  private String _errorMessage;

  /** 
   * le pointeur vers l'editing context qui a servit pour instancier cet objet 
   * Peut servir si on ne veut pas uiliser le context par defaut de CRIDatabus
   * qui peut avoir des locks en multithread
   */
  private EOEditingContext ec;
  
  /**
   * Cree une nouvelle instance de data bus.
   */
  public CngDataBus(EOEditingContext editingContext) {
    super(editingContext);
    ec = editingContext;
 }

  /**
   * Retourne la reference vers la session en cours.
   */
  protected Session cngSession() {
    return _cngSession;
  }

  /**
   * Definit la reference vers la session en cours.
   */
  protected void setCngSession(Session session) {
    this._cngSession = session;
  }
  
  /**
   * Retourne la reference vers le gestionnaire des "dataBus" permettant
   * d'acceder � cette instance de l'objet.
   */
  protected CngDataCenter cngDataCenter() {
    return _cngDataCenter;
  }
  
  /**
   * Definit la reference vers le gestionnaire des "dataBus" qui permettra
   * acceder a cette instance de l'objet.
   */
  protected void setCngDataCenter(CngDataCenter dataCenter) {
    _cngDataCenter = dataCenter;
  }
  
  /**
   * Retourne la reference vers l'application en cours.
   */
  protected Application cngApp() {
    return (Application)WOApplication.application();  
  }

  /**
   * Retourne la reference version les informations utilisateur actuellement
   * connecte.
   */
  protected CngUserInfo userInfo() {
    return _cngSession.cngUserInfo();
  }

  /**
   * Retourne le dernier message d'erreur ou <i>null</i> si aucun message.
   */
  public String getErrorMessage() {
    return _errorMessage;
  }
 
  /**
   * Definit un message d'erreur.
   */
  public void setErrorMessage(String message) {
    this._errorMessage = message;
  }
  
  /**
   * Teste si la derniere operation avec la base de donnees etait executee
   * avec les erreurs.
   */
  public boolean hasError() {
    return (_errorMessage != null);
  }
  
  /**
   * Execute une requete definie par la condition <code>qualifier</code>.
   * Retourne un tableau des objets de sont resultat.
   * Modification de <i>CRIBasicDataBus</i> qui par defaut
   * useDistinct=true pour les EOFetchSpecification.
   */
  public NSArray fetchArray(String nomTable, EOQualifier qualifier, NSArray sort) {
    return super.fetchArray(super.editingContext(), nomTable, qualifier, 
        sort, false, super.refreshFetchedObjects());
  }

  /**
   * Execute une requete definie par la condition <code>qualifier</code>
   * sur la transaction en parametre. A utiliser si on veut supprimer des 
   * des objets qui contiennent des to-many.
   * 
   * @param transId : l'identifiant de la transaction
   */
  public NSArray fetchArray(Integer transId, String nomTable, EOQualifier qualifier, NSArray sort) {
    return super.fetchArray(econtextForTransaction(transId), nomTable, qualifier, 
        sort, false, super.refreshFetchedObjects());
  }
  
  // References vers les autres bus
  
  /**
   * Retourne une instance du bus de la gestion des changements
   * intervenus dans la base de donnees <code>CngDbTodoBus</code>.
   */
  public CngDbTodoBus dbTodoBus() {
    return cngDataCenter().dbTodoBus();
  }
  
  /**
   * Retourne une instance du bus de la gestion du planning
   * <code>CngPlanningBus</code>.
   */
  public CngPlanningBus planningBus() {
    return cngDataCenter().planningBus();
  }

  /**
   * Retourne une instance du bus de la gestion des indivuds
   * <code>PlanningBus</code>.
   */
  public CngIndividuBus individuBus() {
    return cngDataCenter().individuBus();
  }
  
  /**
   * Retourne une instance du bus de la gestion des structures
   * <code>CngStructureBus</code>.
   */
  public CngStructureBus structureBus() {
    return cngDataCenter().structureBus();
  }
  
  /**
   * Retourne une instance du bus de la gestion des structures
   * <code>CngStructureBus</code>.
   */
  public CngOccupationBus occupationBus() {
    return cngDataCenter().occupationBus();
  }
  
  /**
   * Retourne une instance du bus de la gestion des alertes
   * <code>CngAlerteBus</code>.
   */
  public CngAlerteBus alerteBus() {
    return cngDataCenter().alerteBus();
  }
  
  // gestion du temps d'execution des operations
  private Hashtable durations = new Hashtable();
  
  /**
   * le temps d'execution de la derniere transaction en ms
   */
  private String transactionDuration(Integer transId) {
    StringBuffer txt = new StringBuffer();
    txt.append("Transaction #"+transId+" duration : ");
    Long startTime = (Long) durations.get(transId);
    if (startTime != null) {
      txt.append((System.currentTimeMillis()-startTime.longValue())+"ms");
      // supprimer l'entree
      durations.remove(transId);
    } else {
      txt.append("unknown");
    }
    return txt.toString();
  }
  
  public Integer beginECTransaction() {
    Integer transId = super.beginECTransaction();
    durations.put(transId, new Long(System.currentTimeMillis()));
    return transId;
  }
  
  public boolean commitECTrancsacition(Integer transId) {
    boolean transResult = super.commitECTrancsacition(transId);
    LRLog.rawLog(transactionDuration(transId), 2);
    return transResult;
  }
  
  public void rollbackECTrancsacition(Integer transId) {
    super.rollbackECTrancsacition(transId);
    LRLog.rawLog(transactionDuration(transId), 2);
  }

  /**
   * Le context qui a servit a l'instanciation de l'objet
   */
	public EOEditingContext getEc() {
		return ec;
	}

}
