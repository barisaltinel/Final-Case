    package com.patika.bootcamp.taskmanagement.model;

    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder  // 📌 Builder pattern ekledim, nesne oluşturmayı kolaylaştırır.
    @Table(name = "attachments")
    public class Attachment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String fileName;

        @Column(nullable = false, unique = true) // 📌 Dosya yolu benzersiz olmalı
        private String filePath;

        @Column(nullable = false)
        private String mimeType;  // 📌 Dosya türü (Örn: image/png, application/pdf)

        @Column(nullable = false)
        private Long fileSize;  // 📌 Dosya boyutu (byte cinsinden)

        @ManyToOne(fetch = FetchType.LAZY) // 📌 Gereksiz veriyi yüklememek için LAZY kullanıldı.
        @JoinColumn(name = "task_id", nullable = false)
        private Task task;

        @Column(nullable = false, updatable = false)
        private LocalDateTime uploadedAt;

        @Column(nullable = false)
        private boolean deleted;  // 📌 Soft delete için alan eklendi

        /** 🚀 Soft delete işlemi için yardımcı metod */
        public void markAsDeleted() {
            this.deleted = true;
        }

        /** 📌 Yeni dosya oluştururken kullanılacak yardımcı factory metodu */
        public static Attachment createNewAttachment(String fileName, String filePath, String mimeType, Long fileSize, Task task) {
            return Attachment.builder()
                    .fileName(fileName)
                    .filePath(filePath)
                    .mimeType(mimeType)
                    .fileSize(fileSize)
                    .task(task)
                    .uploadedAt(LocalDateTime.now())
                    .deleted(false)
                    .build();
        }
    }
