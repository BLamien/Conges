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


package fr.univlr.cri.conges.objects.finder;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import fr.univlr.cri.conges.eos.modele.grhum.EOStructure;
import fr.univlr.cri.conges.objects.Responsabilite;

/**
 * @author ctarade
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FinderResponsabilite {

	/**
	 * permet de trouver l'objet responsabilite d'une structure dans un tableau
	 * @param array
	 * @param structure
	 * @return
	 */
	public static Responsabilite findResponsabiliteForStructureInArray(NSArray array, EOStructure structure) {
		EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("structure = %@", new NSArray(structure));
		NSArray responsabilites = EOQualifier.filteredArrayWithQualifier(array, qual);
		if (responsabilites.count() > 0)
			return (Responsabilite)responsabilites.objectAtIndex(0);
		else
			return null;
	}

}
