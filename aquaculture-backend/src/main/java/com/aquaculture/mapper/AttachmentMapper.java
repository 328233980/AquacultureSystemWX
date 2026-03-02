package com.aquaculture.mapper;

import com.aquaculture.entity.Attachment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AttachmentMapper {
    
    @Select("SELECT * FROM attachment WHERE id = #{id}")
    Attachment findById(Long id);
    
    @Select("SELECT * FROM attachment WHERE related_type = #{relatedType} AND related_id = #{relatedId}")
    List<Attachment> findByRelated(@Param("relatedType") String relatedType, @Param("relatedId") Long relatedId);
    
    @Insert("INSERT INTO attachment (related_type, related_id, file_name, file_path, file_type, file_size, upload_by, created_at) " +
            "VALUES (#{relatedType}, #{relatedId}, #{fileName}, #{filePath}, #{fileType}, #{fileSize}, #{uploadBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Attachment attachment);
    
    @Delete("DELETE FROM attachment WHERE id = #{id}")
    int deleteById(Long id);
    
    @Delete("DELETE FROM attachment WHERE related_type = #{relatedType} AND related_id = #{relatedId}")
    int deleteByRelated(@Param("relatedType") String relatedType, @Param("relatedId") Long relatedId);
}
