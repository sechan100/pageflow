package org.pageflow.boundedcontext.file.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.file.model.FileIdentity;
import org.pageflow.boundedcontext.file.model.FilePath;
import org.pageflow.boundedcontext.file.model.FileUploadCmd;
import org.pageflow.boundedcontext.file.persistence.FileData;
import org.pageflow.boundedcontext.file.persistence.FileDataJpaRepository;
import org.pageflow.boundedcontext.file.shared.FileOwnerType;
import org.pageflow.boundedcontext.file.shared.FileProcessingException;
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


  public FilePath upload(final FileUploadCmd cmd) {
    String originalFilename = cmd.getFile().getOriginalFilename();
    assert originalFilename!=null; // UploadCmd에서 이미 Runtime 검증

    FilePath filePath = new FilePath(
      getDailyStaticParent(),
      UUID.randomUUID(),
      extractExtension(originalFilename)
    );

    FileData fileData = FileData.builder()
      .originalFilename(originalFilename)
      .managedFilename(filePath.getFilename())
      .extension(filePath.getExtension())
      .size(cmd.getFile().getSize())
      .ownerId(cmd.getFileIdentity().getOwnerId())
      .ownerType(cmd.getFileIdentity().getFileOwnerType())
      .fileType(cmd.getFileIdentity().getFileType().name())
      .staticParent(filePath.getStaticParent())
      .build();
    repository.persist(fileData);

    try {
      File file = new File(filePath.getFullPath());
      File parent = file.getParentFile();

      // 파일을 저장할 디렉토리가 없다면 생성
      if(!parent.exists()){
        boolean mkdirSuccess = parent.mkdirs();
        Assert.state(mkdirSuccess, "파일을 저장할 디렉토리를 생성하는데 실패했습니다.");
      }
      // 파일 저장
      cmd.getFile().transferTo(file);
    } catch(Exception e){
      throw new FileProcessingException("파일을 저장하는데 실패했습니다.", e);
    }
    return fileData.toStaticPath();
  }


  public List<FileData> findAll(FileIdentity identity) {
    return repository.findAll(identity);
  }


  public void delete(FileData fileData) {
    deleteByFilePath(fileData.toStaticPath());
  }

  /**
   * @param webUrl /{webUrl}/{YYYY}/{MM}/{DD}/{UUID}.{ext}
   */
  public void delete(String webUrl) {
    FilePath filePath = FilePath.fromWebUrl(webUrl);
    deleteByFilePath(filePath);
  }

  public void deleteAll(FileIdentity identity) {
    repository.deleteAll(identity);
  }

  public void deleteAll(UUID ownerId, FileOwnerType ownerType) {
    repository.deleteAll(ownerId, ownerType);
  }

  private void deleteByFilePath(FilePath filePath) {
    File file = new File(filePath.getFullPath());
    boolean isSuccess = file.delete();
    if(isSuccess){
      repository.deleteById(filePath.getFilename());
    } else {
      throw new FileProcessingException("파일을 삭제하는데 실패했습니다.");
    }
  }


  private String extractExtension(String filename) {
    String extension = filename.substring(filename.lastIndexOf('.') + 1);
    if(extension.isEmpty()){
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
