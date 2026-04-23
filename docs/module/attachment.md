# Attachment Module

## Overview
Manages file uploads, storage, and lifecycle. Handles creation, retrieval, and deletion of attachments with support for multiple file types and metadata.

## Domain

### `Attachment` Entity
- **Purpose**: Represents a file with metadata (description, MIME type, size, validity period)
- **Type field**: Categorizes attachment purpose (e.g., invoice, contract, image)
- **Validity**: Optional time-based validity window (validFrom, validTo)
- **Primary flag**: Marks primary attachment among multiple files
- **Audit fields**: createdAt, updatedAt

### `AttachmentRepository` Interface
- **Purpose**: Data access abstraction for Attachment persistence
- **Methods**: findById, save, delete

## Application

### `AttachmentService`
- **createAttachment**: Upload file, store it, and create metadata record
- **deleteAttachment**: Remove file and metadata
- **getAttachmentById**: Retrieve attachment metadata
- **getAttachmentFile**: Download file bytes by reference

## API

### `AttachmentController`
- **POST** `/api/attachment` - Upload file (multipart/form-data)
  - Parameters: file, type, description (optional)
- **GET** `/api/attachment/{id}` - Get attachment metadata
- **DELETE** `/api/attachment/{id}` - Delete attachment

### `AttachmentApiMapper`
- Maps between domain entities and DTOs

### `AttachmentDto`
- API representation of Attachment

## Infrastructure

### Storage (`storage/`)
- **FileStorageService**: Abstract file storage operations (store, delete, retrieve)
- Handles physical file management independent of persistence layer

### Persistence (`persistence/`)
- Database mapping and ORM configuration
- JPA entities and database access implementations

## Key Features
- Multipart file upload support
- Automatic MIME type detection
- File validation and storage abstraction
- Metadata tracking (size, type, description)
- Temporal validity support
- Primary attachment designation

## Typical Workflow
1. Client uploads file via POST endpoint
2. Service stores file and creates metadata record
3. Returns attachment metadata (id, href, type)
4. Client can retrieve file via href or download via GET endpoint
5. Deletion removes both metadata and file storage
