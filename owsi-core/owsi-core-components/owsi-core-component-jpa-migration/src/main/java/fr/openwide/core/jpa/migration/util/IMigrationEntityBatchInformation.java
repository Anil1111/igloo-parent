package fr.openwide.core.jpa.migration.util;

import java.util.Map;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.migration.rowmapper.AbstractEntityRowMapper;

public interface IMigrationEntityBatchInformation<T extends GenericEntity<Long, T>> {

	Class<T> getEntityClass();

	Class<? extends AbstractEntityRowMapper<?>> getRowMapperClass();

	String getSqlCountRows();

	String getSqlAllIds();

	/*
	 * Chaîne utilisée dans le IN() de la requête SQL
	 */
	String getParameterIds();

	String getSqlRequest();

	/*
	 * Map utilisée pour le préchargement d'entités liées pour accélérer la migration.
	 * La clé est la classe des entités à précharger.
	 * La valeur est : 
	 * 		- soit la requête SQL associée
	 * 		- soit null dans le cas où les entités sont déjà préchargées, auquel cas on se contentera d'appeler
	 * 		la méthode listEntitiesByIds de la classe AbstractEntityMigrationService
	 */
	Map<Class<? extends GenericEntity<Long, ?>>, String> getPreloadRequests();

}
