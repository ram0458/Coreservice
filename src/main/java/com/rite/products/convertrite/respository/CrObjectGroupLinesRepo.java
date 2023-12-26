package com.rite.products.convertrite.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rite.products.convertrite.model.CrObjectGroupLines;
import com.rite.products.convertrite.model.CrObjectGroupLinesView;

public interface CrObjectGroupLinesRepo extends JpaRepository<CrObjectGroupLines, Long>{
	
	@Query("SELECT C FROM CrObjectGroupLines C WHERE C.groupId = :groupId")
	public List<CrObjectGroupLines> getobjectLinesByGroupId(Long groupId);
	
	@Query("Delete  from CrObjectGroupLines C where C.groupId = :groupId")
	public void deleteAllLinesByGroupId(Long groupId);
	
	@Query("SELECT C FROM CrObjectGroupLinesView C WHERE C.groupId = :groupId")
	public List<CrObjectGroupLinesView> getObjectLinesViewByGroupId(Long groupId);

	@Query("select groupId from  CrObjectGroupLines where  objectId=:objectId")
	Long getGroupIdbyObjectId(Long objectId);
}
