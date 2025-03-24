package org.pageflow.file.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.common.result.Result;
import org.pageflow.file.entity.FileData;
import org.pageflow.file.model.FilePath;
import org.pageflow.file.model.FileUploadCmd;
import org.pageflow.file.repository.FileDataJpaRepository;
import org.pageflow.file.shared.FileCode;
import org.pageflow.file.shared.FileType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
  private final FileDataJpaRepository repository;
  private final List<ImageFileValidatior> ImageFileValidatior;


  public FilePath upload(FileUploadCmd cmd) {
    String originalFilename = cmd.getFile().getOriginalFilename();
    assert originalFilename != null; // UploadCmd에서 이미 Runtime 검증

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
      .ownerId(cmd.getFileIdentity().getOwnerId())
      .ownerType(cmd.getFileIdentity().getFileType())
      .fileType(cmd.getFileIdentity().getFileType().name())
      .staticParent(filePath.getStaticParent())
      .build();
    repository.persist(fileData);

    try {
      File file = new File(filePath.getFullPath());
      File parent = file.getParentFile();

      // 파일을 저장할 디렉토리가 없다면 생성
      if(!parent.exists()) {
        boolean mkdirSuccess = parent.mkdirs();
        Assert.state(mkdirSuccess, "파일을 저장할 디렉토리를 생성하는데 실패했습니다.");
      }
      // 파일 저장
      cmd.getFile().transferTo(file);
    } catch(Exception e) {
      throw new FileProcessingException("파일을 저장하는데 실패했습니다.", e);
    }
    return fileData.getFilePath();
  }


  public List<FileData> findAll(String ownerId, FileType type) {
    return repository.findAll(ownerId, type);
  }

  /**
   * @code FAIL_TO_DELETE_FILE: 파일 삭제에 실패시
   */
  public Result delete(FileData fileData) {
    return _deleteByFilePath(fileData.getFilePath());
  }

  /**
   * @code FAIL_TO_DELETE_FILE: 파일 삭제에 실패시
   */
  public Result delete(FilePath path) {
    return _deleteByFilePath(path);
  }

  public void deleteAll(String ownerId, FileType ownerType) {
    repository.deleteAll(ownerId, ownerType);
  }

  /**
   * @code FAIL_TO_DELETE_FILE: 파일 삭제에 실패시
   */
  private Result _deleteByFilePath(FilePath filePath) {
    File file = new File(filePath.getFullPath());
    boolean isSuccess = file.delete();
    if(isSuccess) {
      repository.deleteById(filePath.getFilename());
      return Result.success();
    } else {
      return Result.of(FileCode.FAIL_TO_DELETE_FILE, filePath.getStaticPath());
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
