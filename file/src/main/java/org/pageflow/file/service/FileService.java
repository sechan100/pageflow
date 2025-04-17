package org.pageflow.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.ResultException;
import org.pageflow.file.entity.FileData;
import org.pageflow.file.model.FilePath;
import org.pageflow.file.model.FileUploadCmd;
import org.pageflow.file.repository.FileDataJpaRepository;
import org.pageflow.file.shared.FileCode;
import org.pageflow.file.shared.FileType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
  private final FileDataJpaRepository repository;
  private final List<FileValidator> fileValidators;


  /**
   * @code FAIL_TO_UPLOAD_FILE: 파일 업로드에 실패한 경우
   * @code 그 외 FileValidator에 따라서 다양한 ResultCode 발생 가능
   */
  public FilePath upload(FileUploadCmd cmd) {
    // 파일 유효성 검사 ===============================
    Result fileValidationResult = fileValidators.stream()
      .filter(v -> v.accept(cmd.getFileType()))
      .findFirst()
      .map(validator -> validator.validateFile(cmd.getFile()))
      .orElse(Result.ok());
    if(fileValidationResult.isFailure()) {
      throw new ResultException(fileValidationResult);
    }

    // 파일 데이터 엔티티 저장 ====================================
    String originalFilename = cmd.getFile().getOriginalFilename();
    assert originalFilename != null;

    FilePath filePath = new FilePath(
      getDailyStaticParent(),
      UUID.randomUUID(),
      _extractExtension(originalFilename)
    );

    FileData fileData = FileData.builder()
      .originalFilename(originalFilename)
      .filename(filePath.getFilename())
      .extension(filePath.getExtension())
      .size(cmd.getFile().getSize())
      .ownerId(cmd.getOwnerId())
      .fileType(cmd.getFileType())
      .staticParent(filePath.getStaticParent())
      .build();
    repository.persist(fileData);

    // 실제 파일 저장 ===========================
    File file = new File(filePath.getFullPath());
    File parent = file.getParentFile();
    // 파일을 저장할 디렉토리가 없다면 생성
    if(!parent.exists()) {
      boolean mkdirSuccess = parent.mkdirs();
      if(!mkdirSuccess) {
        log.error("파일을 저장할 디렉토리를 생성하는데 실패했습니다. parent: {}, fileData: {}", parent, fileData);
        throw new ResultException(FileCode.FAIL_TO_UPLOAD_FILE, originalFilename);
      }
    }
    // 파일 저장
    try {
      cmd.getFile().transferTo(file);
      return filePath;
    } catch(Exception e) {
      log.error("파일 저장에 실패했습니다. fileData: {}", fileData, e);
      throw new ResultException(FileCode.FAIL_TO_UPLOAD_FILE, originalFilename);
    }
  }


  public List<FileData> findAll(String ownerId, FileType type) {
    return repository.findAll(ownerId, type);
  }

  /**
   * @code FAIL_TO_DELETE_FILE: 파일 삭제에 실패시
   */
  public void delete(FileData fileData) {
    _deleteByFilePath(fileData.getFilePath());
  }

  /**
   * @code FAIL_TO_DELETE_FILE: 파일 삭제에 실패시
   */
  public void delete(FilePath path) {
    _deleteByFilePath(path);
  }

  public void deleteAll(String ownerId, FileType ownerType) {
    List<FileData> fileDatas = repository.findAllByOwnerIdAndFileType(ownerId, ownerType);
    for(FileData fileData : fileDatas) {
      _deleteByFilePath(fileData.getFilePath());
    }
  }

  /**
   * @code FAIL_TO_DELETE_FILE: 파일 삭제에 실패시
   */
  private void _deleteByFilePath(FilePath filePath) {
    File file = new File(filePath.getFullPath());
    boolean isSuccess = file.delete();
    if(isSuccess) {
      repository.deleteById(filePath.getFilename());
    } else {
      throw new ResultException(FileCode.FAIL_TO_DELETE_FILE, filePath.getStaticPath());
    }
  }


  private String _extractExtension(String filename) {
    String extension = filename.substring(filename.lastIndexOf('.') + 1);
    if(extension.isEmpty()) {
      throw new IllegalArgumentException("파일명에 확장자가 없습니다.");
    } else {
      return extension;
    }
  }

  /**
   * 당일 날짜를 기반으로 상대경로를 생성
   *
   * @return /{YYYY}/{MM}/{DD}
   */
  private String getDailyStaticParent() {
    LocalDateTime now = LocalDateTime.now();
    return "/" + now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth();
  }

}
