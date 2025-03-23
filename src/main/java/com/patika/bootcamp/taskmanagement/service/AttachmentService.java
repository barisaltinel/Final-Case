package com.patika.bootcamp.taskmanagement.service;

import com.patika.bootcamp.taskmanagement.service.exception.AttachmentNotFoundException;
import com.patika.bootcamp.taskmanagement.model.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    /** ✅ Sadece silinmemiş (deleted = false) dosyaları getir */
    List<Attachment> getAllAttachments();

    /** ✅ Sadece silinmemiş (deleted = false) dosyayı getir */
    Attachment findById(Long id) throws AttachmentNotFoundException;

    /** ✅ Yeni bir dosya yükle */
    Attachment upload(MultipartFile file, Long taskId);

    /** ✅ Dosyayı güncelle */
    Attachment update(Long id, Attachment updatedAttachment);

    /** ✅ Soft delete: Dosyayı tamamen silmek yerine pasif hale getir */
    void softDelete(Long id);
}
