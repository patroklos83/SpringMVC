package com.patroclos.repository;

import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Component;

import com.patroclos.model.BaseO;

@Component("AuditEnversRepository")
public class AuditEnversRepository extends Repository {

	public List<Object[]> getEntityRevisionsByProcessId(Class<? extends BaseO> o, Long id, String processId) {
		AuditReader auditReader = AuditReaderFactory.get(em);
		@SuppressWarnings("unchecked")
		List<Object[]> revResults  = auditReader.createQuery()
		.forRevisionsOfEntityWithChanges(o, false)	
		.add(AuditEntity.property("lastUpdatedByprocessId").eq(processId))
		.add(AuditEntity.property("id").eq(id))
		.getResultList();		
		return revResults;
	}
	
//	// get nested relations many/many
//	public List<Object[]> getEntityRevisionsNested(Class<? extends BaseO> o, Long id, String processId) {
//		AuditReader auditReader = AuditReaderFactory.get(em);
//		@SuppressWarnings("unchecked")
//		List<Object[]> revResults  = auditReader.createQuery()
//		.forRevisionsOfEntity(o, true, true)		
//		.add(AuditEntity.property("lastUpdatedByprocessId").eq(processId))
//		.add(AuditEntity.property("id").eq(id))
//		.traverseRelation("citations", JoinType.LEFT)
//		.getResultList();		
	
	
//	var l = AuditReaderFactory.get(em )
//	.createQuery()
//	.forEntitiesAtRevision(Article.class, 1)
//	.traverseRelation("citations", JoinType.INNER)
//	.getResultList();

//		return revResults;
//	}

	public List<Object[]> getEntityRevisionsBeforeRevision(Class<? extends BaseO> o, Long id, int revisionNumber) {
		AuditReader auditReader = AuditReaderFactory.get(em);
		@SuppressWarnings("unchecked")
		List<Object[]> revResults  = auditReader.createQuery()
		.forRevisionsOfEntityWithChanges(o, false)
		.add(AuditEntity.revisionNumber().lt(revisionNumber))
		.add(AuditEntity.property("id").eq(id))
		.getResultList();	
		return revResults;
	}

	public Object getEntityAtRevision(Class<? extends BaseO> o, Long id, int revisionNumber) {
		AuditReader auditReader = AuditReaderFactory.get(em);
		Object revResults  = auditReader.find(o, id, revisionNumber);	
		return revResults;
	}

	public Object countEntityRevisions(Class<? extends BaseO> o, Long id) {
		AuditReader auditReader = AuditReaderFactory.get(em);
		return auditReader.createQuery()
				.forRevisionsOfEntity(o, false, true)
				.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();
	}
}
