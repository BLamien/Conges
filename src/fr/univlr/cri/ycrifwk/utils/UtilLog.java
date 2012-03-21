/*
 * Copyright Universit� de La Rochelle 2005
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
package fr.univlr.cri.ycrifwk.utils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import fr.univlr.cri.util.StringCtrl;
import fr.univlr.cri.webapp.LRLog;

/**
 * @author ctarade 9 mai 2005
 *
 */
public class UtilLog {
    
    private static class StackTrace {
    
	    public static void displayStack(int nbLignes) {

	        if (nbLignes == 0) {
	            return;
	        }
	        
	        StringWriter sw = new StringWriter();
	        new Throwable("").printStackTrace(new PrintWriter(sw));
	        String stackTrace = sw.toString();
	        
	        // to clean up the stacktrace
	        StringTokenizer st = new StringTokenizer(stackTrace, "\n");

	        String uneLigne = "";
	        // on vire les 3 premieres lignes ...
	        //	       java.lang.Throwable: 
			//	        	at UtilLog$StackTrace.displayStack(UtilLog.java:58)
			//	        	at UtilLog.log(UtilLog.java:75)
;
			uneLigne = st.nextToken();
			uneLigne = st.nextToken();
	        uneLigne = st.nextToken();
	        
	        // on est sur le premier ligne interssante
	        uneLigne = st.nextToken();
	        
	        // on affiche nbLigne
	        while (nbLignes > 0) {
	            uneLigne = StringCtrl.replace(uneLigne, "\r", "");
		        LRLog.log(uneLigne);
		        try {
		            uneLigne = st.nextToken();
			        nbLignes--;    
		        }
		        catch (NoSuchElementException e) {
		            nbLignes = 0;
		        }
	        }
	    }
    }
	    
    public static void log(Object o) {
        log(0, o);
    }
    
    public static void log(int taille, Object o) {
        StackTrace.displayStack(taille);
        String chaine = "null";
        if (o != null) {
            chaine = (String) o;
        }
        LRLog.log(chaine);
        LRLog.log("-----------------------------------------");
    }
    
}
