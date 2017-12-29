package org.iglooproject.showcase.core.business.fileupload.dao;

import org.springframework.stereotype.Repository;

import org.iglooproject.jpa.business.generic.dao.GenericEntityDaoImpl;
import org.iglooproject.showcase.core.business.fileupload.model.ShowcaseFile;

@Repository("showcaseFileDao")
public class ShowcaseFileDaoImpl extends GenericEntityDaoImpl<Long, ShowcaseFile> implements IShowcaseFileDao {

}
